package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LookAheadLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

import java.util.Arrays;
import java.util.List;

/**
 * Lexer used for syntax highlighting
 *
 * It reuses the simple lexer, changing types of some tokens
 */
public class LatteLookAheadLexer extends LookAheadLexer {
	private static final TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE, LatteTypes.T_WHITESPACE);
	private static final TokenSet TAG_TAGS = TokenSet.create(LatteTypes.T_HTML_TAG_ATTR_EQUAL_SIGN, LatteTypes.T_HTML_TAG_ATTR_DQ);
	private static final List<String> LINK_TAGS = Arrays.asList("link", "plink", "n:href");

	private boolean lastLink = false;
	private boolean replaceAsLink = false;
	private Lexer lexer;

	public LatteLookAheadLexer(Lexer baseLexer) {
		super(baseLexer, 1);
		lexer = baseLexer;
	}

	@Override
	protected void addToken(int endOffset, IElementType type) {
		boolean wasLinkDestination = false;
		if ((type == LatteTypes.T_PHP_IDENTIFIER || type == LatteTypes.T_PHP_KEYWORD || (type == LatteTypes.T_MACRO_ARGS && isCharacterAtCurrentPosition(lexer, '#', ':'))) && replaceAsLink) {
			type = LatteTypes.T_LINK_DESTINATION;
			wasLinkDestination = true;
		}

		super.addToken(endOffset, type);
		if (!TAG_TAGS.contains(type)) {
			boolean current = (type == LatteTypes.T_MACRO_NAME || type == LatteTypes.T_HTML_TAG_NATTR_NAME) && isLinkMacro(lexer);
			replaceAsLink = (wasLinkDestination && !WHITESPACES.contains(type))
					|| (!wasLinkDestination && lastLink && WHITESPACES.contains(type))
					|| current;
			lastLink = current;
		}
	}

	public static boolean isCharacterAtCurrentPosition(Lexer baseLexer, char ...characters) {
		char current = baseLexer.getBufferSequence().charAt(baseLexer.getCurrentPosition().getOffset());
		for (char character : characters) {
			if (current == character) {
				return true;
			}
		}
		return false;
	}

	public static boolean isLinkMacro(Lexer baseLexer) {
		CharSequence tagName = baseLexer.getBufferSequence().subSequence(baseLexer.getTokenStart(), baseLexer.getTokenEnd());
		if (tagName.length() == 0) {
			return false;
		}
		return LINK_TAGS.contains(tagName.toString());
	}
}
