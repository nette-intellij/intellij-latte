package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpPropertyStub;

public interface LattePhpPropertyElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpPropertyStub> {

	public abstract String getPropertyName();

}