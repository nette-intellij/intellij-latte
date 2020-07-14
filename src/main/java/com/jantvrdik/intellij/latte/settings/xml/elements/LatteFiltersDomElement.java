package com.jantvrdik.intellij.latte.settings.xml.elements;

import com.intellij.util.xml.DomElement;

import java.util.List;

public interface LatteFiltersDomElement extends DomElement {
	List<LatteFilterDomElement> getFilters();
}
