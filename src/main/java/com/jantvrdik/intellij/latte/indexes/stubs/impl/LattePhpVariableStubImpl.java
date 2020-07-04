package com.jantvrdik.intellij.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpVariableStub;
import com.jantvrdik.intellij.latte.indexes.stubs.types.LattePhpVariableStubType;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

public class LattePhpVariableStubImpl extends StubBase<LattePhpVariable> implements LattePhpVariableStub {
    private final String variableName;

    public LattePhpVariableStubImpl(final StubElement parent, final String variableName) {
        super(parent, (LattePhpVariableStubType) LatteTypes.PHP_VARIABLE);
        this.variableName = variableName;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }
}
