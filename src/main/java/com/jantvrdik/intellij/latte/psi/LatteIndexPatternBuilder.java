package com.jantvrdik.intellij.latte.psi;

import com.intellij.lexer.LayeredLexer;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.search.IndexPatternBuilder;
import com.intellij.psi.impl.search.LexerEditorHighlighterLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class LatteIndexPatternBuilder implements IndexPatternBuilder {

	@Nullable
	@Override
	public Lexer getIndexingLexer(@NotNull PsiFile file) {
		if (!(file instanceof LatteFile)) {
			return null;
		}
		VirtualFile virtualFile = file.getVirtualFile();
		if (virtualFile == null) {
			virtualFile = file.getViewProvider().getVirtualFile();
		}

		try {
			LayeredLexer.ourDisableLayersFlag.set(Boolean.TRUE);
			EditorHighlighter highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(file.getProject(), virtualFile);
			return new LexerEditorHighlighterLexer(highlighter, false);
		} finally {
			LayeredLexer.ourDisableLayersFlag.set(Boolean.FALSE);
		}
	}

	@Nullable
	@Override
	public TokenSet getCommentTokenSet(@NotNull PsiFile file) {
		return TokenSet.create(LatteTypes.T_MACRO_COMMENT);
	}

	@Override
	public int getCommentStartDelta(IElementType tokenType) {
		return 0;
	}

	@Override
	public int getCommentEndDelta(IElementType tokenType) {
		if (tokenType == LatteTypes.T_MACRO_COMMENT) {
			return 2;
		}
		return 0;
	}
}
