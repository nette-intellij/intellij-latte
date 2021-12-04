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
%state PHP_TYPE_PART
%state CLASS_REFERENCE
%state CLASS_REFERENCE_TYPE

WHITE_SPACE=[ \t\r\n]+

// numbers
BIN_NUMBER = [+-]?0[bB][01]+*
OCT_NUMBER = [+-]?0[oO][1-7][0-7]*
HEX_NUMBER = [+-]?0[xX][0-9a-fA-F]+
NUMBER = [+-]?[0-9]+(\.[0-9]+)?([Ee][+-]?[0-9]+)?

// identifiers
IDENTIFIER=[a-zA-Z_][a-zA-Z0-9_]*
CLASS_NAME=\\?[a-zA-Z_][a-zA-Z0-9_]*\\[a-zA-Z_][a-zA-Z0-9_\\]* | \\[a-zA-Z_][a-zA-Z0-9_]*
CONTENT_TYPE=[a-zA-Z\-][a-zA-Z0-9\-]*\/[a-zA-Z\-][a-zA-Z0-9\-\.]*

// keywords
TYPES=("string" | "int" | "bool" | "object" | "float" | "array" | "callable" | "iterable" | "void")
KEYWORD=(class | "false" | "true" | "break" | "continue" | "case" | "default" | "die" | "exit" | "do" | "while" | "foreach" | "for" | "function" | "echo" | "print" | "catch" | "finally" | "try" | "instanceof" | "if" | "else" | "elseif" | "endif" | "endforeach" | "endfor" | "endwhile" | "endswitch" | "isset" | "or" | "switch" | "use")
NULL="null"
MIXED="mixed"
NEW="new"
AS="as"

%%

<YYINITIAL> {

	"$" {IDENTIFIER} {
        return T_MACRO_ARGS_VAR;
    }

    "\\" / {IDENTIFIER} {
        yybegin(CLASS_REFERENCE);
        return T_PHP_NAMESPACE_RESOLUTION;
    }

    {IDENTIFIER} / ("\\") {
        yybegin(CLASS_REFERENCE);
        return T_PHP_NAMESPACE_REFERENCE;
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

    ("<>" | "==" | "===" | "\!==" | "\!=" | "\==") {
        return T_PHP_OPERATOR;
    }

    ("<=>" | "<=" | ">=" | "<" | ">") {
        return T_PHP_RELATIONAL_OPERATOR;
    }

    ("(bool)" | "(boolean)" | "(array)" | "(real)" | "(double)" | "(float)" | "(int)" | "(integer)" | "(object)" | "(string)" | "(unset)") {
        return T_PHP_CAST;
    }

    ("<<" | ">>") {
        return T_PHP_SHIFT_OPERATOR;
    }

    ("--" | "++") {
        return T_PHP_UNARY_OPERATOR;
    }

    ("**=" | "??" | "..." | "<<<" | "<<=" | ">>=") {
        return T_PHP_EXPRESSION;
    }

    ("||" | "&&") {
        return T_PHP_LOGIC_OPERATOR;
    }

    (".=" | "^=" | "-=" | "+=" | "%=" | "*=" | "|=" | "&=" | "/=") {
        return T_PHP_ASSIGNMENT_OPERATOR;
    }

    ("+" | "-") {
        return T_PHP_ADDITIVE_OPERATOR;
    }

    "^" {
        return T_PHP_BITWISE_OPERATOR;
    }

    "&" {
        return T_PHP_REFERENCE_OPERATOR;
    }

    "." {
        return T_PHP_CONCATENATION;
    }

    ("**" | "/" | "*" | "%") {
        return T_PHP_MULTIPLICATIVE_OPERATORS;
    }

    ":" {
        return T_PHP_COLON;
    }

    "?" {
        return T_PHP_NULL_MARK;
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

    {NEW} {
        return T_PHP_NEW;
    }

    "array" / {WHITE_SPACE}? "(" {
        return T_PHP_ARRAY;
    }

    {TYPES} {
        return T_PHP_TYPE;
    }

    "|" / ({AS} | {KEYWORD} | {NULL} | {MIXED} | {TYPES} | {CLASS_NAME}) {
        yybegin(PHP_TYPE_PART);
        return T_PHP_OR_INCLUSIVE;
    }

    "|" / {IDENTIFIER} {
        yybegin(MACRO_FILTERS);
        return T_PHP_MACRO_SEPARATOR;
    }

    "|" {
        return T_PHP_OR_INCLUSIVE;
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

	{NUMBER} | {BIN_NUMBER} | {HEX_NUMBER} | {OCT_NUMBER} {
		return T_MACRO_ARGS_NUMBER;
	}

    {WHITE_SPACE} {
        return T_WHITESPACE;
    }

    [^] {
        return T_MACRO_ARGS;
    }

}

<PHP_TYPE_PART> {
	"\\" / {IDENTIFIER} {
        yybegin(CLASS_REFERENCE_TYPE);
        return T_PHP_NAMESPACE_RESOLUTION;
    }

    {IDENTIFIER} / ("\\") {
        yybegin(CLASS_REFERENCE_TYPE);
        return T_PHP_NAMESPACE_REFERENCE;
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
        yybegin(CLASS_REFERENCE);
        return T_PHP_IDENTIFIER;
    }

    "|" {
        pushState(YYINITIAL);
        return T_PHP_OR_INCLUSIVE;
    }
}

<CLASS_REFERENCE> {
    {IDENTIFIER} {
        pushState(YYINITIAL);
        return T_PHP_NAMESPACE_REFERENCE;
    }

    "\\" {
        pushState(YYINITIAL);
        return T_PHP_NAMESPACE_RESOLUTION;
    }
}

<CLASS_REFERENCE_TYPE> {
    {IDENTIFIER} {
        pushState(YYINITIAL);
        return T_PHP_NAMESPACE_REFERENCE;
    }

    "\\" {
        pushState(YYINITIAL);
        return T_PHP_NAMESPACE_RESOLUTION;
    }
}

<MACRO_FILTERS> {
    {IDENTIFIER} {
        pushState(YYINITIAL);
        return T_MACRO_FILTERS;
    }

    "|" {
        pushState(YYINITIAL);
        return T_PHP_MACRO_SEPARATOR;
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

	("\\" [^] | [^\"\\$])+ {
		return T_MACRO_ARGS_STRING;
	}

	"$" {IDENTIFIER} {
    	return T_MACRO_ARGS_VAR;
    }

    "$" {
		return T_MACRO_ARGS_STRING;
    }
}