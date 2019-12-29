package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LattePhpConstant;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConstantUsagesInspection extends LocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "ConstantUsages";
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
				if (element instanceof LattePhpConstant) {
					LattePhpType phpType = ((LattePhpConstant) element).getPhpType();

					Collection<PhpClass> phpClasses = phpType.getPhpClasses(element.getProject());
					if (phpClasses == null) {
						return;
					}

					boolean isFound = false;
					String constantName = ((LattePhpConstant) element).getConstantName();
					for (PhpClass phpClass : phpClasses) {
						for (Field field : phpClass.getFields()) {
							if (field.isConstant() && field.getName().equals(constantName)) {
								PhpModifier modifier = field.getModifier();
								if (modifier.isPrivate()) {
									addProblem(manager, problems, element, "Used private constant '" + constantName + "'", isOnTheFly);

								} else if (modifier.isProtected()) {
									addProblem(manager, problems, element, "Used protected constant '" + constantName + "'", isOnTheFly);
								}
								isFound = true;
							}
						}
					}

					if (!isFound) {
						addProblem(manager, problems, element, "Constant '" + constantName + "' not found", ProblemHighlightType.GENERIC_ERROR, isOnTheFly);
					}

				} else {
					super.visitElement(element);
				}
			}
		});

		return problems.toArray(new ProblemDescriptor[problems.size()]);
	}

	private void addProblem(
			@NotNull final InspectionManager manager,
			List<ProblemDescriptor> problems,
			@NotNull PsiElement element,
			@NotNull String description,
			boolean isOnTheFly
	) {
		addProblem(manager, problems, element, description, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly);
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
}
