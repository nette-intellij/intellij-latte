package com.jantvrdik.intellij.latte.psi;

import com.intellij.lexer.Lexer;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.search.IndexPatternBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.lexer.LatteLexer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class LatteIndexPatternBuilder implements IndexPatternBuilder {

	@Nullable
	@Override
	public Lexer getIndexingLexer(@NotNull PsiFile file) {
		return file instanceof LatteFile ? new LatteLexer() : null;
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
		return 2;
	}
}
