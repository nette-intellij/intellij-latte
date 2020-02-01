package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.utils.LatteTypesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MacroVarTypeInspection extends LocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "LatteVarType";
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
				if (element instanceof LatteMacroTag && ((LatteMacroTag) element).getMacroName().equals("varType")) {
					LatteMacroContent macroContent = PsiTreeUtil.findChildOfType(element, LatteMacroContent.class);
					if (macroContent != null) {
						List<LattePhpContent> phpContent = new ArrayList<>(macroContent.getPhpContentList());

						if (phpContent.size() == 0) {
							addError("Tag {varType} must have php content.", problems, element, manager, isOnTheFly);

						} else {
							Result result = new Result();
							for (LattePhpContent content : phpContent) {
								content.accept(new PsiRecursiveElementWalkingVisitor() {
									@Override
									public void visitElement(PsiElement element) {
										IElementType type = element.getNode().getElementType();
										if (LatteTypesUtil.getAllSkippedHintTokens().contains(type)) {

										} else if (LatteTypesUtil.getTypeHintTokens().contains(type)) {
											if (!result.hasTypeFirst) {
												result.hasTypeFirst = true;
											}

										} else if (type == LatteTypes.PHP_VARIABLE) {
											result.variableCount++;

										} else {
											super.visitElement(element);
											if (type != LatteTypes.PHP_CONTENT) {
												result.otherCount++;
											}
										}
									}
								});
							}

							if (!result.hasTypeFirst) {
								addError("First value in {varType} tag must be type hint.", problems, element, manager, isOnTheFly);
							}

							if (result.variableCount == 0) {
								addError("Last value in {varType} tag must be variable.", problems, element, manager, isOnTheFly);

							} else if (result.variableCount > 1) {
								addError("Only one variable can be defined in {varType} tag.", problems, element, manager, isOnTheFly);
							}

							if (result.otherCount > 0) {
								addError("Invalid content in {varType} tag.", problems, element, manager, isOnTheFly);
							}
						}


					}

				} else {
					super.visitElement(element);
				}
			}
		});

		return problems.toArray(new ProblemDescriptor[problems.size()]);
	}

	private void addError(String message, List<ProblemDescriptor> problems, @NotNull PsiElement element, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
		ProblemDescriptor problem = manager.createProblemDescriptor(
				element,
				message,
				true,
				ProblemHighlightType.GENERIC_ERROR,
				isOnTheFly
		);
		problems.add(problem);
	}

	private static class Result {
		boolean hasTypeFirst = false;
		int variableCount = 0;
		int otherCount = 0;
	}
}
