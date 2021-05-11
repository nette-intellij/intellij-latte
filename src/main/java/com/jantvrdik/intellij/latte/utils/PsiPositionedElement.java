package com.jantvrdik.intellij.latte.utils;

import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import org.jetbrains.annotations.NotNull;

public class PsiPositionedElement {

    private final int position;
    private final LattePhpVariable element;

    public PsiPositionedElement(int position, @NotNull LattePhpVariable element) {
        this.position = position;
        this.element = element;
    }

    public int getPosition() {
        return position;
    }

    public LattePhpVariable getElement() {
        return element;
    }
}