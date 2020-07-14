package com.jantvrdik.intellij.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LatteFilterStub;
import com.jantvrdik.intellij.latte.indexes.stubs.types.LatteFilterStubType;
import com.jantvrdik.intellij.latte.psi.LatteMacroModifier;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

public class LatteFilterStubImpl extends StubBase<LatteMacroModifier> implements LatteFilterStub {
    private final String filterName;

    public LatteFilterStubImpl(final StubElement parent, final String filterName) {
        super(parent, (LatteFilterStubType) LatteTypes.MACRO_MODIFIER);
        this.filterName = filterName;
    }

    @Override
    public String getModifierName() {
        return filterName;
    }
}
