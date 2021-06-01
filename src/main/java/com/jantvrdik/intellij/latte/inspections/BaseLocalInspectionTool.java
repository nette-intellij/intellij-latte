package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.inspections.utils.LatteInspectionInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

abstract class BaseLocalInspectionTool extends LocalInspectionTool {

	protected void addInspections(
			@NotNull final InspectionManager manager,
			@NotNull final List<ProblemDescriptor> problems,
			@NotNull final List<LatteInspectionInfo> inspectionInfo,
			final boolean isOnTheFly
	) {
		for (LatteInspectionInfo info : inspectionInfo) {
			ProblemDescriptor problem = manager.createProblemDescriptor(info.getElement(), info.getDescription(), true, info.getType(), isOnTheFly, info.getFixes());
			problems.add(problem);
		}
	}

	protected void addError(
			@NotNull final InspectionManager manager,
			List<ProblemDescriptor> problems,
			@NotNull PsiElement element,
			@NotNull String description,
			boolean isOnTheFly
	) {
		addProblem(manager, problems, element, description, ProblemHighlightType.GENERIC_ERROR, isOnTheFly);
	}

	protected void addProblem(
			@NotNull final InspectionManager manager,
			List<ProblemDescriptor> problems,
			@NotNull PsiElement element,
			@NotNull String description,
			boolean isOnTheFly
	) {
		addProblem(manager, problems, element, description, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly);
	}

	protected void addDeprecated(
			@NotNull final InspectionManager manager,
			List<ProblemDescriptor> problems,
			@NotNull PsiElement element,
			@NotNull String description,
			boolean isOnTheFly
	) {
		addProblem(manager, problems, element, description, ProblemHighlightType.LIKE_DEPRECATED, isOnTheFly);
	}

	private void addProblem(
			@NotNull final InspectionManager manager,
			List<ProblemDescriptor> problems,
			@NotNull PsiElement element,
			@NotNull String description,
			@NotNull ProblemHighlightType type,
			boolean isOnTheFly
	) {
		ProblemDescriptor problem = manager.createProblemDescriptor(element, description, true, type, isOnTheFly);
		problems.add(problem);
	}

	protected void addProblem(
			@NotNull final InspectionManager manager,
			List<ProblemDescriptor> problems,
			@NotNull PsiElement element,
			@NotNull String description,
			boolean isOnTheFly,
			final LocalQuickFix... fixes
	) {
		ProblemDescriptor problem = manager.createProblemDescriptor(element, description, true, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly, fixes);
		problems.add(problem);
	}
}
