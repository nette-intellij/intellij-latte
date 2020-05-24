package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.LattePhpTypeElement;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LattePhpTypedPartElement extends PsiElement {

    @Nullable
    LattePhpTypeElement getPhpTypeElement();

    @NotNull
    LattePhpVariable getPhpVariable();

    @NotNull
    LattePhpType getPhpType();

}