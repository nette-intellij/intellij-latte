package com.jantvrdik.intellij.latte.editorActions;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jantvrdik.intellij.latte.LatteLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * Handles individual keystrokes.
 */
public class LatteTypedHandler extends TypedHandlerDelegate {
	@Override
	public Result charTyped(char charTyped, Project project, @NotNull Editor editor, @NotNull PsiFile file) {
		// auto-inserts '}' after typing '{'
		if (charTyped == '{' && project != null && file.getLanguage() == LatteLanguage.INSTANCE) {
			int offset = editor.getCaretModel().getOffset();
			editor.getDocument().insertString(offset, "}");
			return Result.STOP;
		}

		return super.charTyped(charTyped, project, editor, file);
	}
}
