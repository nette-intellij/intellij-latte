package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.Pair;
import org.junit.Test;

import static com.jantvrdik.intellij.latte.Assert.assertTokens;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LatteHighlightingLexerTest {
	@Test
	@SuppressWarnings("unchecked")
	public void testMacroLexer() {
		Lexer lexer = new LatteHighlightingLexer(new LatteLookAheadLexer(new LatteLexer()));

		lexer.start("{include #blockName}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "include"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_BLOCK_NAME, "#"),
			Pair.create(T_BLOCK_NAME, "blockName"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{= dump ()}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_SHORTNAME, "="),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_METHOD, "dump"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_LEFT_NORMAL_BRACE, "("),
			Pair.create(T_PHP_RIGHT_NORMAL_BRACE, ")"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{= \\Foo\\Bar}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_SHORTNAME, "="),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Foo"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Bar"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{= \\Bar}");
		assertTokens(lexer, new Pair[] {
				Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
				Pair.create(T_MACRO_SHORTNAME, "="),
				Pair.create(T_WHITESPACE, " "),
				Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
				Pair.create(T_PHP_NAMESPACE_REFERENCE, "Bar"),
				Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}
}
