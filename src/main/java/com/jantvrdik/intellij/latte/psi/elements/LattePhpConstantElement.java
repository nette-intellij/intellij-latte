package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpConstantStub;

public interface LattePhpConstantElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpConstantStub> {

	public abstract String getConstantName();

}