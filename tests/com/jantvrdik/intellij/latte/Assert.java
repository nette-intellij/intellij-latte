package com.jantvrdik.intellij.latte;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.tree.IElementType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Assert {
	/**
	 * Checks that given lexer returns the correct tokens.
	 *
	 * @param lexer
	 * @param expectedTokens
	 */
	public static void assertTokens(Lexer lexer, Pair<IElementType, String>[] expectedTokens) {
		int i;
		for (i = 0; lexer.getTokenType() != null; i++) {
			if (i == expectedTokens.length) fail("Too many tokens from lexer; unexpected " + lexer.getTokenType());
			assertEquals("Wrong token type at index " + i, expectedTokens[i].first, lexer.getTokenType());
			assertEquals("Wrong token text at index " + i, expectedTokens[i].second, lexer.getTokenText());
			lexer.advance();
		}
		if (i < expectedTokens.length) fail("Not enough tokens from lexer; expected " + expectedTokens[i].first + " but got nothing.");
	}
}
