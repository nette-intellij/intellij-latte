package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpTypeStub;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.LattePhpTypePart;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LattePhpTypeElement extends PsiElement, StubBasedPsiElement<LattePhpTypeStub> {

    @NotNull
    List<LattePhpTypePart> getPhpTypePartList();

    @NotNull
    NettePhpType getReturnType();

}