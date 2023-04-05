package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jantvrdik.intellij.latte.psi.LatteMacroContent;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LatteMacroTagElement extends LattePsiNamedElement {

    @Override
    default PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }

    @NotNull String getMacroName();

    int getMacroNameLength();

    @Nullable
    LatteMacroContent getMacroContent();

    boolean matchMacroName(@NotNull String name);

}