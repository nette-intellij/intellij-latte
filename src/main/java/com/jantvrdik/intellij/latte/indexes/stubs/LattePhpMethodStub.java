package com.jantvrdik.intellij.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.psi.LattePhpMethod;

public interface LattePhpMethodStub extends StubElement<LattePhpMethod> {
    String getMethodName();
}