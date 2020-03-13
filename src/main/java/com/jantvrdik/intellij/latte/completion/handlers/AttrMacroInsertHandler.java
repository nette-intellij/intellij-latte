package com.jantvrdik.intellij.latte.completion.handlers;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.LatteLanguage;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteMacro;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import org.jetbrains.annotations.NotNull;

public class AttrMacroInsertHandler implements InsertHandler<LookupElement> {

	private static final AttrMacroInsertHandler instance = new AttrMacroInsertHandler();

	public static AttrMacroInsertHandler getInstance() {
		return instance;
	}

	protected AttrMacroInsertHandler() {
		super();
	}

	public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement lookupElement) {
		PsiElement element = context.getFile().findElementAt(context.getStartOffset());
		if (element != null && element.getLanguage() == LatteLanguage.INSTANCE && element.getNode().getElementType() == LatteTypes.T_HTML_TAG_NATTR_NAME) {
			Editor editor = context.getEditor();
			CaretModel caretModel = editor.getCaretModel();
			int offset = caretModel.getOffset();
			if (LatteUtil.isStringAtCaret(editor, "=")) {
				caretModel.moveToOffset(offset + 2);
				return;
			}

			String attrName = LatteUtil.normalizeNAttrNameModifier(element.getText());
			LatteMacro macro = LatteConfiguration.INSTANCE.getMacro(element.getProject(), attrName);
			if (macro != null && !macro.hasParameters) {
				return;
			}

			editor.getDocument().insertString(offset, "=\"\"");
			caretModel.moveToOffset(offset + 2);

			PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());
		}
	}
}