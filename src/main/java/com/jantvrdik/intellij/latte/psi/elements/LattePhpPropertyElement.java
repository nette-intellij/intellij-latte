package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpPropertyStub;
import com.jantvrdik.intellij.latte.php.LattePhpTypeDetector;
import com.jantvrdik.intellij.latte.php.NettePhpType;

public interface LattePhpPropertyElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpPropertyStub> {

	default NettePhpType getPrevReturnType() {
		return LattePhpTypeDetector.detectPrevPhpType(this);
	}

	public abstract String getPropertyName();

}