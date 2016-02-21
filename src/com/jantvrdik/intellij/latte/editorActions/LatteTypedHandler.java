package com.jantvrdik.intellij.latte.editorActions;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jantvrdik.intellij.latte.LatteLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * Handles individual keystrokes.
 */
public class LatteTypedHandler extends TypedHandlerDelegate {
	@Override
	public Result beforeCharTyped(char charTyped, Project project, Editor editor, PsiFile file, FileType fileType) {
		// ignores typing '}' before '}'
		if (charTyped == '}' && project != null && file.getViewProvider().getLanguages().contains(LatteLanguage.INSTANCE)) {
			CaretModel caretModel = editor.getCaretModel();
			String text = editor.getDocument().getText();
			int offset = caretModel.getOffset();
			if (text.length() > offset && text.charAt(offset) == '}') {
				caretModel.moveToOffset(offset + 1);
				return Result.STOP;
			}
		}

		return super.beforeCharTyped(charTyped, project, editor, file, fileType);
	}

	@Override
	public Result charTyped(char charTyped, Project project, @NotNull Editor editor, @NotNull PsiFile file) {
		// auto-inserts '}' after typing '{'
		if (charTyped == '{' && project != null && file.getViewProvider().getLanguages().contains(LatteLanguage.INSTANCE)) {
			int offset = editor.getCaretModel().getOffset();
			String text = editor.getDocument().getText();
			if (text.length() == offset || text.charAt(offset) != '}') {
				editor.getDocument().insertString(offset, "}");
				return Result.STOP;
			}
		}

		return super.charTyped(charTyped, project, editor, file);
	}
}
