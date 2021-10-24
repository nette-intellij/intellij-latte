package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LattePhpStaticVariable;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StaticPropertyUsagesInspection extends BaseLocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "StaticPropertyUsages";
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
				if (element instanceof LattePhpStaticVariable) {
					NettePhpType phpType = ((LattePhpStaticVariable) element).getPrevReturnType();

					Collection<PhpClass> phpClasses = phpType.getPhpClasses(element.getProject());
					if (phpClasses.size() == 0) {
						return;
					}

					boolean isFound = false;
					String variableName = ((LattePhpStaticVariable) element).getVariableName();
					for (PhpClass phpClass : phpClasses) {
						for (Field field : phpClass.getFields()) {
							if (!field.isConstant() && field.getName().equals(variableName)) {
								PhpModifier modifier = field.getModifier();
								if (modifier.isPrivate()) {
									addProblem(manager, problems, element, "Used private static property '" + variableName + "'", isOnTheFly);
								} else if (modifier.isProtected()) {
									addProblem(manager, problems, element, "Used protected static property '" + variableName + "'", isOnTheFly);
								} else if (field.isDeprecated()) {
									addDeprecated(manager, problems, element, "Used static property '" + variableName + "' is marked as deprecated", isOnTheFly);
								} else if (field.isInternal()) {
									addDeprecated(manager, problems, element, "Used static property '" + variableName + "' is marked as internal", isOnTheFly);
								}

								if (!modifier.isStatic()) {
									String description = "Property '" + variableName + "' is not static but used statically";
									addProblem(manager, problems, element, description, isOnTheFly);

								}
								isFound = true;
							}
						}
					}

					if (!isFound) {
						addProblem(manager, problems, element, "Property '" + variableName + "' not found for type '" + phpType.toString() + "'", isOnTheFly);
					}

				} else {
					super.visitElement(element);
				}
			}
		});

		return problems.toArray(new ProblemDescriptor[0]);
	}
}
