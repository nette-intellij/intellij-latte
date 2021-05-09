package com.jantvrdik.intellij.latte.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class LatteTagsUtil {

    public enum Type {

        VAR("var"),
        VAR_TYPE("varType"),
        LINK("link"),
        PLINK("plink"),
        N_HREF("n:href"),
        TEMPLATE_TYPE("templateType");

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
    public static final List<String> TYPE_TAGS_LIST = Arrays.asList(Type.VAR_TYPE.getTagName(), Type.TEMPLATE_TYPE.getTagName(), Type.VAR.getTagName());

}