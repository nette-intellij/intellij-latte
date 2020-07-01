package com.jantvrdik.intellij.latte.settings.xml.elements;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;

public interface LatteTagArgumentDomElement extends DomElement {
    @Attribute("name")
    LatteNameAttributeValue getNameAttributeAttribute();

    @Attribute("types")
    LatteStringAttributeValue getTypesAttribute();
}
