package com.jantvrdik.intellij.latte.psi.elements;

import com.jantvrdik.intellij.latte.php.LattePhpTypeDetector;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.LattePhpType;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LattePhpTypedPartElement extends LattePsiElement {

    @Nullable
    LattePhpType getPhpType();

    @NotNull
    LattePhpVariable getPhpVariable();

    default @NotNull NettePhpType getReturnType() {
        return LattePhpTypeDetector.detectPhpType(this);
    }

}