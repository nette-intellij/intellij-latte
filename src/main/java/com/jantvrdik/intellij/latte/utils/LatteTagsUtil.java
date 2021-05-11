package com.jantvrdik.intellij.latte.utils;

import com.jantvrdik.intellij.latte.settings.LatteTagSettings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LatteTagsUtil {

    public enum Type {

        BLOCK("block"),
        DEFINE("define"),
        FOREACH("foreach"),
        ELSE("else"),
        ELSEIF("elseif"),
        ELSEIFSET("elseifset"),
        FOR("for"),
        IF("if"),
        IFCHANGED("ifchanged"),
        IFSET("ifset"),
        LINK("link"),
        N_HREF("n:href"),
        PLINK("plink"),
        SNIPPET("snippet"),
        SNIPPETAREA("snippetArea"),
        TEMPLATE_TYPE("templateType"),
        VAR("var"),
        VAR_TYPE("varType"),
        WHILE("while");

        final private String tagName;

        Type(@NotNull String tagName) {
            this.tagName = tagName;
        }

        public String getTagName() {
            return tagName;
        }

    }

    public static final List<String> LINK_TAGS_LIST = Arrays.asList(
            Type.LINK.getTagName(),
            Type.PLINK.getTagName(),
            Type.N_HREF.getTagName()
    );
    public static final List<String> CONTEXT_TAGS_LIST = Arrays.asList(
            Type.FOREACH.getTagName(),
            Type.FOR.getTagName(),
            Type.BLOCK.getTagName(),
            Type.DEFINE.getTagName(),
            Type.SNIPPET.getTagName(),
            Type.SNIPPETAREA.getTagName(),
            Type.IF.getTagName(),
            Type.IFCHANGED.getTagName(),
            Type.ELSE.getTagName(),
            Type.ELSEIF.getTagName(),
            Type.ELSEIFSET.getTagName(),
            Type.WHILE.getTagName(),
            Type.IFSET.getTagName()
    );
    public static final List<String> TYPE_TAGS_LIST = Arrays.asList(Type.VAR_TYPE.getTagName(), Type.TEMPLATE_TYPE.getTagName(), Type.VAR.getTagName());

    private static List<String> CONTEXT_NETTE_ATTRIBUTES_LIST = null;

    public static boolean isContextTag(@NotNull String tagName) {
        return CONTEXT_TAGS_LIST.contains(tagName);
    }

    public static boolean isContextNetteAttribute(@NotNull String tagName) {
        if (CONTEXT_NETTE_ATTRIBUTES_LIST == null) {
            CONTEXT_NETTE_ATTRIBUTES_LIST = new ArrayList<>();
            for (String currentTagName : CONTEXT_TAGS_LIST) {
                CONTEXT_NETTE_ATTRIBUTES_LIST.addAll(
                    LatteTagSettings.createNetteAttributes(currentTagName, true, true)
                );
            }
        }
        return CONTEXT_NETTE_ATTRIBUTES_LIST.contains(tagName);
    }

}