package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LatteMacroContent;
import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModifierNotAllowedInspection extends LocalInspectionTool {

	private static Set<String> macros;


	@NotNull
	@Override
	public String getShortName() {
		return "ModifierNotAllowed";
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
					boolean result = checkClassicMacro((LatteMacroTag) element);
					if (result) {
						ProblemDescriptor problem = manager.createProblemDescriptor(element, "Modifiers are not allowed here", true, ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly);
						problems.add(problem);
					}
				} else {
					super.visitElement(element);
				}
			}
		});


		return problems.toArray(new ProblemDescriptor[problems.size()]);
	}

	private static boolean checkClassicMacro(LatteMacroTag macro) {
		String name = macro.getMacroName();
		if (!macros.contains(name)) {
			return false;
		}
		LatteMacroContent content = macro.getMacroContent();
		if (content == null) {
			return false;
		}
		return content.getNode().getChildren(TokenSet.create(LatteTypes.T_MACRO_MODIFIERS)).length > 0;
	}

	static {
		macros = new HashSet<String>();
		macros.add("includeblock");
		macros.add("extends");
		macros.add("ifset");
		macros.add("if");
		macros.add("elseif");
		macros.add("else");
		macros.add("use");
		macros.add("breakIf");
		macros.add("continueIf");
		macros.add("dump");
		macros.add("debugbreak");
		macros.add("var");
		macros.add("contentType");
		macros.add("status");
		macros.add("ifCurrent");
		macros.add("form");
		macros.add("formContainer");
		macros.add("label");
		macros.add("input");
		macros.add("inputError");
	}
}
