package com.jantvrdik.intellij.latte.psi.elements;

import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.LattePhpTypePart;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LattePhpTypeElement extends LattePsiElement {

    @NotNull
    List<LattePhpTypePart> getPhpTypePartList();

    @NotNull
    NettePhpType getReturnType();

}