package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpConstantStub;
import com.jantvrdik.intellij.latte.php.LattePhpTypeDetector;
import com.jantvrdik.intellij.latte.php.NettePhpType;

public interface LattePhpConstantElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpConstantStub> {

	default NettePhpType getPrevReturnType() {
		return LattePhpTypeDetector.detectPrevPhpType(this);
	}

	String getConstantName();

}