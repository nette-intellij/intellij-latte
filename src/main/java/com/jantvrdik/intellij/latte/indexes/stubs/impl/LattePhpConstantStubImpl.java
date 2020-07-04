package com.jantvrdik.intellij.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpConstantStub;
import com.jantvrdik.intellij.latte.indexes.stubs.types.LattePhpConstantStubType;
import com.jantvrdik.intellij.latte.psi.LattePhpConstant;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

public class LattePhpConstantStubImpl extends StubBase<LattePhpConstant> implements LattePhpConstantStub {
    private final String constantName;

    public LattePhpConstantStubImpl(final StubElement parent, final String constantName) {
        super(parent, (LattePhpConstantStubType) LatteTypes.PHP_CONSTANT);
        this.constantName = constantName;
    }

    @Override
    public String getConstantName() {
        return constantName;
    }
}
