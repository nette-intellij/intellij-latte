package com.jantvrdik.intellij.latte.documentation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.config.LatteModifier;
import com.jantvrdik.intellij.latte.psi.LatteMacroModifier;
import org.jetbrains.annotations.Nullable;

public class LatteDocumentationProvider extends AbstractDocumentationProvider {

	@Override
	public @Nullable String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
		if (element instanceof LatteMacroModifier) {
			LatteModifier modifier = ((LatteMacroModifier) element).getMacroModifier();
			if (modifier == null) {
				return null;
			}

			String helpText = modifier.help.trim();
			if (helpText.length() > 0 && modifier.description.trim().length() > 0) {
				helpText += " - ";
			}
			helpText += "\"" + modifier.description + "\"";
			return helpText;
		}
		return null;
	}
}
