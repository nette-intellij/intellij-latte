package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LattePhpProperty;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PropertyUsagesInspection extends BaseLocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "PropertyUsages";
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
				if (element instanceof LattePhpProperty) {
					LattePhpType phpType = ((LattePhpProperty) element).getPhpType();

					Collection<PhpClass> phpClasses = phpType.getPhpClasses(element.getProject());
					if (phpClasses == null) {
						return;
					}

					boolean isFound = false;
					String variableName = ((LattePhpProperty) element).getPropertyName();
					for (PhpClass phpClass : phpClasses) {
						for (Field field : phpClass.getFields()) {
							if (!field.isConstant() && field.getName().equals(LattePhpUtil.normalizePhpVariable(variableName))) {
								PhpModifier modifier = field.getModifier();
								if (modifier.isPrivate()) {
									addProblem(manager, problems, element, "Used private property '" + variableName + "'", isOnTheFly);

								} else if (modifier.isProtected()) {
									addProblem(manager, problems, element, "Used protected property '" + variableName + "'", isOnTheFly);
								}

								if (modifier.isStatic()) {
									String description = "Property '" + variableName + "' is static but used non statically";
									addProblem(manager, problems, element, description, isOnTheFly);

								}
								isFound = true;
							}
						}
					}

					if (!isFound) {
						addProblem(manager, problems, element, "Property '" + variableName + "' not found", ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly);
					}

				} else {
					super.visitElement(element);
				}
			}
		});

		return problems.toArray(new ProblemDescriptor[problems.size()]);
	}
}
