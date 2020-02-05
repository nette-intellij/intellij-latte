package com.jantvrdik.intellij.latte.utils;

public class LatteMimeTypes {

    final private static String[] defaultMimeTypes = new String[]{
            "text/html",
            "text/xml",
            "application/xml",
            "text/css",
            "text/calendar",
            "text/javascript",
            "application/javascript",
            "text/plain",
    };

    public static String[] getDefaultMimeTypes() {
        return defaultMimeTypes;
    }
}