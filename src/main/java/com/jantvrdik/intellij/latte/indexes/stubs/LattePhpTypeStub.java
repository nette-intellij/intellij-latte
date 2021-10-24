package com.jantvrdik.intellij.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.psi.LattePhpType;

public interface LattePhpTypeStub extends StubElement<LattePhpType> {
    String getPhpType();
}