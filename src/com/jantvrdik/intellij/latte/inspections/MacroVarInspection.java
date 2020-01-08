package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.utils.LatteTypesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MacroVarInspection extends LocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "LatteMacroVar";
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
				if (element instanceof LatteMacroTag && ((LatteMacroTag) element).getMacroName().equals("var")) {
					LatteMacroContent macroContent = PsiTreeUtil.findChildOfType(element, LatteMacroContent.class);
					if (macroContent != null) {
						List<LattePhpContent> phpContent = new ArrayList<>(macroContent.getPhpContentList());

						if (phpContent.size() == 0) {
							addError("Macro 'var' must have php content.", problems, element, manager, isOnTheFly);

						} else {
							Result result = new Result();
							for (LattePhpContent content : phpContent) {
								content.accept(new PsiRecursiveElementWalkingVisitor() {
									@Override
									public void visitElement(PsiElement element) {
										IElementType type = element.getNode().getElementType();
										if (LatteTypesUtil.getAllTypeHintTokens().contains(type)) {

										} else if (type == LatteTypes.PHP_VARIABLE) {
											result.hasValidVariable = true;

										} else {
											super.visitElement(element);
											if (type != LatteTypes.PHP_CONTENT && !element.getText().equals("|")) {
												if (!result.hasValidVariable) {
													result.beforeVarCount++;
												}
											}
										}
									}
								});
							}

							if (!result.hasValidVariable) {
								addError("Macro 'var' must contains valid variable.", problems, element, manager, isOnTheFly);
							}

							if (result.beforeVarCount > 0) {
								addError("Invalid content in 'var' macro.", problems, element, manager, isOnTheFly);
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
		boolean hasValidVariable = false;
		int beforeVarCount = 0;
	}
}
