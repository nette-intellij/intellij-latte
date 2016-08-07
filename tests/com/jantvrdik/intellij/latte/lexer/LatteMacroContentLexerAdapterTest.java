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
			Pair.create(T_MACRO_ARGS_SYMBOL, "a"),
			Pair.create(T_WHITESPACE, " "),
		});

		lexer.start("$var");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS_VAR, "$var"),
		});

		lexer.start("a()");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_MACRO_ARGS_IDENTIFIER, "a"),
			Pair.create(T_MACRO_ARGS_LEFT_BRACKET, "("),
			Pair.create(T_MACRO_ARGS_RIGHT_BRACKET, ")"),
		});

		lexer.start("a::b");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_MACRO_ARGS_IDENTIFIER, "a"),
			Pair.create(T_MACRO_ARGS_PAAMAYIM_NEKUDOTAYIM, "::"),
			Pair.create(T_MACRO_ARGS_SYMBOL, "b"),
		});

		lexer.start("a\\b");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_MACRO_ARGS_SYMBOL, "a"),
			Pair.create(T_MACRO_ARGS_OTHER, "\\"),
			Pair.create(T_MACRO_ARGS_SYMBOL, "b"),
		});

		lexer.start("$var|noescape");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_MACRO_ARGS_VAR, "$var"),
			Pair.create(T_MACRO_MODIFIERS, "|"),
			Pair.create(T_MACRO_ARGS_SYMBOL, "noescape"),
		});

		lexer.start("$var|truncate:10|upper");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_MACRO_ARGS_VAR, "$var"),
			Pair.create(T_MACRO_MODIFIERS, "|"),
			Pair.create(T_MACRO_ARGS_SYMBOL, "truncate"),
			Pair.create(T_MACRO_ARGS_COLON, ":"),
			Pair.create(T_MACRO_ARGS_NUMBER, "10"),
			Pair.create(T_MACRO_MODIFIERS, "|"),
			Pair.create(T_MACRO_ARGS_SYMBOL, "upper"),
		});

		// https://github.com/nette/latte/issues/13
		lexer.start(" $a as $v|noiterator ");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_VAR, "$a"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_SYMBOL, "as"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_VAR, "$v"),
			Pair.create(T_MACRO_MODIFIERS, "|"),
			Pair.create(T_MACRO_ARGS_SYMBOL, "noiterator"),
			Pair.create(T_WHITESPACE, " "),
		});

		lexer.start(" function() { } ");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_IDENTIFIER, "function"),
			Pair.create(T_MACRO_ARGS_LEFT_BRACKET, "("),
			Pair.create(T_MACRO_ARGS_RIGHT_BRACKET, ")"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_LEFT_CURLY_BRACKET, "{"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_RIGHT_CURLY_BRACKET, "}"),
			Pair.create(T_WHITESPACE, " "),
		});

		// edge cases
		lexer.start("{a|b}");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_MACRO_ARGS_LEFT_CURLY_BRACKET, "{"),
			Pair.create(T_MACRO_ARGS_SYMBOL, "a"),
			Pair.create(T_MACRO_MODIFIERS, "|"),
			Pair.create(T_MACRO_ARGS_SYMBOL, "b"),
			Pair.create(T_MACRO_ARGS_RIGHT_CURLY_BRACKET, "}"),
		});

		lexer.start("a|b:{}");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_MACRO_ARGS_SYMBOL, "a"),
			Pair.create(T_MACRO_MODIFIERS, "|"),
			Pair.create(T_MACRO_ARGS_SYMBOL, "b"),
			Pair.create(T_MACRO_ARGS_COLON, ":"),
			Pair.create(T_MACRO_ARGS_LEFT_CURLY_BRACKET, "{"),
			Pair.create(T_MACRO_ARGS_RIGHT_CURLY_BRACKET, "}"),
		});

		lexer.start("'{}}'{|a:'{}}'}");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_MACRO_ARGS_SINGLE_QUOTE_LEFT, "'"),
			Pair.create(T_MACRO_ARGS_STRING, "{}}"),
			Pair.create(T_MACRO_ARGS_SINGLE_QUOTE_RIGHT, "'"),
			Pair.create(T_MACRO_ARGS_LEFT_CURLY_BRACKET, "{"),
			Pair.create(T_MACRO_MODIFIERS, "|"),
			Pair.create(T_MACRO_ARGS_SYMBOL, "a"),
			Pair.create(T_MACRO_ARGS_COLON, ":"),
			Pair.create(T_MACRO_ARGS_SINGLE_QUOTE_LEFT, "'"),
			Pair.create(T_MACRO_ARGS_STRING, "{}}"),
			Pair.create(T_MACRO_ARGS_SINGLE_QUOTE_RIGHT, "'"),
			Pair.create(T_MACRO_ARGS_RIGHT_CURLY_BRACKET, "}"),
		});

		lexer.start(" 'function() { $a = $b|c() }' |mod");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_SINGLE_QUOTE_LEFT, "'"),
			Pair.create(T_MACRO_ARGS_STRING, "function() { $a = $b|c() }"),
			Pair.create(T_MACRO_ARGS_SINGLE_QUOTE_RIGHT, "'"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_MODIFIERS, "|"),
			Pair.create(T_MACRO_ARGS_SYMBOL, "mod"),
		});

		lexer.start("1");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS_NUMBER, "1"),
		});
		lexer.start("1a");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS_NUMBER, "1"),
				Pair.create(T_MACRO_ARGS_SYMBOL, "a"),
		});
		lexer.start("a1");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS_SYMBOL, "a1"),
		});

		//incomplete

		lexer.start("a'b");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_MACRO_ARGS_SYMBOL, "a"),
			Pair.create(T_MACRO_ARGS_SINGLE_QUOTE_LEFT, "'"),
			Pair.create(T_MACRO_ARGS_STRING, "b"),
		});
		lexer.start("a\"b");
		assertTokens(lexer, new Pair[]{
			Pair.create(T_MACRO_ARGS_SYMBOL, "a"),
			Pair.create(T_MACRO_ARGS_DOUBLE_QUOTE_LEFT, "\""),
			Pair.create(T_MACRO_ARGS_STRING, "b"),
		});
	}
}
