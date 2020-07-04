package com.jantvrdik.intellij.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.psi.LattePhpStaticVariable;

public interface LattePhpStaticVariableStub extends StubElement<LattePhpStaticVariable> {
    String getVariableName();
}