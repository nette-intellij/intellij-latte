package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.Pair;
import org.junit.Test;

import static com.jantvrdik.intellij.latte.Assert.assertTokens;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LatteMacroContentLexerAdapterTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testMacroLexer() {
		Lexer lexer = new LatteMacroContentLexerAdapter();

		lexer.start(" a ");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_WHITESPACE, " "),
				Pair.create(T_PHP_CONTENT, "a"),
				Pair.create(T_WHITESPACE, " "),
		});

		lexer.start("$var");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_PHP_CONTENT, "$var"),
		});

		lexer.start("a()");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_PHP_CONTENT, "a()"),
		});

		lexer.start("a::b");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_PHP_CONTENT, "a"),
				Pair.create(T_MACRO_ARGS, "::"),
				Pair.create(T_PHP_CONTENT, "b"),
		});

		lexer.start("a\\b");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_PHP_CONTENT, "a\\b"),
		});

		lexer.start("$var|noescape");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_PHP_CONTENT, "$var|noescape"),
		});

		lexer.start(" function() { } ");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_WHITESPACE, " "),
				Pair.create(T_PHP_CONTENT, "function() { } "),
		});

		lexer.start("1");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_PHP_CONTENT, "1"),
		});

		lexer.start("1a");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_PHP_CONTENT, "1a"),
		});

		lexer.start("a1");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_PHP_CONTENT, "a1"),
		});
	}
}
