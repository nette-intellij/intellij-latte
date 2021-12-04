package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpStaticVariableStub;
import com.jantvrdik.intellij.latte.php.LattePhpTypeDetector;
import com.jantvrdik.intellij.latte.php.NettePhpType;

public interface LattePhpStaticVariableElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpStaticVariableStub> {

	default NettePhpType getPrevReturnType() {
		return LattePhpTypeDetector.detectPrevPhpType(this);
	}

	String getVariableName();

}