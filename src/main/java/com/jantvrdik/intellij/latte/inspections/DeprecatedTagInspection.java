package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import com.jantvrdik.intellij.latte.settings.LatteTagSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DeprecatedTagInspection extends LocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "DeprecatedTag";
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
				if (element instanceof LatteMacroTag) {
					String macroName = ((LatteMacroTag) element).getMacroName();
					LatteTagSettings macro = LatteConfiguration.getInstance(element.getProject()).getTag(macroName);
					if (macro != null && macro.isDeprecated()) {
						String description = macro.getDeprecatedMessage() != null && macro.getDeprecatedMessage().length() > 0
								? macro.getDeprecatedMessage()
								: "Tag {" + macroName + "} is deprecated";
						ProblemDescriptor problem = manager.createProblemDescriptor(element, description, true, ProblemHighlightType.LIKE_DEPRECATED, isOnTheFly);
						problems.add(problem);

					}
				} else {
					super.visitElement(element);
				}
			}
		});

		return problems.toArray(new ProblemDescriptor[0]);
	}
}
