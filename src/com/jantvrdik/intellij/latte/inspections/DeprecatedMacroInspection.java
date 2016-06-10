package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteMacro;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DeprecatedMacroInspection extends LocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "DeprecatedMacro";
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
				if (element instanceof LatteMacroTag) {
					String macroName = ((LatteMacroTag) element).getMacroName();
					LatteMacro macro = LatteConfiguration.INSTANCE.getMacro(element.getProject(), macroName);
					if (macro.deprecated) {
						ProblemDescriptor problem = manager.createProblemDescriptor(element, "Macro " + macroName + " is deprecated", true, ProblemHighlightType.LIKE_DEPRECATED, isOnTheFly);
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
