package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.Pair;
import org.junit.Test;

import static com.jantvrdik.intellij.latte.Assert.assertTokens;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LatteTopLexerAdapterTest {
	@Test
	@SuppressWarnings("unchecked")
	public void testSimple() {
		Lexer lexer = new LatteTopLexerAdapter();

		lexer.start("");
		assertTokens(lexer, new Pair[] {});

		lexer.start("abc<strong>def</strong>ghi");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "abc<strong>def</strong>ghi"),
		});

		lexer.start("<strong a1='v1' a2=\"v2\" a3=v3>abc</strong>");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<strong a1='v1' a2=\"v2\" a3=v3>abc</strong>"),
		});

		lexer.start("{maAro}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_CLASSIC, "{maAro}"),
		});

		lexer.start("{ macro }");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "{ macro }"),
		});

		lexer.start("{macro}abc{/}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_CLASSIC, "{macro}"),
			Pair.create(T_TEXT, "abc"),
			Pair.create(T_MACRO_CLASSIC, "{/}"),
		});

		lexer.start("abc{macro}def{/macro}ghi");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "abc"),
			Pair.create(T_MACRO_CLASSIC, "{macro}"),
			Pair.create(T_TEXT, "def"),
			Pair.create(T_MACRO_CLASSIC, "{/macro}"),
			Pair.create(T_TEXT, "ghi"),
		});

		lexer.start("{? $f = function() { } }");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_CLASSIC, "{? $f = function() { } }"),
		});

		lexer.start("{={a|b}}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_CLASSIC, "{={a|b}}"),
		});

		lexer.start("<div {if $abc}def{/if}>");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<div "),
			Pair.create(T_MACRO_CLASSIC, "{if $abc}"),
			Pair.create(T_TEXT, "def"),
			Pair.create(T_MACRO_CLASSIC, "{/if}"),
			Pair.create(T_TEXT, ">"),
		});

		lexer.start("<div attr='{$val}'>");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<div attr='"),
			Pair.create(T_MACRO_CLASSIC, "{$val}"),
			Pair.create(T_TEXT, "'>"),
		});

		lexer.start("<div attr='abc{$val}def'>");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<div attr='abc"),
			Pair.create(T_MACRO_CLASSIC, "{$val}"),
			Pair.create(T_TEXT, "def'>"),
		});

		lexer.start("<div attr='abc{ $val }def'>");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<div attr='abc{ $val }def'>"),
		});

		lexer.start("<div n:attr='{$val}'>");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<div "),
			Pair.create(T_HTML_TAG_NATTR_NAME, "n:attr"),
			Pair.create(T_HTML_TAG_ATTR_EQUAL_SIGN, "="),
			Pair.create(T_HTML_TAG_ATTR_SQ, "'"),
			Pair.create(T_MACRO_CONTENT, "{$val}"),
			Pair.create(T_HTML_TAG_ATTR_SQ, "'"),
			Pair.create(T_TEXT, ">"),
		});

		lexer.start("A<!-- {$b} <el> -->C");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "A<!-- "),
			Pair.create(T_MACRO_CLASSIC, "{$b}"),
			Pair.create(T_TEXT, " <el> -->C"),
		});

		lexer.start("A{* {$var} * <el> *}B{*C}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "A"),
			Pair.create(T_MACRO_COMMENT, "{* {$var} * <el> *}"),
			Pair.create(T_TEXT, "B"),
			Pair.create(T_MACRO_CLASSIC, "{*C}"),
		});

		lexer.start("<script><el>{$var}</style></script><el>");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<script><el>"),
			Pair.create(T_MACRO_CLASSIC, "{$var}"),
			Pair.create(T_TEXT, "</style></script><el>"),
		});

		lexer.start("<style><el>{$var}</script></style><el>");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<style><el>"),
			Pair.create(T_MACRO_CLASSIC, "{$var}"),
			Pair.create(T_TEXT, "</script></style><el>"),
		});

		lexer.start("<script><el>");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<script><el>"),
		});

		// edge
		lexer.start("<e1 a1=\"A{if $cond}<e2 a2=\"B{if $cond}INNER{/if}C\">{/if}D\">");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<e1 a1=\"A"),
			Pair.create(T_MACRO_CLASSIC, "{if $cond}"),
			Pair.create(T_TEXT, "<e2 a2=\"B"),
			Pair.create(T_MACRO_CLASSIC, "{if $cond}"),
			Pair.create(T_TEXT, "INNER"),
			Pair.create(T_MACRO_CLASSIC, "{/if}"),
			Pair.create(T_TEXT, "C\">"),
			Pair.create(T_MACRO_CLASSIC, "{/if}"),
			Pair.create(T_TEXT, "D\">"),
		});

		lexer.start("{* ' *}A{* ' *}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_COMMENT, "{* ' *}"),
			Pair.create(T_TEXT, "A"),
			Pair.create(T_MACRO_COMMENT, "{* ' *}"),
		});

		lexer.start("<div n:syntax='off'><div>{$foo}</div>{$bar}</div>{$lorem}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<div "),
			Pair.create(T_HTML_TAG_NATTR_NAME, "n:syntax"),
			Pair.create(T_HTML_TAG_ATTR_EQUAL_SIGN, "="),
			Pair.create(T_HTML_TAG_ATTR_SQ, "'"),
			Pair.create(T_MACRO_CONTENT, "off"),
			Pair.create(T_HTML_TAG_ATTR_SQ, "'"),
			Pair.create(T_TEXT, "><div>{$foo}</div>{$bar}</div>"),
			Pair.create(T_MACRO_CLASSIC, "{$lorem}"),
		});
		lexer.start("<script n:syntax=off>{$foo}</script>{$lorem}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<script "),
			Pair.create(T_HTML_TAG_NATTR_NAME, "n:syntax"),
			Pair.create(T_HTML_TAG_ATTR_EQUAL_SIGN, "="),
			Pair.create(T_MACRO_CONTENT, "off"),
			Pair.create(T_TEXT, ">{$foo}</script>"),
			Pair.create(T_MACRO_CLASSIC, "{$lorem}"),
		});
		lexer.start("{$lorem}{syntax off}foo {$lorem}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_CLASSIC, "{$lorem}"),
			Pair.create(T_MACRO_CLASSIC, "{syntax off}"),
			Pair.create(T_TEXT, "foo {$lorem}"),
		});

		lexer.start("{$lorem}{syntax double}{{$lorem}}{$lorem}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_CLASSIC, "{$lorem}"),
			Pair.create(T_MACRO_CLASSIC, "{syntax double}"),
			Pair.create(T_MACRO_CLASSIC_DOUBLE, "{{$lorem}}"),
			Pair.create(T_TEXT, "{$lorem}"),
		});
		lexer.start("{$lorem}{syntax double}{{/syntax}}{$lorem}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_CLASSIC, "{$lorem}"),
			Pair.create(T_MACRO_CLASSIC, "{syntax double}"),
			Pair.create(T_MACRO_CLASSIC_DOUBLE, "{{/syntax}}"),
			Pair.create(T_MACRO_CLASSIC, "{$lorem}"),
		});
		lexer.start("<div n:syntax='double'>{$foo}{{$foo}}</div>{$foo}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_TEXT, "<div "),
			Pair.create(T_HTML_TAG_NATTR_NAME, "n:syntax"),
			Pair.create(T_HTML_TAG_ATTR_EQUAL_SIGN, "="),
			Pair.create(T_HTML_TAG_ATTR_SQ, "'"),
			Pair.create(T_MACRO_CONTENT, "double"),
			Pair.create(T_HTML_TAG_ATTR_SQ, "'"),
			Pair.create(T_TEXT, ">{$foo}"),
			Pair.create(T_MACRO_CLASSIC_DOUBLE, "{{$foo}}"),
			Pair.create(T_TEXT, "</div>"),
			Pair.create(T_MACRO_CLASSIC, "{$foo}"),
		});
	}
}
