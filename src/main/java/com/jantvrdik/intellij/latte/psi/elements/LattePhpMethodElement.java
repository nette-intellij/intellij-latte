package com.jantvrdik.intellij.latte.psi.elements;

import com.jantvrdik.intellij.latte.php.LattePhpTypeDetector;
import com.jantvrdik.intellij.latte.php.NettePhpType;

public interface LattePhpMethodElement extends BaseLattePhpElement {

	default NettePhpType getPrevReturnType() {
		return LattePhpTypeDetector.detectPrevPhpType(this);
	}

	String getMethodName();

	boolean isStatic();

	boolean isFunction();

}