package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteModifier;
import com.jantvrdik.intellij.latte.intentions.AddCustomLatteModifier;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LatteMacroModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModifierDefinitionInspection extends LocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "LatteModifierDefinition";
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
				if (element instanceof LatteMacroModifier) {
					String filterName = ((LatteMacroModifier) element).getModifierName();
					LatteModifier latteModifier = LatteConfiguration.INSTANCE.getModifier(element.getProject(), filterName);
					if (latteModifier == null) {
						LocalQuickFix addModifierFix = IntentionManager.getInstance().convertToFix(new AddCustomLatteModifier(filterName));
						ProblemHighlightType type = ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
						String description = "Undefined latte filter '" + filterName + "'";
						ProblemDescriptor problem = manager.createProblemDescriptor(element, description, true, type, isOnTheFly, addModifierFix);
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
