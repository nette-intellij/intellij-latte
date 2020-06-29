package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.LattePhpStatement;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LattePhpExpressionElement extends PsiElement {

    @NotNull
    LattePhpType getPhpType();

    @NotNull
    List<LattePhpStatement> getPhpStatementList();

}