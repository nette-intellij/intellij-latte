package com.jantvrdik.intellij.latte.utils;

import com.intellij.psi.tree.TokenSet;

import java.util.Arrays;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LatteHtmlUtil {
    public static TokenSet HTML_TOKENS = TokenSet.create(T_TEXT, T_HTML_CLOSE_TAG_OPEN, T_HTML_OPEN_TAG_OPEN, T_HTML_OPEN_TAG_CLOSE, T_HTML_TAG_CLOSE);

    final private static String[] voidTags = new String[]{
            "area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr", "!doctype",
            "style", "script" // style and script are here only because LatteTopLexer.flex implementation
    };

    public static boolean isVoidTag(String tagName) {
        return Arrays.asList(voidTags).contains(tagName.toLowerCase());
    }
}