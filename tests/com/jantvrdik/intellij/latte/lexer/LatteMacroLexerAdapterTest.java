package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.Pair;
import org.junit.Test;

import static com.jantvrdik.intellij.latte.Assert.assertTokens;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LatteMacroLexerAdapterTest {
	@Test
	@SuppressWarnings("unchecked")
	public void testMacroLexer() {
		Lexer lexer = new LatteMacroLexerAdapter();

		lexer.start("{aB}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "aB"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{/De}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_CLOSE_TAG_OPEN, "{/"),
			Pair.create(T_MACRO_NAME, "De"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{/}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_CLOSE_TAG_OPEN, "{/"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{macro a /}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "macro"),
			Pair.create(T_MACRO_ARGS, " a "),
			Pair.create(T_MACRO_TAG_CLOSE_EMPTY, "/}"),
		});

		lexer.start("{$var}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$var"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{!$var}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NOESCAPE, "!"),
			Pair.create(T_MACRO_ARGS_VAR, "$var"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{=$var}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_SHORTNAME, "="),
			Pair.create(T_MACRO_ARGS_VAR, "$var"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{!=$var}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NOESCAPE, "!"),
			Pair.create(T_MACRO_SHORTNAME, "="),
			Pair.create(T_MACRO_ARGS_VAR, "$var"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{a()}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS, "a()"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{a::b}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS, "a::b"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{a\\b}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS, "a\\b"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{$var|noescape}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$var"),
			Pair.create(T_MACRO_MODIFIERS, "|noescape"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{$var|truncate:10|upper}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$var"),
			Pair.create(T_MACRO_MODIFIERS, "|truncate:10|upper"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		// https://github.com/nette/latte/issues/13
		lexer.start("{foreach $a as $v|noiterator /}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "foreach"),
			Pair.create(T_MACRO_ARGS, " "),
			Pair.create(T_MACRO_ARGS_VAR, "$a"),
			Pair.create(T_MACRO_ARGS, " as "),
			Pair.create(T_MACRO_ARGS_VAR, "$v"),
			Pair.create(T_MACRO_MODIFIERS, "|noiterator /"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{? function() { } }");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "?"),
			Pair.create(T_MACRO_ARGS, " function() { } "),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		// edge cases
		lexer.start("{={a|b}}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_SHORTNAME, "="),
			Pair.create(T_MACRO_ARGS, "{a"),
			Pair.create(T_MACRO_MODIFIERS, "|b}"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{=a|b:{}}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_SHORTNAME, "="),
			Pair.create(T_MACRO_ARGS, "a"),
			Pair.create(T_MACRO_MODIFIERS, "|b:{}"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{='{}}'{|a:'{}}'}/}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_SHORTNAME, "="),
			Pair.create(T_MACRO_ARGS_STRING, "'{}}'"),
			Pair.create(T_MACRO_ARGS, "{"),
			Pair.create(T_MACRO_MODIFIERS, "|a:'{}}'}/"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{? 'function() { $a = $b|c() }' |mod}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "?"),
			Pair.create(T_MACRO_ARGS, " "),
			Pair.create(T_MACRO_ARGS_STRING, "'function() { $a = $b|c() }'"),
			Pair.create(T_MACRO_ARGS, " "),
			Pair.create(T_MACRO_MODIFIERS, "|mod"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}
}
