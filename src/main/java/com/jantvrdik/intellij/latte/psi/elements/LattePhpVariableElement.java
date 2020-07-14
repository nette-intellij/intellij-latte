package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpVariableStub;

public interface LattePhpVariableElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpVariableStub> {

	public abstract String getVariableName();

}