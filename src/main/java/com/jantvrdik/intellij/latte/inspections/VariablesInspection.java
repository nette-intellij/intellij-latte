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
import com.jantvrdik.intellij.latte.utils.LattePhpVariableDefinition;
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
			public void visitElement(@NotNull PsiElement element) {
				if (element instanceof LattePhpVariable) {
					String variableName = ((LattePhpVariable) element).getVariableName();
					PsiElement variableContext = ((LattePhpVariable) element).getVariableContext();
					if (((LattePhpVariable) element).isDefinition()) {
						VirtualFile file = element.getContainingFile().getVirtualFile();
						List<PsiPositionedElement> all = LatteUtil.findVariablesInFile(element.getProject(), file, variableName);
						List<PsiPositionedElement> afterElement = LatteUtil.findVariablesInFileAfterElement(element, file, variableName);
						List<PsiPositionedElement> definitions = all.stream()
								.filter(variableElement -> variableElement.getElement() != null && variableElement.getElement().isDefinition())
								.collect(Collectors.toList());

						List<PsiPositionedElement> usages = afterElement.stream()
								.filter(variableElement -> variableElement.getElement() != null && !variableElement.getElement().isDefinition())
								.collect(Collectors.toList());
						List<PsiPositionedElement> varDefinitions = definitions.stream()
								.filter(variableElement -> variableElement.getElement() != null && !variableElement.getElement().isVarTypeDefinition())
								.collect(Collectors.toList());

						if (varDefinitions.size() > 0 && !((LattePhpVariable) element).isVarTypeDefinition()) {
							LatteVariableSettings defaultVariable = LatteConfiguration.getInstance(element.getProject()).getVariable(variableName);
							if (defaultVariable != null) {
								addProblem(manager, problems, element, "Rewrite default variable '" + variableName + "' defined as template parameters", isOnTheFly);
							}
						}

						if (!((LattePhpVariable) element).isVarTypeDefinition()) {
							for (PsiPositionedElement varDefinition : varDefinitions) {
								if (varDefinition.getElement() != element && varDefinition.getElement().getVariableContext() == variableContext) {
									addProblem(manager, problems, element, "Multiple definitions for variable '" + variableName + "'", isOnTheFly);
									break;
								}
							}
						}

						if (usages.size() == 0) {
							addUnused(manager, problems, element, "Unused variable '" + variableName + "'", isOnTheFly);
						}

					} else {
						List<LattePhpVariableDefinition> variableDefinitions = LatteUtil.getVariableDefinition((LattePhpVariable) element);

						boolean isDefined = false;
						boolean isProbablyUndefined = false;
						for (LattePhpVariableDefinition variableDefinition : variableDefinitions) {
							if (!variableDefinition.isProbablyUndefined()) {
								isDefined = true;
							} else {
								isProbablyUndefined = true;
							}
						}

						if (!isDefined) {
							LatteVariableSettings defaultVariable = LatteConfiguration.getInstance(element.getProject()).getVariable(variableName);
							if (defaultVariable != null) {
								isDefined = true;
								isProbablyUndefined = false;

							} else {
								List<Field> fields = LattePhpVariableUtil.findPhpFiledListFromTemplateTypeTag(element, variableName);
								if (fields.size() > 0) {
									isDefined = true;
									isProbablyUndefined = false;
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

						if (!isDefined && isProbablyUndefined) {
							addWeakWarning(manager, problems, element, "Variable '" + variableName + "' is probably undefined", isOnTheFly);

						} else if (!isDefined) {
							LocalQuickFix notNullFix = IntentionManager.getInstance().convertToFix(new AddCustomNullableVariable(variableName));
							LocalQuickFix notNotNullFix = IntentionManager.getInstance().convertToFix(new AddCustomNotNullVariable(variableName));
							problems.add(
								manager.createProblemDescriptor(
									element,
									"Undefined variable '" + variableName + "'",
									true,
									ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
									isOnTheFly,
									notNotNullFix,
									notNullFix
								)
							);
						}
					}

				} else {
					super.visitElement(element);
				}
			}
		});

		return problems.toArray(new ProblemDescriptor[0]);
	}
}
