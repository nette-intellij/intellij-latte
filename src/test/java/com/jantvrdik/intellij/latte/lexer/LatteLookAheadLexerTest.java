package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.Pair;
import org.junit.Test;

import static com.jantvrdik.intellij.latte.Assert.assertTokens;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LatteLookAheadLexerTest {
	@Test
	@SuppressWarnings("unchecked")
	public void testMacroLexer() {
		Lexer lexer = new LatteLookAheadLexer(new LatteLexer());

		lexer.start("{link default}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "link"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_LINK_DESTINATION, "default"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}
}
