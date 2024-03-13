package com.jantvrdik.intellij.latte.psi.elements;

import com.jantvrdik.intellij.latte.psi.LattePhpClassUsage;
import org.jetbrains.annotations.NotNull;

public interface LattePhpClassReferenceElement extends BaseLattePhpElement {

	String getClassName();

	@NotNull
	LattePhpClassUsage getPhpClassUsage();

}