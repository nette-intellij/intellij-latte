package com.jantvrdik.intellij.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.psi.LattePhpNamespaceReference;

public interface LattePhpNamespaceStub extends StubElement<LattePhpNamespaceReference> {
    String getNamespaceName();
}