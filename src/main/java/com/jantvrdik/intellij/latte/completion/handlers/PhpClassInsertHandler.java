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
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

public class PhpClassInsertHandler extends PhpReferenceInsertHandler {

	private static final PhpClassInsertHandler instance = new PhpClassInsertHandler();

	public PhpClassInsertHandler() {
		super();
	}

	public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement lookupElement) {
		final Object object = lookupElement.getObject();
		final String classNamespace = object instanceof PhpClass ? ((PhpClass) object).getNamespaceName() : "";

		if (!classNamespace.isEmpty()) {
			int startOffset = context.getEditor().getCaretModel().getOffset();
			String fileText = context.getEditor().getDocument().getText();
			String current = fileText.substring(0, startOffset);
			int lastSpace = current.lastIndexOf(" ");
			current = current.substring(lastSpace + 1);
			int index = current.lastIndexOf("\\");
			String existingNamespace = "";
			if (index > 0 && current.length() >= index) {
				existingNamespace = current.substring(0, index) + "\\";
			}

			String fqn = classNamespace;
			if (!classNamespace.equals("\\") && !existingNamespace.startsWith("\\") && fqn.startsWith("\\")) {
				fqn = fqn.substring(1);
			} else if (classNamespace.equals("\\") && existingNamespace.length() == 0) {
				fqn = "\\";
			}

			if (existingNamespace.length() > 0 && fqn.contains(existingNamespace)) {
				fqn = fqn.replace(existingNamespace, "");
			}

			context.getDocument().insertString(context.getStartOffset(), fqn);
			PsiDocumentManager.getInstance(context.getProject()).commitDocument(context.getDocument());
		}
	}

	public static PhpClassInsertHandler getInstance() {
		return instance;
	}
}