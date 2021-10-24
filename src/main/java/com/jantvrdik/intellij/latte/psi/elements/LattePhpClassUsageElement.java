package com.jantvrdik.intellij.latte.psi.elements;

public interface LattePhpClassUsageElement extends BaseLattePhpElement {

	String getClassName();

	boolean isTemplateType();

	void reset();

}