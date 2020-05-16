package com.jantvrdik.intellij.latte.completion.handlers;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.LatteLanguage;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.Variable;
import org.jetbrains.annotations.NotNull;

public class PhpVariableInsertHandler implements InsertHandler<LookupElement> {

	private static final PhpVariableInsertHandler instance = new PhpVariableInsertHandler();

	public PhpVariableInsertHandler() {
		super();
	}

	public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement lookupElement) {
		PsiElement element = context.getFile().findElementAt(context.getStartOffset());
		if (element != null && element.getLanguage() == LatteLanguage.INSTANCE) {
			PsiElement parent = element.getParent();
			if (!(parent instanceof Variable) && !element.getText().startsWith("$")) {
				Editor editor = context.getEditor();
				CaretModel caretModel = editor.getCaretModel();
				int offset = caretModel.getOffset();
				caretModel.moveToOffset(element.getTextOffset() + (PhpPsiUtil.isOfType(parent, PhpElementTypes.CAST_EXPRESSION) ? 1 : 0));
				EditorModificationUtil.insertStringAtCaret(editor, "$");
				caretModel.moveToOffset(offset + 1);
				PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());
			}
		}
	}

	public static PhpVariableInsertHandler getInstance() {
		return instance;
	}
}