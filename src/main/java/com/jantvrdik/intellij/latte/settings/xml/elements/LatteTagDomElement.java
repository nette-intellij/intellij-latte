package com.jantvrdik.intellij.latte.settings.xml.elements;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;

public interface LatteTagDomElement extends DomElement {
    @Attribute("name")
    //@NameValue
    //@Resolve(soft = true)
    LatteNameAttributeValue getNameAttribute();
}
