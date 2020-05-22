package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.jantvrdik.intellij.latte.settings.LatteFilterSettings;
import org.jetbrains.annotations.Nullable;

public interface LatteMacroModifierElement extends PsiNameIdentifierOwner {

    public abstract String getModifierName();

    @Nullable
    public LatteFilterSettings getMacroModifier();

}