package com.jantvrdik.intellij.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubBase;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpMethodStub;
import com.jantvrdik.intellij.latte.indexes.stubs.types.LattePhpMethodStubType;
import com.jantvrdik.intellij.latte.psi.LattePhpMethod;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

public class LattePhpMethodStubImpl extends StubBase<LattePhpMethod> implements LattePhpMethodStub {
    private final String methodName;

    public LattePhpMethodStubImpl(final StubElement parent, final String methodName) {
        super(parent, (LattePhpMethodStubType) LatteTypes.PHP_METHOD);
        this.methodName = methodName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }
}
