package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpMethodStub;

public interface LattePhpMethodElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpMethodStub> {

	public abstract String getMethodName();

}