package com.jantvrdik.intellij.latte.lexer;

import com.intellij.psi.tree.IElementType;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

%%

%class LatteMacroContentLexer
%extends LatteBaseFlexLexer
%function advance
%type IElementType
%unicode
%ignorecase


%state SINGLE_QUOTED
%state DOUBLE_QUOTED

SYMBOL = [_[:letter:]][_0-9[:letter:]]*(-[_0-9[:letter:]]+)* //todo: unicode letters
IDENTIFIER = [a-zA-Z_][a-zA-Z0-9_]*
WHITE_SPACE=[ \t\r\n]+

%%


<YYINITIAL> {

	{WHITE_SPACE} {
		return T_WHITESPACE;
	}

	"$" [a-zA-Z_][a-zA-Z0-9_]* {
		return T_MACRO_ARGS_VAR;
	}

	"$" {
		return T_MACRO_ARGS_DOLLAR;
	}

	"::" {
		return T_MACRO_ARGS_PAAMAYIM_NEKUDOTAYIM;
	}

	":" {
		return T_MACRO_ARGS_COLON;
	}

	"??" {
		return T_MACRO_ARGS_COALESCE;
	}

	"?" {
		return T_MACRO_ARGS_QUESTION_MARK;
	}

	"->" {
		return T_MACRO_ARGS_OBJECT_OPERATOR;
	}

	"(" {
		return T_MACRO_ARGS_LEFT_BRACKET;
	}

	")" {
		return T_MACRO_ARGS_RIGHT_BRACKET;
	}

	"{" {
		return T_MACRO_ARGS_LEFT_CURLY_BRACKET;
	}
	"}" {
		return T_MACRO_ARGS_RIGHT_CURLY_BRACKET;
	}
	"[" {
		return T_MACRO_ARGS_LEFT_SQUARE_BRACKET;
	}
	"]" {
		return T_MACRO_ARGS_RIGHT_SQUARE_BRACKET;
	}

	"," {
		return T_MACRO_ARGS_COMMA;
	}

	"=>" {
		return T_MACRO_ARGS_DOUBLE_ARROW;
	}

	[+-/*%&] | "===" | "!==" | "==" | "!=" | ">" | "<" | ">=" | "<="  | "&&" | "||" | "<=>" | ">>" | "<<" | "&" | "." {
		return T_MACRO_ARGS_BINARY_OPERATOR;
	}

	"|" {
		return T_MACRO_ARGS_MODIFIERS;
	}

	"++" {
		return T_MACRO_ARGS_INC;
	}
	"--" {
		return T_MACRO_ARGS_DEC;
	}
	"@" {
		return T_MACRO_ARGS_SHUTUP;
	}
	"!" {
		return T_MACRO_ARGS_NOT;
	}
	"~" {
		return T_MACRO_ARGS_NEG;
	}
	"..." {
		return T_MACRO_ARGS_ELLIPSIS;
	}

	"instanceof" {
		return T_MACRO_ARGS_INSTANCEOF;
	}

	"new" {
		return T_MACRO_ARGS_NEW;
	}

	"clone" {
		return T_MACRO_ARGS_CLONE;
	}

	"array" / "(" {
		return T_MACRO_ARGS_ARRAY;
	}

	"isset" / "(" {
		return T_MACRO_ARGS_ISSET;
	}

	"empty" / "(" {
		return T_MACRO_ARGS_EMPTY;
	}

	"list" / "(" {
		return T_MACRO_ARGS_LIST;
	}

	{IDENTIFIER} / ("(" | "::") {
		return T_MACRO_ARGS_IDENTIFIER;
	}

	"'"  {
		pushState(SINGLE_QUOTED);
		return T_MACRO_ARGS_SINGLE_QUOTE_LEFT;
	}

	"\"" {
		pushState(DOUBLE_QUOTED);

		return T_MACRO_ARGS_DOUBLE_QUOTE_LEFT;
	}

	[0-9]+ {
		return T_MACRO_ARGS_NUMBER;
	}

	{SYMBOL} {
		return T_MACRO_ARGS_SYMBOL;
	}

	[^] {
		return T_MACRO_ARGS_OTHER;
	}

}


<SINGLE_QUOTED> {
	"'" {
		pushState(YYINITIAL);
		return T_MACRO_ARGS_SINGLE_QUOTE_RIGHT;
	}
	("\\" [^] | [^'\\])+ {
		return T_MACRO_ARGS_STRING;
	}
}

<DOUBLE_QUOTED> {
	"\"" {
		pushState(YYINITIAL);
		return T_MACRO_ARGS_DOUBLE_QUOTE_RIGHT;
	}
	("\\" [^] | [^\"\\$]) {
		return T_MACRO_ARGS_STRING;
	}
	"$" [a-zA-Z_][a-zA-Z0-9_]* {
    	return T_MACRO_ARGS_VAR;
    }
    "$" {
		return T_MACRO_ARGS_STRING;
    }
}
