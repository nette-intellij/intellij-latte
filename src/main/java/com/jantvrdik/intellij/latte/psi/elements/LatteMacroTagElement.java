package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

public interface LatteMacroTagElement extends PsiNameIdentifierOwner {

    @NotNull String getMacroName();

    int getMacroNameLength();

    boolean matchMacroName(@NotNull String name);

}