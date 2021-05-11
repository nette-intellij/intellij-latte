package com.jantvrdik.intellij.latte.utils;

import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import org.jetbrains.annotations.NotNull;

public class LattePhpVariableDefinition {

    private final boolean probablyUndefined;
    private final LattePhpVariable element;

    public LattePhpVariableDefinition(boolean probablyUndefined, @NotNull LattePhpVariable element) {
        this.probablyUndefined = probablyUndefined;
        this.element = element;
    }

    public boolean isProbablyUndefined() {
        return probablyUndefined;
    }

    public LattePhpVariable getElement() {
        return element;
    }
}