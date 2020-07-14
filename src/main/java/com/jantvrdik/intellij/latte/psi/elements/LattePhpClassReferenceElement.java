package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpClassStub;

public interface LattePhpClassReferenceElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpClassStub> {

	public abstract String getClassName();

}