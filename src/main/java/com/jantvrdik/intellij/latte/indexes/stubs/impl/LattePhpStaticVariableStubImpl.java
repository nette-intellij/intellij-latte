package com.jantvrdik.intellij.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpStaticVariableStub;
import com.jantvrdik.intellij.latte.indexes.stubs.types.LattePhpStaticVariableStubType;
import com.jantvrdik.intellij.latte.psi.LattePhpStaticVariable;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

public class LattePhpStaticVariableStubImpl extends StubBase<LattePhpStaticVariable> implements LattePhpStaticVariableStub {
    private final String variableName;

    public LattePhpStaticVariableStubImpl(final StubElement parent, final String variableName) {
        super(parent, (LattePhpStaticVariableStubType) LatteTypes.PHP_STATIC_VARIABLE);
        this.variableName = variableName;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }
}
