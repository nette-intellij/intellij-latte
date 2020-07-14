package com.jantvrdik.intellij.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.psi.LatteMacroModifier;

public interface LatteFilterStub extends StubElement<LatteMacroModifier> {
    String getModifierName();
}