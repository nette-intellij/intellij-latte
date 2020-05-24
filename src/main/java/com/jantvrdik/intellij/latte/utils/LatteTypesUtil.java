package com.jantvrdik.intellij.latte.utils;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class LatteTypesUtil {

    final private static String[] nativeClassConstants = new String[]{"class"};

    final private static String[] nativeTypeHints = new String[]{"string", "int", "bool", "object", "float", "array", "mixed", "null", "callable", "iterable"};

    final private static String[] iterableInterfaces = new String[]{"\\Iterator", "\\Generator"};

    final private static String[] nativeIterableTypeHints = new String[]{"array", "iterable"};

    final private static String[] excludedCompletion = new String[]{"__construct", "__callstatic", "__call", "__get", "__isset", "__clone", "__set", "__unset"};

    final public static TokenSet whitespaceTokens = TokenSet.create(LatteTypes.T_WHITESPACE, TokenType.WHITE_SPACE);

    public static String[] getNativeClassConstants() {
        return nativeClassConstants;
    }

    public static String[] getNativeTypeHints() {
        return nativeTypeHints;
    }

    public static String[] getIterableInterfaces() {
        return iterableInterfaces;
    }

    public static boolean isNativeTypeHint(@NotNull String value) {
        return Arrays.asList(nativeTypeHints).contains(normalizeTypeHint(value));
    }

    public static boolean isIterable(@NotNull String value) {
        return Arrays.asList(nativeIterableTypeHints).contains(normalizeTypeHint(value));
    }

    public static boolean isNull(@NotNull String value) {
        return "null".equals(normalizeTypeHint(value));
    }

    public static boolean isMixed(@NotNull String value) {
        return "mixed".equals(normalizeTypeHint(value));
    }

    public static boolean isExcludedCompletion(@NotNull String value) {
        return Arrays.asList(excludedCompletion).contains(value.toLowerCase());
    }

    public static String normalizeTypeHint(String value) {
        value = value.toLowerCase();
        return value.startsWith("\\") ? value.substring(1) : value;
    }

}