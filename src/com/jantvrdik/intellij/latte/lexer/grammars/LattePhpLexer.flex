package com.jantvrdik.intellij.latte.lexer;

import com.intellij.psi.tree.IElementType;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

%%

%class LattePhpLexer
%extends LatteBaseFlexLexer
%function advance
%type IElementType
%unicode
%ignorecase

%state SINGLE_QUOTED
%state DOUBLE_QUOTED
%state MACRO_FILTERS

WHITE_SPACE=[ \t\r\n]+
IDENTIFIER=[a-zA-Z_][a-zA-Z0-9_]*
CLASS_NAME=\\?[a-zA-Z_][a-zA-Z0-9_]*\\[a-zA-Z_][a-zA-Z0-9_\\]* | \\[a-zA-Z_][a-zA-Z0-9_]*
CONTENT_TYPE=[a-zA-Z\-][a-zA-Z0-9\-]*\/[a-zA-Z\-][a-zA-Z0-9\-\.]*
TYPES=("string" | "int" | "bool" | "object" | "float" | "array" | "callable" | "iterable" | "void")
KEYWORD=(class | "false" | "true" | "break" | "continue" | "case" | "default" | "die" | "exit" | "do" | "while" | "foreach" | "for" | "function" | "echo" | "print" | "catch" | "finally" | "try" | "instanceof" | "if" | "else" | "elseif" | "endif" | "endforeach" | "endfor" | "endwhile" | "endswitch" | "isset" | "or" | "new" | "switch" | "use")
NULL="null"
MIXED="mixed"
AS="as"

%%

<YYINITIAL> {

	"$" {IDENTIFIER} {
        return T_MACRO_ARGS_VAR;
    }

    {CLASS_NAME} {
        return T_PHP_CLASS_NAME;
    }

    {CONTENT_TYPE} {
        return T_PHP_CONTENT_TYPE;
    }

    "::" {
        return T_PHP_DOUBLE_COLON;
    }

    "=>" {
        return T_PHP_DOUBLE_ARROW;
    }

    "->" {
        return T_PHP_OBJECT_OPERATOR;
    }

    "(" {
        return T_PHP_LEFT_NORMAL_BRACE;
    }

    ")" {
        return T_PHP_RIGHT_NORMAL_BRACE;
    }

    "{" {
        return T_PHP_LEFT_CURLY_BRACE;
    }

    "}" {
        return T_PHP_RIGHT_CURLY_BRACE;
    }

    "[" {
        return T_PHP_LEFT_BRACKET;
    }

    "]" {
        return T_PHP_RIGHT_BRACKET;
    }

    ("<=>" | "<>" | "<=" | ">=" | "<" | ">" | "==" | "===" | "\!=" | "\==") {
        return T_PHP_OPERATOR;
    }

    ("(bool)" | "(boolean)" | "(array)" | "(real)" | "(double)" | "(float)" | "(int)" | "(integer)" | "(object)" | "(string)" | "(unset)") {
        return T_PHP_CAST;
    }

    ("||" | "&&" | "**=" | "**" | ".=" | "^=" | "-=" | "+=" | "%=" | "*=" | "|=" | "&=" | "??" | "--" | "/=" | "..." | "++" | "<<<" | "<<=" | ">>=" | "<<" | ">>") {
        return T_PHP_EXPRESSION;
    }

    ("+" | "-" | "?" | ":" | "&" | "." | "*" | "/") {
        return T_PHP_EXPRESSION;
    }

    "=" {
        return T_PHP_DEFINITION_OPERATOR;
    }

    {AS} {
        return T_PHP_AS;
    }

    {KEYWORD} {
        return T_PHP_KEYWORD;
    }

    {NULL} {
        return T_PHP_NULL;
    }

    {MIXED} {
        return T_PHP_MIXED;
    }

    {TYPES} {
        return T_PHP_TYPE;
    }

    "|" / ({IDENTIFIER} | {CLASS_NAME}) {
        yybegin(MACRO_FILTERS);
        return T_PHP_OR_INCLUSIVE;
    }

    "|" {
        return T_PHP_OR_INCLUSIVE;
    }

    {IDENTIFIER} / ("(") {
        return T_PHP_METHOD;
    }

    {IDENTIFIER} {
        return T_PHP_IDENTIFIER;
    }

    "'"  {
    	pushState(SINGLE_QUOTED);
    	return T_PHP_SINGLE_QUOTE_LEFT;
    }

    "\"" {
        pushState(DOUBLE_QUOTED);
        return T_PHP_DOUBLE_QUOTE_LEFT;
    }

	[0-9]+ {
		return T_MACRO_ARGS_NUMBER;
	}

    {WHITE_SPACE} {
        return T_WHITESPACE;
    }

    [^] {
        return T_MACRO_ARGS;
    }

}

<MACRO_FILTERS> {
	{CLASS_NAME} {
        pushState(YYINITIAL);
        return T_PHP_CLASS_NAME;
    }

	{AS} {
        pushState(YYINITIAL);
        return T_PHP_AS;
    }

	{KEYWORD} {
        pushState(YYINITIAL);
        return T_PHP_KEYWORD;
    }

    {NULL} {
        pushState(YYINITIAL);
        return T_PHP_NULL;
    }

    {MIXED} {
        pushState(YYINITIAL);
        return T_PHP_MIXED;
    }

    {TYPES} {
        pushState(YYINITIAL);
        return T_PHP_TYPE;
    }

    {IDENTIFIER} {
        pushState(YYINITIAL);
        return T_MACRO_FILTERS;
    }

    "|" {
        pushState(YYINITIAL);
        return T_PHP_OR_INCLUSIVE;
    }
}

<SINGLE_QUOTED> {
	"'" {
		pushState(YYINITIAL);
		return T_PHP_SINGLE_QUOTE_RIGHT;
	}
	("\\" [^] | [^'\\])+ {
		return T_MACRO_ARGS_STRING;
	}
}

<DOUBLE_QUOTED> {
	"\"" {
		pushState(YYINITIAL);
		return T_PHP_DOUBLE_QUOTE_RIGHT;
	}

	("\\" [^] | [^\"\\$]) {
		return T_MACRO_ARGS_STRING;
	}

	"$" {IDENTIFIER} {
    	return T_MACRO_ARGS_VAR;
    }

    "$" {
		return T_MACRO_ARGS_STRING;
    }
}