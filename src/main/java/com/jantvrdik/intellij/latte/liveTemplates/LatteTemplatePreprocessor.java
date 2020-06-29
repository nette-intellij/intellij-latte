package com.jantvrdik.intellij.latte.liveTemplates;

import com.intellij.codeInsight.template.impl.TemplatePreprocessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jantvrdik.intellij.latte.psi.LatteMacroContent;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.Nullable;

public class LatteTemplatePreprocessor implements TemplatePreprocessor {

	@Override
	public void preprocessTemplate(Editor editor, PsiFile file, int caretOffset, String textToInsert, String templateText) {
		if (!textToInsert.endsWith("}")) {
			return;
		}

		PsiElement el = file.findElementAt(caretOffset);
		if (el == null) {
			return;
		}

		PsiElement nextCloseTag = getNextElementTagClose(el);
		if (nextCloseTag != null) {
			editor.getDocument().replaceString(caretOffset, caretOffset + 1, "");
		}
	}

	@Nullable
	private PsiElement getNextElementTagClose(PsiElement element) {
		PsiElement next = element.getNextSibling();
		while (next != null) {
			if (next instanceof LatteMacroContent || next.getNode().getElementType() == LatteTypes.T_MACRO_NAME) {
				next = next.getNextSibling();
				continue;

			} else if (next.getNode().getElementType() == LatteTypes.T_MACRO_TAG_CLOSE) {
				return next;
			}
			break;
		}
		return null;
	}
}