package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpNamespaceStub;

public interface LattePhpNamespaceReferenceElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpNamespaceStub> {

	public abstract String getNamespaceName();

}