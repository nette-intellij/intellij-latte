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
	private static final TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE, LatteTypes.T_WHITESPACE);

	private boolean lastHashTag = false;
	private IElementType lastToken = null;
	private Lexer lexer;

	public LatteHighlightingLexer(Lexer baseLexer) {
		super(baseLexer, 1);
		lexer = baseLexer;
	}

	@Override
	protected void addToken(int endOffset, IElementType type) {
		if (lastToken == LatteTypes.T_PHP_NAMESPACE_RESOLUTION && (type == LatteTypes.T_PHP_IDENTIFIER)) {
			type = LatteTypes.T_PHP_NAMESPACE_REFERENCE;
		} else if (type == LatteTypes.T_PHP_IDENTIFIER && lastHashTag) {
			type = LatteTypes.T_BLOCK_NAME;
		}

		super.addToken(endOffset, type);
		lastToken = type;
		if (!WHITESPACES.contains(type)) {
			lastHashTag = type == LatteTypes.T_MACRO_ARGS && LatteLookAheadLexer.isCharacterAtCurrentPosition(lexer, '#');
		}
	}

	@Override
	protected void lookAhead(Lexer baseLexer) {
		IElementType currentToken = baseLexer.getTokenType();

		if (currentToken == LatteTypes.T_PHP_IDENTIFIER) {
			advanceLexer(baseLexer);
			currentToken = baseLexer.getTokenType();
			if (WHITESPACES.contains(currentToken)) {
				advanceLexer(baseLexer);
				currentToken = baseLexer.getTokenType();
			}

			if (currentToken == LatteTypes.T_PHP_LEFT_NORMAL_BRACE) {
				replaceCachedType(0, LatteTypes.T_PHP_METHOD);
			} else {
				super.lookAhead(baseLexer);
			}

		} else if (currentToken == LatteTypes.T_MACRO_ARGS && LatteLookAheadLexer.isCharacterAtCurrentPosition(baseLexer, '#')) {
			advanceLexer(baseLexer);
			currentToken = baseLexer.getTokenType();
			if (TokenSet.create(TokenType.WHITE_SPACE, LatteTypes.T_WHITESPACE).contains(currentToken)) {
				advanceLexer(baseLexer);
				currentToken = baseLexer.getTokenType();
			}

			if (currentToken == LatteTypes.T_PHP_IDENTIFIER) {
				replaceCachedType(0, LatteTypes.T_BLOCK_NAME);
			}

		} else {
			super.lookAhead(baseLexer);
		}
	}
}
