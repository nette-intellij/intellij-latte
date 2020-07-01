package com.jantvrdik.intellij.latte.settings.xml.elements;

import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface LatteStringAttributeValue extends GenericAttributeValue<String> {

    @NotNull
    default String getAttributeValue() {
        String value = getValue();
        return value != null ? value : "";
    }

}
