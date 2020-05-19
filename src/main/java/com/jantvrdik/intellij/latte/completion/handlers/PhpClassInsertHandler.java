package com.jantvrdik.intellij.latte.completion.handlers;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jetbrains.php.completion.insert.PhpReferenceInsertHandler;
import org.jetbrains.annotations.NotNull;

public class PhpClassInsertHandler extends PhpReferenceInsertHandler {

	private static final PhpClassInsertHandler instance = new PhpClassInsertHandler();

	public PhpClassInsertHandler() {
		super();
	}

	public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement lookupElement) {
		// for removing first `\` (because class completion is triggered if prev element is `\` and PHP completion adding `\` before)
		PsiElement element = context.getFile().findElementAt(context.getStartOffset());

		super.handleInsert(context, lookupElement);

		element = context.getFile().findElementAt(context.getStartOffset());
		String text = element != null ? element.getText() : null;
		if (element == null || text == null || (text.startsWith("\\") && !text.substring(1).contains("\\"))) {
			return;
		}

		if (element.getNode().getElementType() == LatteTypes.PHP_CLASS_USAGE) {
			Editor editor = context.getEditor();
			CaretModel caretModel = editor.getCaretModel();
			int offset = caretModel.getOffset();
			caretModel.moveToOffset(element.getTextOffset());
			editor.getSelectionModel().setSelection(element.getTextOffset(), element.getTextOffset() + 1);
			EditorModificationUtil.deleteSelectedText(editor);
			caretModel.moveToOffset(offset - 1);
			PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());
		}
	}

	public static PhpClassInsertHandler getInstance() {
		return instance;
	}
}