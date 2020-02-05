package com.jantvrdik.intellij.latte.utils;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class PsiPositionedElement {

    private final int position;
    private final PsiElement element;

    public PsiPositionedElement(int position, @NotNull PsiElement element) {
        this.position = position;
        this.element = element;
    }

    public int getPosition() {
        return position;
    }

    public PsiElement getElement() {
        return element;
    }
}