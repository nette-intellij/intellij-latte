package com.jantvrdik.intellij.latte.utils;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

public class LatteTypesUtil {

    final private static String[] nativeClassConstants = new String[]{"class"};

    final private static String[] nativeTypeHints = new String[]{"string", "int", "bool", "object", "float", "array", "mixed", "null"};

    final private static TokenSet whitespaceTokens = TokenSet.create(LatteTypes.T_WHITESPACE, TokenType.WHITE_SPACE);

    final private static TokenSet typeHintTokens = TokenSet.create(LatteTypes.T_PHP_TYPE, LatteTypes.PHP_CLASS, LatteTypes.T_PHP_NULL, LatteTypes.T_PHP_MIXED);

    final private static TokenSet typeHintOperatorTokens = TokenSet.create(
            LatteTypes.T_PHP_OR_INCLUSIVE,
            LatteTypes.T_PHP_LEFT_BRACKET,
            LatteTypes.T_PHP_RIGHT_BRACKET
    );

    public static String[] getNativeClassConstants() {
        return nativeClassConstants;
    }

    public static String[] getNativeTypeHints() {
        return nativeTypeHints;
    }

    public static TokenSet getTypeHintTokens() {
        return typeHintTokens;
    }

    public static TokenSet getAllTypeHintTokens() {
        return TokenSet.orSet(typeHintOperatorTokens, typeHintTokens, whitespaceTokens);
    }

    public static TokenSet getAllSkippedHintTokens() {
        return TokenSet.orSet(typeHintOperatorTokens, whitespaceTokens);
    }

}