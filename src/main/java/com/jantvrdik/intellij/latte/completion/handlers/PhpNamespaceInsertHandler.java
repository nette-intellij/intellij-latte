package com.jantvrdik.intellij.latte.completion.handlers;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiDocumentManager;
import com.jetbrains.php.completion.insert.PhpReferenceInsertHandler;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import org.jetbrains.annotations.NotNull;

public class PhpNamespaceInsertHandler extends PhpReferenceInsertHandler {

	private static final PhpNamespaceInsertHandler instance = new PhpNamespaceInsertHandler();

	public PhpNamespaceInsertHandler() {
		super();
	}

	public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement lookupElement) {
		final Object object = lookupElement.getObject();
		String fqn = object instanceof PhpNamespace ? ((PhpNamespace) object).getParentNamespaceName() : "";
		if (fqn.isEmpty()) {
			return;
		}

		int startOffset = context.getEditor().getCaretModel().getOffset();
		String fileText = context.getEditor().getDocument().getText();
		String current = fileText.substring(0, startOffset);
		int lastSpace = current.lastIndexOf(" ");
		current = current.substring(lastSpace + 1);

		if (fqn.startsWith("\\")) {
			fqn = fqn.substring(1);
		}

		context.getDocument().insertString(context.getStartOffset(), fqn);

		if (!current.endsWith("\\")) {
			EditorModificationUtil.insertStringAtCaret(context.getEditor(), "\\");
		}

		PsiDocumentManager.getInstance(context.getProject()).commitDocument(context.getDocument());
	}

	public static PhpNamespaceInsertHandler getInstance() {
		return instance;
	}
}