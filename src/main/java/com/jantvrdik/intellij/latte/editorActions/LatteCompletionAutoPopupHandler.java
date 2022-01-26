package com.jantvrdik.intellij.latte.editorActions;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.CompletionAutoPopupHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Handles auto popups
 */
public class LatteCompletionAutoPopupHandler extends CompletionAutoPopupHandler {

	final private Map<Character, Character> allowedPairs = new HashMap<Character, Character>() {{
		put('>', '-');
		put(':', ':');
	}};

	final private Set<Character> allowedCharacters = new HashSet<Character>() {{
		add('$');
		add('|');
		add('{');
		add('\\');
	}};

	@Override
	public @NotNull Result checkAutoPopup(char charTyped, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
		if (!(file instanceof LatteFile)) {
			return Result.DEFAULT;
		}

		if (allowedCharacters.contains(charTyped)) {
			AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
			return Result.STOP;

		} else if (allowedPairs.containsKey(charTyped)) {
			int offset = editor.getCaretModel().getOffset();
			String text = editor.getDocument().getText();

			Character pairChar = allowedPairs.get(charTyped);
			if (offset > 0 && text.length() - 1 >= offset && text.charAt(offset - 1) == pairChar) {
				AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
				return Result.STOP;
			}
			return Result.CONTINUE;
		}
		return super.checkAutoPopup(charTyped, project, editor, file);
	}
}
