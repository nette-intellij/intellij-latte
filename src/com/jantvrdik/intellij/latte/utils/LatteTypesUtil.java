package com.jantvrdik.intellij.latte.utils;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class LatteTypesUtil {

    final private static String[] nativeClassConstants = new String[]{"class"};

    final private static String[] nativeTypeHints = new String[]{"string", "int", "bool", "object", "float", "array", "mixed", "null", "callable", "iterable"};

    final private static String[] excludedCompletion = new String[]{"__construct", "__callstatic", "__call", "__get", "__isset", "__clone", "__set", "__unset"};

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

    public static boolean isNativeTypeHint(@NotNull String value) {
        value = value.toLowerCase();
        return Arrays.asList(nativeTypeHints).contains(value.startsWith("\\") ? value.substring(1) : value);
    }

    public static boolean isExcludedCompletion(@NotNull String value) {
        return Arrays.asList(excludedCompletion).contains(value.toLowerCase());
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