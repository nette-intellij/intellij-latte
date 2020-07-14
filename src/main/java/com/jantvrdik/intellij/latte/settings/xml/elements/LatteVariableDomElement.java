package com.jantvrdik.intellij.latte.settings.xml.elements;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Resolve;

public interface LatteVariableDomElement extends DomElement {
    @Attribute("name")
    @NameValue
    @Resolve(soft = true)
    LatteNameAttributeValue getNameAttribute();
}
