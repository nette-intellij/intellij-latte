package com.jantvrdik.intellij.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.psi.LattePhpProperty;

public interface LattePhpPropertyStub extends StubElement<LattePhpProperty> {
    String getPropertyName();
}