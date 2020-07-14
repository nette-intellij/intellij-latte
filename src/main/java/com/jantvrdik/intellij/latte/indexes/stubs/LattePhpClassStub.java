package com.jantvrdik.intellij.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.psi.LattePhpClassReference;

public interface LattePhpClassStub extends StubElement<LattePhpClassReference> {
    String getClassName();
}