package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import com.jantvrdik.intellij.latte.psi.LattePhpClassUsage;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MacroTemplateTypeInspection extends LocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "LatteTemplateType";
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
			public void visitElement(@NotNull PsiElement element) {
				if (element instanceof LatteMacroTag && ((LatteMacroTag) element).matchMacroName("templateType")) {
					List<LatteMacroTag> allMacros = new ArrayList<>();
					LatteUtil.findLatteMacroTemplateType(allMacros, (LatteFile) file);
					if (allMacros.size() > 1) {
						ProblemDescriptor problem = manager.createProblemDescriptor(
								element,
								"Tag {templateType} can be used only once per file.",
								true,
								ProblemHighlightType.GENERIC_ERROR,
								isOnTheFly
						);
						problems.add(problem);

					} else {
						List<LattePhpClassUsage> currentClasses = new ArrayList<>();
						LatteUtil.findLatteTemplateType(currentClasses, element);
						if (currentClasses.size() == 0) {
							ProblemDescriptor problem = manager.createProblemDescriptor(
									element,
									"Invalid class name in tag {templateType}.",
									true,
									ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
									isOnTheFly
							);
							problems.add(problem);
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
