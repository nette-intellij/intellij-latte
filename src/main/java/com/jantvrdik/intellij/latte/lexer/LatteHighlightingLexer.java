package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LookAheadLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

/**
 * Lexer used for syntax highlighting
 *
 * It reuses the simple lexer, changing types of some tokens
 */
public class LatteHighlightingLexer extends LookAheadLexer {

	public LatteHighlightingLexer(Lexer baseLexer) {
		super(baseLexer, 1);
	}

	@Override
	protected void lookAhead(Lexer baseLexer) {
		IElementType currentToken = baseLexer.getTokenType();

		if (currentToken == LatteTypes.T_PHP_IDENTIFIER) {
			advanceLexer(baseLexer);
			currentToken = baseLexer.getTokenType();
			if (TokenSet.create(TokenType.WHITE_SPACE, LatteTypes.T_WHITESPACE).contains(currentToken)) {
				advanceLexer(baseLexer);
				currentToken = baseLexer.getTokenType();
			}

			if (currentToken == LatteTypes.T_PHP_LEFT_NORMAL_BRACE) {
				replaceCachedType(0, LatteTypes.T_PHP_METHOD);
			}

		} else {
			super.lookAhead(baseLexer);
		}
	}
}
