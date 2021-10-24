package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.inspections.utils.LatteInspectionInfo;
import com.jantvrdik.intellij.latte.intentions.AddCustomNotNullVariable;
import com.jantvrdik.intellij.latte.intentions.AddCustomNullableVariable;
import com.jantvrdik.intellij.latte.php.LattePhpVariableUtil;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpVariableElement;
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;
import com.jantvrdik.intellij.latte.utils.LattePhpCachedVariable;
import com.jetbrains.php.lang.psi.elements.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
		addInspections(manager, problems, checkFile(file), isOnTheFly);
		return problems.toArray(new ProblemDescriptor[0]);
	}

	@NotNull
	List<LatteInspectionInfo> checkFile(@NotNull final PsiFile file) {
		final List<LatteInspectionInfo> problems = new ArrayList<>();
		Map<PsiElement, LattePhpCachedVariable> all = LattePhpVariableUtil.getAllVariablesInFile(file);
		file.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
			@Override
			public void visitElement(@NotNull PsiElement psiElement) {
				if (psiElement instanceof LattePhpVariable) {
					LattePhpCachedVariable element = all.get(psiElement);
					if (element == null) {
						super.visitElement(psiElement);
						return;
					}

					if (element.isDefinition()) {
						List<LattePhpCachedVariable> sameName = all.values().stream()
								.filter((current) -> current.getVariableName() != null && current.getVariableName().equals(element.getVariableName()))
								.collect(Collectors.toList());
						checkVariableDefinition(sameName, element, problems);
					} else {
						checkVariableUsages(element, problems);
					}

				} else {
					super.visitElement(psiElement);
				}
			}
		});
		return problems;
	}

	private void checkVariableDefinition(
			@NotNull final List<LattePhpCachedVariable> sameName,
			@NotNull final LattePhpCachedVariable element,
			@NotNull final List<LatteInspectionInfo> problems
	) {
		List<LattePhpCachedVariable> definitions = sameName.stream()
				.filter(current -> current.isDefinition() && !current.isVarTypeDefinition())
				.collect(Collectors.toList());
		List<LattePhpCachedVariable> usages = sameName.stream()
				.filter((current) -> !current.isDefinition() && current.getPosition() >= element.getPosition())
				.collect(Collectors.toList());

		LattePhpVariableElement variable = element.getElement();
		String variableName = element.getVariableName();
		if (!element.isVarTypeDefinition()) {
			LatteVariableSettings defaultVariable = LatteConfiguration.getInstance(element.getProject()).getVariable(variableName);
			if (defaultVariable != null) {
				problems.add(
					LatteInspectionInfo.error(variable, "Rewrite default variable '" + variableName + "' defined as template parameters")
				);
			}

			for (LattePhpCachedVariable varDefinition : definitions) {
				if (!varDefinition.matchElement(element) && varDefinition.getVariableContext() == element.getVariableContext()) {
					problems.add(
						LatteInspectionInfo.warning(variable, "Multiple definitions for variable '" + variableName + "'")
					);
					break;
				}
			}
		}

		boolean isUsed = false;
		if (usages.size() > 0) {
			for (LattePhpCachedVariable usage : usages) {
				if (usage.getVariableDefinitions().contains(variable)) {
					isUsed = true;
					break;
				}
			}
		}

		if (!isUsed) {
			problems.add(LatteInspectionInfo.unused(variable, "Unused variable '" + variableName + "'"));
		}
	}

	private void checkVariableUsages(
			@NotNull final LattePhpCachedVariable element,
			@NotNull final List<LatteInspectionInfo> problems
	) {
		LattePhpVariableElement variable = element.getElement();
		LatteFile file = variable.getLatteFile();
		if (file == null) {
			return;
		}

		List<LattePhpCachedVariable> variableDefinitions = file.getCachedVariableDefinitions(variable);
		boolean isDefined = false;
		boolean isProbablyUndefined = false;
		for (LattePhpCachedVariable variableDefinition : variableDefinitions) {
			if (!variableDefinition.isProbablyUndefined()) {
				isDefined = true;
			} else {
				isProbablyUndefined = true;
			}
		}

		String variableName = element.getVariableName();
		if (!isDefined) {
			LatteVariableSettings defaultVariable = LatteConfiguration.getInstance(element.getProject()).getVariable(variableName);
			if (defaultVariable != null) {
				isDefined = true;
				isProbablyUndefined = false;

			} else {
				List<Field> fields = LattePhpVariableUtil.findPhpFiledListFromTemplateTypeTag(variable, variableName);
				if (fields.size() > 0) {
					isDefined = true;
					isProbablyUndefined = false;
					for (Field field : fields) {
						if (field.isDeprecated()) {
							problems.add(LatteInspectionInfo.deprecated(variable, "Variable '" + variableName + "' is deprecated"));
						}
						if (field.isInternal()) {
							problems.add(LatteInspectionInfo.deprecated(variable, "Variable '" + variableName + "' is internal"));
						}
					}
				}
			}
		}

		if (!isDefined && isProbablyUndefined) {
			problems.add(LatteInspectionInfo.weakWarning(variable, "Variable '" + variableName + "' is probably undefined"));

		} else if (!isDefined) {
			LatteInspectionInfo info = LatteInspectionInfo.error(variable, "Undefined variable '" + variableName + "'");
			IntentionManager intentionManager = IntentionManager.getInstance();
			if (intentionManager != null) {
				info.addFix(intentionManager.convertToFix(new AddCustomNullableVariable(variableName)));
				info.addFix(intentionManager.convertToFix(new AddCustomNotNullVariable(variableName)));
			}
			problems.add(info);
		}
	}

}
