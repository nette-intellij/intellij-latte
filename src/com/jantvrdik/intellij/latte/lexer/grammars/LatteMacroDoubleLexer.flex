package com.jantvrdik.intellij.latte.lexer;

import com.intellij.psi.tree.IElementType;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

%%

%class LatteMacroDoubleLexer
%extends LatteBaseFlexLexer
%function advance
%type IElementType
%unicode
%ignorecase

%state NAME_ANY
%state NAME_NOT_Q
%state NAME_SHORT
%state ARGS

NAME_FULL = [a-zA-Z][a-zA-Z0-9_]* ([.:][a-zA-Z0-9_]+)*
MODIFIERS = [a-zA-Z] ({STRING} | [^'\"])*
STRING = {STRING_SQ} | {STRING_DQ}
STRING_SQ = "'" ("\\" [^] | [^'\\])* "'"
STRING_DQ = "\"" ("\\" [^] | [^\"\\])* "\""

%%
<YYINITIAL> {
	"{{" {
		yybegin(NAME_ANY);
		return T_MACRO_OPEN_TAG_OPEN;
	}

	"{{/" {
		yybegin(NAME_NOT_Q);
		return T_MACRO_CLOSE_TAG_OPEN;
	}
}

<NAME_ANY> {
	"?" {
		yybegin(ARGS);
		return T_MACRO_NAME;
	}
}

<NAME_ANY, NAME_NOT_Q> {
	"!" {
		yybegin(NAME_SHORT);
		return T_MACRO_NOESCAPE;
	}

	{NAME_FULL} {
		yybegin(ARGS);
		return T_MACRO_NAME;
	}

	{NAME_FULL} ("::" | "(" | "\\") {
		yybegin(ARGS);
		return T_MACRO_CONTENT;
	}
}

<NAME_ANY, NAME_NOT_Q, NAME_SHORT> {
	[=~#%\^&_] {
		yybegin(ARGS);
		return T_MACRO_SHORTNAME;
	}
}

<NAME_ANY, NAME_NOT_Q, NAME_SHORT, ARGS> {
	"}}" {
		yybegin(ARGS);
		return T_MACRO_TAG_CLOSE;
	}

	"}}" / [^] {
		yybegin(ARGS);
		return T_MACRO_CONTENT;
	}

	"/}}" {
		yybegin(ARGS);
		return T_MACRO_TAG_CLOSE_EMPTY;
	}

	"/}}" / [^] {
		yybegin(ARGS);
		return T_MACRO_CONTENT;
	}

	"|" / {MODIFIERS} "/"? "}}" {
		yybegin(ARGS);
		return T_MACRO_MODIFIERS;
	}

	"|" / {MODIFIERS} "/"? "}}" [^] {
		yybegin(ARGS);
		return T_MACRO_CONTENT;
	}

	[^] {
		yybegin(ARGS);
		return T_MACRO_CONTENT;
	}
}
