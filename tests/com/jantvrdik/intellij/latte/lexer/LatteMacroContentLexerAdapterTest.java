package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.Pair;
import org.junit.Test;

import static com.jantvrdik.intellij.latte.Assert.assertTokens;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.T_MACRO_ARGS;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.T_MACRO_ARGS_NUMBER;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.T_MACRO_ARGS_STRING;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.T_MACRO_ARGS_VAR;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.T_MACRO_MODIFIERS;


public class LatteMacroContentLexerAdapterTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testMacroLexer() {
		Lexer lexer = new LatteMacroContentLexerAdapter();

		lexer.start(" a ");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS, " a "),
		});

		lexer.start("$var");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS_VAR, "$var"),
		});

		lexer.start("a()");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS, "a()"),
		});

		lexer.start("a::b");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS, "a::b"),
		});

		lexer.start("a\\b");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS, "a\\b"),
		});

		lexer.start("$var|noescape");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS_VAR, "$var"),
				Pair.create(T_MACRO_MODIFIERS, "|noescape"),
		});

		lexer.start("$var|truncate:10|upper");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS_VAR, "$var"),
				Pair.create(T_MACRO_MODIFIERS, "|truncate:10|upper"),
		});

		// https://github.com/nette/latte/issues/13
		lexer.start(" $a as $v|noiterator ");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS, " "),
				Pair.create(T_MACRO_ARGS_VAR, "$a"),
				Pair.create(T_MACRO_ARGS, " as "),
				Pair.create(T_MACRO_ARGS_VAR, "$v"),
				Pair.create(T_MACRO_MODIFIERS, "|noiterator "),
		});

		lexer.start(" function() { } ");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS, " function() { } "),
		});

		// edge cases
		lexer.start("{a|b}");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS, "{a"),
				Pair.create(T_MACRO_MODIFIERS, "|b}"),
		});

		lexer.start("a|b:{}");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS, "a"),
				Pair.create(T_MACRO_MODIFIERS, "|b:{}"),
		});

		lexer.start("'{}}'{|a:'{}}'}");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS_STRING, "'{}}'"),
				Pair.create(T_MACRO_ARGS, "{"),
				Pair.create(T_MACRO_MODIFIERS, "|a:'{}}'}"),
		});

		lexer.start(" 'function() { $a = $b|c() }' |mod");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS, " "),
				Pair.create(T_MACRO_ARGS_STRING, "'function() { $a = $b|c() }'"),
				Pair.create(T_MACRO_ARGS, " "),
				Pair.create(T_MACRO_MODIFIERS, "|mod"),
		});

		lexer.start("1");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS_NUMBER, "1"),
		});
		lexer.start("1a");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS_NUMBER, "1"),
				Pair.create(T_MACRO_ARGS, "a"),
		});
		lexer.start("a1");
		assertTokens(lexer, new Pair[]{
				Pair.create(T_MACRO_ARGS, "a1"),
		});
	}
}
