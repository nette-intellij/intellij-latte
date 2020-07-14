package com.jantvrdik.intellij.latte.settings.xml.elements;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.Nullable;

public interface LatteRootDomElement extends DomElement {
	@Attribute("vendor")
	LatteStringAttributeValue getVendorAttribute();

	@Nullable LatteTagsDomElement getTags();

	@Nullable LatteFiltersDomElement getFilters();

	@Nullable LatteVariablesDomElement getVariables();

	@Nullable LatteFunctionsDomElement getFunctions();
}
