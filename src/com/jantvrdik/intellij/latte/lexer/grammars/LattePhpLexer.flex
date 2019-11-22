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

STRING = {STRING_SQ} | {STRING_DQ}
STRING_SQ = "'" ("\\" [^] | [^'\\])* "'"
STRING_DQ = "\"" ("\\" [^] | [^\"\\])* "\""
WHITE_SPACE=[ \t\r\n]+
VAR_STRING=[a-zA-Z_][a-zA-Z0-9_]*

%%

<YYINITIAL> {

	"$" {VAR_STRING} {
        return T_MACRO_ARGS_VAR;
    }

    "\\" [a-zA-Z_][a-zA-Z0-9_\\]* {
        return T_PHP_VAR_TYPE;
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

	{STRING} {
		return T_MACRO_ARGS_STRING;
	}

    ("<=>" | "<>" | "<=" | ">=" | "<" | ">" | "==" | "===" | "\!=" | "\==") {
        return T_PHP_OPERATOR;
    }

    ("class" | "false" | "true" | "null" | "break" | "continue" | "case" | "default" | "die" | "exit" | "do" | "while" | "foreach" | "for" | "function" | "echo" | "print" | "catch" | "finally" | "try" | "instanceof" | "if" | "else" | "elseif" | "endif" | "endforeach" | "endfor" | "endwhile" | "endswitch" | "isset" | "or" | "new" | "switch" | "use") {
        return T_PHP_KEYWORD;
    }

    "as" {
        return T_PHP_AS;
    }

    {VAR_STRING} {
        return T_PHP_METHOD;
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