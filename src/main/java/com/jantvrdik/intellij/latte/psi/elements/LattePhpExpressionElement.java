package com.jantvrdik.intellij.latte.psi.elements;

import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.LattePhpStatement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LattePhpExpressionElement extends LattePsiElement {

    @NotNull
    NettePhpType getReturnType();

    @NotNull
    List<LattePhpStatement> getPhpStatementList();

    int getPhpArrayLevel();

}