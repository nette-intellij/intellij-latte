package com.jantvrdik.intellij.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpPropertyStub;
import com.jantvrdik.intellij.latte.indexes.stubs.types.LattePhpPropertyStubType;
import com.jantvrdik.intellij.latte.psi.LattePhpProperty;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

public class LattePhpPropertyStubImpl extends StubBase<LattePhpProperty> implements LattePhpPropertyStub {
    private final String propertyName;

    public LattePhpPropertyStubImpl(final StubElement parent, final String propertyName) {
        super(parent, (LattePhpPropertyStubType) LatteTypes.PHP_PROPERTY);
        this.propertyName = propertyName;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }
}
