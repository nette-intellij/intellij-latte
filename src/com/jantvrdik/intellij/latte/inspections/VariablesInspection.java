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
import com.jantvrdik.intellij.latte.psi.impl.LattePsiImplUtil;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jantvrdik.intellij.latte.utils.PsiPositionedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VariablesInspection extends LocalInspectionTool {

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

		final List<ProblemDescriptor> problems = new ArrayList<ProblemDescriptor>();
		file.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
			@Override
			public void visitElement(PsiElement element) {
				if (element instanceof LattePhpVariable) {
					String variableName = ((LattePhpVariable) element).getVariableName();
					VirtualFile file = element.getContainingFile().getVirtualFile();
					List<PsiPositionedElement> all = LatteUtil.findVariablesInFile(element.getProject(), file, variableName);
					List<PsiPositionedElement> definitions = all.stream()
							.filter(variableElement -> variableElement.getElement() instanceof LattePhpVariable && ((LattePhpVariable) variableElement.getElement()).isDefinition())
							.collect(Collectors.toList());

					int offset = LatteUtil.getStartOffsetInFile(element);
					List<PsiPositionedElement> beforeElement = definitions.stream()
							.filter(variableElement -> variableElement.getPosition() <= offset)
							.collect(Collectors.toList());
					List<PsiPositionedElement> varDefinitions = definitions.stream()
							.filter(variableElement -> variableElement.getElement() instanceof LattePhpVariable && !((LattePhpVariable) variableElement.getElement()).isVarTypeDefinition())
							.collect(Collectors.toList());

					ProblemHighlightType type = null;
					String description = null;
					boolean isUndefined = false;
					if (((LattePhpVariable) element).isDefinition()) {
						List<PsiPositionedElement> usages = all.stream()
								.filter(
										variableElement -> variableElement.getElement() instanceof LattePhpVariable
										&& !((LattePhpVariable) variableElement.getElement()).isDefinition()
								)
								.collect(Collectors.toList());

						if (varDefinitions.size() > 0 && !((LattePhpVariable) element).isVarTypeDefinition()) {
							LatteVariableSettings defaultVariable = LatteConfiguration.INSTANCE.getVariable(element.getProject(), variableName);
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

						if (varDefinitions.size() > 1) {
							type = ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
							description = "Multiple definitions for variable '" + variableName + "'";

						} else if (usages.size() == 0) {
							type = ProblemHighlightType.LIKE_UNUSED_SYMBOL;
							description = "Unused variable '" + variableName + "'";
						}

					} else if (beforeElement.size() == 0) {
						LatteVariableSettings defaultVariable = LatteConfiguration.INSTANCE.getVariable(element.getProject(), variableName);
						if (defaultVariable == null && LattePsiImplUtil.detectVariableTypeFromTemplateType(element, variableName) == null) {
							type = ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
							description = "Undefined variable '" + variableName + "'";
							isUndefined = true;
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

		return problems.toArray(new ProblemDescriptor[problems.size()]);
	}
}
