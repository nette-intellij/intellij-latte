package com.jantvrdik.intellij.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpNamespaceStub;
import com.jantvrdik.intellij.latte.indexes.stubs.types.LattePhpNamespaceStubType;
import com.jantvrdik.intellij.latte.psi.LattePhpNamespaceReference;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

public class LattePhpNamespaceStubImpl extends StubBase<LattePhpNamespaceReference> implements LattePhpNamespaceStub {
    private final String namespaceName;

    public LattePhpNamespaceStubImpl(final StubElement parent, final String constantName) {
        super(parent, (LattePhpNamespaceStubType) LatteTypes.PHP_NAMESPACE_REFERENCE);
        this.namespaceName = constantName;
    }

    @Override
    public String getNamespaceName() {
        return namespaceName;
    }
}
