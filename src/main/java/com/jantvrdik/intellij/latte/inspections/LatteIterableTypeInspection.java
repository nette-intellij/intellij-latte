package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LattePhpArrayUsage;
import com.jantvrdik.intellij.latte.psi.LattePhpForeach;
import com.jantvrdik.intellij.latte.psi.LattePhpStatement;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import com.jantvrdik.intellij.latte.utils.LattePhpVariableUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LatteIterableTypeInspection extends BaseLocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "LatteIterableType";
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
				if (element instanceof BaseLattePhpElement) {
					if (!LattePhpVariableUtil.isNextDefinitionOperator(element)) {
						for (LattePhpArrayUsage usage : ((BaseLattePhpElement) element).getPhpArrayUsageList()) {
							if (usage.getPhpArrayContent().getFirstChild() == null) {
								addError(manager, problems, usage, "Can not use [] for reading", isOnTheFly);
							}
						}
					}

				} else if (element instanceof LattePhpForeach) {
					LattePhpStatement statement = ((LattePhpForeach) element).getPhpStatement();
					if (statement != null) {
						LattePhpType type = statement.getPhpType();
						if (!type.isMixed() && !type.isIterable(element.getProject())) {
							addProblem(
									manager,
									problems,
									statement,
									"Invalid argument supplied to 'foreach'. Expected types: 'array' or 'object', '" + type.toString() + "' provided.",
									isOnTheFly
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
