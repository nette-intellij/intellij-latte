package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LattePhpClassReference;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassUsagesInspection extends BaseLocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "LatteClassUsages";
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
				if (element instanceof LattePhpClassReference) {
					String className = ((LattePhpClassReference) element).getClassName();
					Collection<PhpClass> classes = LattePhpUtil.getClassesByFQN(element.getProject(), className);
					if (classes.size() == 0) {
						addError(manager, problems, element, "Undefined class '" + className + "'", isOnTheFly);

					} else {
						for (PhpClass phpClass : classes) {
							if (phpClass.isDeprecated()) {
								addDeprecated(manager, problems, element, "Used class '" + className + "' is marked as deprecated", isOnTheFly);
								break;

							} else if (phpClass.isInternal()) {
								addDeprecated(manager, problems, element, "Used class '" + className + "' is marked as internal", isOnTheFly);
								break;
							}
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
