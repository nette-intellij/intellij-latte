package com.jantvrdik.intellij.latte.completion.handlers;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.LattePhpClassUsage;
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
		super.handleInsert(context, lookupElement);

		PsiElement prev = context.getFile().findElementAt(context.getStartOffset() - 1);
		PsiElement element = context.getFile().findElementAt(context.getStartOffset());
		String prevText = prev != null ? prev.getText() : null;
		String text = element != null ? element.getText() : null;
		if (prevText == null || text == null || (prevText.startsWith("\\") && !text.startsWith("\\"))) {
			return;
		}
		LattePhpClassUsage classUsage = element.getParent() instanceof LattePhpClassUsage ? (LattePhpClassUsage) element.getParent() : null;
		String[] className = (classUsage != null ? classUsage.getClassName() : "").split("\\\\");

		if ((prevText.length() > 0 || className.length > 1) && element.getNode().getElementType() == LatteTypes.T_PHP_NAMESPACE_RESOLUTION) {
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