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


STRING = {STRING_SQ} | {STRING_DQ}
STRING_SQ = "'" ("\\" [^] | [^'\\])* "'"
STRING_DQ = "\"" ("\\" [^] | [^\"\\])* "\""
SYMBOL = [_[:letter:]][_0-9[:letter:]]*(-[_0-9[:letter:]]+)* //todo: unicode letters
FUNCTION_CALL=[a-zA-Z_][a-zA-Z0-9_]* "("

%%


<YYINITIAL> {

	("\\" | "$" | {FUNCTION_CALL}) .+ {
        return T_PHP_CONTENT;
    }

	({STRING} | [0-9]+ | {SYMBOL}) {
		return T_PHP_CONTENT;
	}

	[^] {
		return T_MACRO_ARGS;
	}
}