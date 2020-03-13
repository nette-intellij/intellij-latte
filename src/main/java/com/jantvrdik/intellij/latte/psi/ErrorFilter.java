package com.jantvrdik.intellij.latte.psi;

import com.intellij.codeInsight.highlighting.HighlightErrorFilter;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.psi.xml.XmlElement;
import com.jantvrdik.intellij.latte.LatteLanguage;
import com.jantvrdik.intellij.latte.psi.LatteMacroClassic;
import org.jetbrains.annotations.NotNull;

public class ErrorFilter extends HighlightErrorFilter {
	public ErrorFilter() {
	}

	public boolean shouldHighlightErrorElement(@NotNull PsiErrorElement element) {
		PsiFile templateLanguageFile = PsiUtilCore.getTemplateLanguageFile(element.getContainingFile());
		if (templateLanguageFile == null) {
			return true;
		}
		Language language = templateLanguageFile.getLanguage();
		if (language != LatteLanguage.INSTANCE) {
			return true;
		}
		if (element.getParent() instanceof XmlElement || element.getParent() instanceof CssElement) {
			return false;
		}
		if (element.getParent().getLanguage() == LatteLanguage.INSTANCE) {
			return true;
		}
		PsiElement nextSibling;
		for (nextSibling = PsiTreeUtil.nextLeaf(element); nextSibling instanceof PsiWhiteSpace; nextSibling = nextSibling.getNextSibling());

		PsiElement psiElement = nextSibling == null ? null : PsiTreeUtil.findCommonParent(nextSibling, element);
		boolean nextIsOuterLanguageElement = nextSibling instanceof OuterLanguageElement || nextSibling instanceof LatteMacroClassic;
		return !nextIsOuterLanguageElement || psiElement == null || psiElement instanceof PsiFile;
	}

}
