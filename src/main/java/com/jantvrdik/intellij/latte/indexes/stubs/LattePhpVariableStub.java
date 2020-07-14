package com.jantvrdik.intellij.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;

public interface LattePhpVariableStub extends StubElement<LattePhpVariable> {
    String getVariableName();
}