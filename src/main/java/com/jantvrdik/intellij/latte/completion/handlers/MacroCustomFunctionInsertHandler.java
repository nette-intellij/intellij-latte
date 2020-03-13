package com.jantvrdik.intellij.latte.completion.handlers;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jetbrains.php.completion.insert.PhpInsertHandlerUtil;
import com.jetbrains.php.lang.psi.elements.impl.PhpUseImpl;
import org.jetbrains.annotations.NotNull;

public class MacroCustomFunctionInsertHandler implements InsertHandler<LookupElement> {

	private static final MacroCustomFunctionInsertHandler instance = new MacroCustomFunctionInsertHandler();

	public static MacroCustomFunctionInsertHandler getInstance() {
		return instance;
	}

	protected MacroCustomFunctionInsertHandler() {
		super();
	}

	public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement lookupElement) {
		Editor editor = context.getEditor();
		if (context.getCompletionChar() == '(') {
			context.setAddCompletionChar(false);
		}

		PsiElement element = context.getFile().findElementAt(context.getStartOffset());
		if (element != null && element.getNode().getElementType() == LatteTypes.T_PHP_IDENTIFIER) {
			boolean notInUse = PhpUseImpl.getUseList(element) == null;

			if (notInUse) {
				if (!LatteUtil.isStringAtCaret(editor, "(")) {
					this.insertParenthesesCodeStyleAware(editor);
				} else if (LatteUtil.isStringAtCaret(editor, "()")) {
					editor.getCaretModel().moveCaretRelatively(2, 0, false, false, true);
				} else {
					editor.getCaretModel().moveCaretRelatively(1, 0, false, false, true);
					showParameterInfo(editor);
				}
			}
		}
	}

	private void insertParenthesesCodeStyleAware(@NotNull Editor editor) {
		PhpInsertHandlerUtil.insertStringAtCaret(editor, "()");
	}

	public static void showParameterInfo(Editor editor) {
		Project project = editor.getProject();

		assert project != null;

		AutoPopupController.getInstance(project).autoPopupParameterInfo(editor, null);
	}
}