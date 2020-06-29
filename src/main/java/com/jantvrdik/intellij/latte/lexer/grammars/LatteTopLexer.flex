package com.jantvrdik.intellij.latte.lexer;

import com.intellij.psi.tree.IElementType;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

%%

%class LatteTopLexer
%extends LatteBaseFlexLexer
%function advance
%type IElementType
%unicode
%ignorecase

%state HTML_TEXT
%state HTML_OPEN_TAG_OPEN
%state HTML_CLOSE_TAG_OPEN
%state HTML_TAG
%state SCRIPT_TAG
%state SCRIPT_CDATA
%state STYLE_TAG
%state STYLE_CDATA
%state NETTE_ATTR
%state NETTE_ATTR_VALUE
%state NETTE_ATTR_SQ
%state NETTE_ATTR_DQ
%state HTML_ATTR
%state HTML_ATTR_VALUE
%state HTML_ATTR_SQ
%state HTML_ATTR_DQ
%state HTML_COMMENT

WHITE_SPACE=[ \t\r\n]+
MACRO_COMMENT = "{*" ~"*}"
MACRO_CLASSIC = "{" [^ \t\r\n'\"{}] ({MACRO_STRING} | "{" {MACRO_STRING}* "}")*  ("'" ("\\" [^] | [^'\\])* | "\"" ("\\" [^] | [^\"\\])*)? "}"?
MACRO_STRING = {MACRO_STRING_SQ} | {MACRO_STRING_DQ} | {MACRO_STRING_UQ}
MACRO_STRING_SQ = "'" ("\\" [^] | [^'\\])* "'"
MACRO_STRING_DQ = "\"" ("\\" [^] | [^\"\\])* "\""
MACRO_STRING_UQ = [^'\"{}]

%%
<YYINITIAL> {
	[^] {
		rollbackMatch();
		pushState(HTML_TEXT);
	}
}

<HTML_TEXT, SCRIPT_TAG, SCRIPT_CDATA, STYLE_TAG, STYLE_CDATA, HTML_TAG, HTML_ATTR_SQ, HTML_ATTR_DQ, HTML_COMMENT> {
	{MACRO_COMMENT} / [^]* {
		return T_MACRO_COMMENT;
	}

	{MACRO_CLASSIC} {
		return T_MACRO_CLASSIC;
	}
}

<HTML_TEXT> {
	"<!--" {
		pushState(HTML_COMMENT);
		return T_TEXT;
	}

	"<" / [a-zA-Z0-9:] {
		pushState(HTML_OPEN_TAG_OPEN);
		return T_HTML_OPEN_TAG_OPEN;
	}

	"</" / [a-zA-Z0-9:]  {
		pushState(HTML_CLOSE_TAG_OPEN);
		return T_HTML_CLOSE_TAG_OPEN;
	}

	[^<{]+ {
		return T_TEXT;
	}

	[^] {
		return T_TEXT;
	}
}

<HTML_OPEN_TAG_OPEN> {
	"script" / [^a-zA-Z0-9:] {
		pushState(SCRIPT_TAG);
		return T_TEXT;
	}

	"style" / [^a-zA-Z0-9:] {
		pushState(STYLE_TAG);
		return T_TEXT;
	}
}

<HTML_OPEN_TAG_OPEN, HTML_CLOSE_TAG_OPEN> {
	[a-zA-Z0-9:]+ {
		pushState(HTML_TAG);
		return T_TEXT;
	}
}

<SCRIPT_TAG> {
	"/>" {
		popState(HTML_OPEN_TAG_OPEN);
		popState(HTML_TEXT);
		pushState(SCRIPT_CDATA);
		return T_TEXT;
	}

	">" {
		popState(HTML_OPEN_TAG_OPEN);
		popState(HTML_TEXT);
		pushState(SCRIPT_CDATA);
		return T_TEXT;
	}
}

<SCRIPT_CDATA> {
	"</" / "script" [^a-zA-Z0-9:] {
		popState(HTML_TEXT);
		pushState(HTML_CLOSE_TAG_OPEN);
		return T_TEXT;
	}

	[^] {
		return T_TEXT;
	}
}

<STYLE_TAG> {
	"/>" {
		popState(HTML_OPEN_TAG_OPEN);
		popState(HTML_TEXT);
		pushState(STYLE_CDATA);
		return T_TEXT;
	}

	">" {
		popState(HTML_OPEN_TAG_OPEN);
		popState(HTML_TEXT);
		pushState(STYLE_CDATA);
		return T_TEXT;
	}
}

<STYLE_CDATA> {
	"</" / "style" [^a-zA-Z0-9:] {
		popState(HTML_TEXT);
		pushState(HTML_CLOSE_TAG_OPEN);
		return T_TEXT;
	}

	[^] {
		return T_TEXT;
	}
}

<HTML_TAG> {
	"/>" {
		popState(HTML_OPEN_TAG_OPEN, HTML_CLOSE_TAG_OPEN);
		popState(HTML_TEXT);
		return T_HTML_OPEN_TAG_CLOSE;
	}

	">" {
		popState(HTML_OPEN_TAG_OPEN, HTML_CLOSE_TAG_OPEN);
		popState(HTML_TEXT);
		return T_HTML_TAG_CLOSE;
	}
}

<SCRIPT_TAG, STYLE_TAG, HTML_TAG> {
	"n:" [^ \t\r\n/>={]+ {
		pushState(NETTE_ATTR);
		return T_HTML_TAG_NATTR_NAME;
	}

	// TODO: missing !'n:' (sth. like [^n] | [n][^:])
	[^ \t\r\n/>={]+ {
		pushState(HTML_ATTR);
		return T_TEXT;
	}

	{WHITE_SPACE} {
		return T_TEXT;
	}

	[^] {
		return T_TEXT; // fallback
	}
}

<NETTE_ATTR> {
	"=" / [ \t\r\n]* [^ \t\r\n/>{] {
		pushState(NETTE_ATTR_VALUE);
		return T_HTML_TAG_ATTR_EQUAL_SIGN;
	}

	{WHITE_SPACE} {
		return T_WHITESPACE;
	}

	[^] {
		rollbackMatch();
		popState(SCRIPT_TAG, STYLE_TAG, HTML_TAG);
	}
}

<NETTE_ATTR_VALUE> {
	['] {
		pushState(NETTE_ATTR_SQ);
		return T_HTML_TAG_ATTR_SQ;
	}

	[\"] {
		pushState(NETTE_ATTR_DQ);
		return T_HTML_TAG_ATTR_DQ;
	}

	[^ \t\r\n/>{'\"][^ \t\r\n/>{]* {
		popState(NETTE_ATTR);
		popState(SCRIPT_TAG, STYLE_TAG, HTML_TAG);
		return T_MACRO_CONTENT;
	}

	{WHITE_SPACE} {
		return T_WHITESPACE;
	}

	[^] {
		rollbackMatch();
		popState(NETTE_ATTR);
		popState(SCRIPT_TAG, STYLE_TAG, HTML_TAG);
	}
}

<NETTE_ATTR_SQ> {
	['] {
		popState(NETTE_ATTR_VALUE);
		popState(NETTE_ATTR);
		popState(SCRIPT_TAG, STYLE_TAG, HTML_TAG);
		return T_HTML_TAG_ATTR_SQ;
	}

	[^']+ {
		return T_MACRO_CONTENT;
	}
}

<NETTE_ATTR_DQ> {
	[\"] {
		popState(NETTE_ATTR_VALUE);
		popState(NETTE_ATTR);
		popState(SCRIPT_TAG, STYLE_TAG, HTML_TAG);
		return T_HTML_TAG_ATTR_DQ;
	}

	[^\"]+ {
		return T_MACRO_CONTENT;
	}
}

<HTML_ATTR> {
	"=" / [ \t\r\n]* [^ \t\r\n/>{] {
		pushState(HTML_ATTR_VALUE);
		return T_TEXT;
	}

	{WHITE_SPACE} {
		return T_TEXT;
	}

	[^] {
		rollbackMatch();
		popState(SCRIPT_TAG, STYLE_TAG, HTML_TAG);
	}
}

<HTML_ATTR_VALUE> {
	['] {
		pushState(HTML_ATTR_SQ);
		return T_TEXT;
	}

	[\"] {
		pushState(HTML_ATTR_DQ);
		return T_TEXT;
	}

	[^ \t\r\n/>{'\"][^ \t\r\n/>{]* {
		popState(HTML_ATTR);
		popState(SCRIPT_TAG, STYLE_TAG, HTML_TAG);
		return T_TEXT;
	}

	{WHITE_SPACE} {
		return T_TEXT;
	}

	[^] {
		rollbackMatch();
		popState(HTML_ATTR);
		popState(SCRIPT_TAG, STYLE_TAG, HTML_TAG);
	}
}

<HTML_ATTR_SQ> {
	['] {
		popState(HTML_ATTR_VALUE);
		popState(HTML_ATTR);
		popState(SCRIPT_TAG, STYLE_TAG, HTML_TAG);
		return T_TEXT;
	}

	[^'{]+ {
		return T_TEXT;
	}

	"{" {
		return T_TEXT;
	}
}

<HTML_ATTR_DQ> {
	[\"] {
		popState(HTML_ATTR_VALUE);
		popState(HTML_ATTR);
		popState(SCRIPT_TAG, STYLE_TAG, HTML_TAG);
		return T_TEXT;
	}

	[^\"{]+ {
		return T_TEXT;
	}

	"{" {
		return T_TEXT;
	}
}

<HTML_COMMENT> {
	"-->" {
		popState(HTML_TEXT);
		return T_TEXT;
	}

	[^] {
		return T_TEXT;
	}
}


<HTML_TEXT, HTML_OPEN_TAG_OPEN, HTML_CLOSE_TAG_OPEN, HTML_TAG, SCRIPT_TAG, SCRIPT_CDATA, STYLE_TAG, STYLE_CDATA, NETTE_ATTR, NETTE_ATTR_SQ, NETTE_ATTR_DQ, HTML_ATTR, HTML_ATTR_SQ, HTML_ATTR_DQ, HTML_COMMENT> {
	[^] {
		// throw new RuntimeException('Lexer failed');
		return com.intellij.psi.TokenType.BAD_CHARACTER;
	}
}
