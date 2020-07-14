package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpStaticVariableStub;

public interface LattePhpStaticVariableElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpStaticVariableStub> {

	public abstract String getVariableName();

}