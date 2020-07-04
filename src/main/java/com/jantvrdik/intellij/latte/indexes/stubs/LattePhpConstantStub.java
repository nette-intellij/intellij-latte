package com.jantvrdik.intellij.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.psi.LattePhpConstant;

public interface LattePhpConstantStub extends StubElement<LattePhpConstant> {
    String getConstantName();
}