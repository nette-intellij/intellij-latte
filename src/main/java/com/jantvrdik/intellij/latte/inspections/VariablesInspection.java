package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.codeInspection.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;
import com.jantvrdik.intellij.latte.intentions.AddCustomNotNullVariable;
import com.jantvrdik.intellij.latte.intentions.AddCustomNullableVariable;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.php.LattePhpVariableUtil;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jantvrdik.intellij.latte.utils.PsiPositionedElement;
import com.jetbrains.php.lang.psi.elements.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VariablesInspection extends BaseLocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "LatteVariablesProblems";
	}

	@Nullable
	@Override
	public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
		if (!(file instanceof LatteFile)) {
			return null;
		}

		final List<ProblemDescriptor> problems = new ArrayList<>();
		file.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
			@Override
			public void visitElement(PsiElement element) {
				if (element instanceof LattePhpVariable) {
					String variableName = ((LattePhpVariable) element).getVariableName();
					VirtualFile file = element.getContainingFile().getVirtualFile();
					List<PsiPositionedElement> all = LatteUtil.findVariablesInFile(element.getProject(), file, variableName);
					List<PsiPositionedElement> afterElement = LatteUtil.findVariablesInFileAfterElement(element, file, variableName);
					List<PsiPositionedElement> definitions = all.stream()
							.filter(variableElement -> variableElement.getElement() instanceof LattePhpVariable && ((LattePhpVariable) variableElement.getElement()).isDefinition())
							.collect(Collectors.toList());

					int offset = LatteUtil.getStartOffsetInFile(element);
					List<PsiPositionedElement> beforeElement = definitions.stream()
							.filter(variableElement -> variableElement.getPosition() <= offset)
							.collect(Collectors.toList());
					int varDefinitions = (int) definitions.stream()
							.filter(variableElement -> variableElement.getElement() instanceof LattePhpVariable && !((LattePhpVariable) variableElement.getElement()).isVarTypeDefinition())
							.count();
					int cyclesDefinitions = (int) definitions.stream()
							.filter(
									variableElement -> variableElement.getElement() instanceof LattePhpVariable
											&& !((LattePhpVariable) variableElement.getElement()).isVarTypeDefinition()
											&& (
												((LattePhpVariable) variableElement.getElement()).isDefinitionInFor()
												|| ((LattePhpVariable) variableElement.getElement()).isDefinitionInForeach()
											)
							).count();
					int normalVarDefinitions =  varDefinitions - cyclesDefinitions;

					ProblemHighlightType type = null;
					String description = null;
					boolean isUndefined = false;
					if (((LattePhpVariable) element).isDefinition()) {
						List<PsiPositionedElement> usages = afterElement.stream()
								.filter(
										variableElement -> variableElement.getElement() instanceof LattePhpVariable
										&& !((LattePhpVariable) variableElement.getElement()).isDefinition()
								)
								.collect(Collectors.toList());

						if (varDefinitions > 0 && !((LattePhpVariable) element).isVarTypeDefinition()) {
							LatteVariableSettings defaultVariable = LatteConfiguration.getInstance(element.getProject()).getVariable(variableName);
							if (defaultVariable != null) {
								ProblemDescriptor descriptor = manager.createProblemDescriptor(
										element,
										"Rewrite default variable '" + variableName + "' defined as template parameters",
										true,
										ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
										isOnTheFly
								);
								problems.add(descriptor);
							}
						}

						if (normalVarDefinitions > 1) {
							type = ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
							description = "Multiple definitions for variable '" + variableName + "'";

						} else if (normalVarDefinitions == 1 && cyclesDefinitions > 0) {
							type = ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
							description = "Multiple definitions for variable '" + variableName + "'. Defined in for/foreach and normally.";

						} else if (usages.size() == 0) {
							type = ProblemHighlightType.LIKE_UNUSED_SYMBOL;
							description = "Unused variable '" + variableName + "'";
						}

					} else if (beforeElement.size() == 0) {
						LatteVariableSettings defaultVariable = LatteConfiguration.getInstance(element.getProject()).getVariable(variableName);
						if (defaultVariable == null) {
							List<Field> fields = LattePhpVariableUtil.findPhpFiledListFromTemplateTypeTag(element, variableName);
							if (fields.size() == 0) {
								type = ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
								description = "Undefined variable '" + variableName + "'";
								isUndefined = true;

							} else {
								for (Field field : fields) {
									if (field.isDeprecated()) {
										addDeprecated(manager, problems, element, "Variable '" + variableName + "' is deprecated", isOnTheFly);
									}
									if (field.isInternal()) {
										addDeprecated(manager, problems, element, "Variable '" + variableName + "' is internal", isOnTheFly);
									}
								}
							}
						}
					}

					if (type != null) {
						ProblemDescriptor problem;
						if (isUndefined) {
							LocalQuickFix notNullFix = IntentionManager.getInstance().convertToFix(new AddCustomNullableVariable(variableName));
							LocalQuickFix notNotNullFix = IntentionManager.getInstance().convertToFix(new AddCustomNotNullVariable(variableName));
							problem = manager.createProblemDescriptor(element, description, true, type, isOnTheFly, notNotNullFix, notNullFix);
						} else {
							problem = manager.createProblemDescriptor(element, description, true, type, isOnTheFly);
						}
						problems.add(problem);
					}

				} else {
					super.visitElement(element);
				}
			}
		});

		return problems.toArray(new ProblemDescriptor[0]);
	}
}
