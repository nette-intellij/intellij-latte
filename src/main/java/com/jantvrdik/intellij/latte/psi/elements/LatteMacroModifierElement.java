package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.jantvrdik.intellij.latte.settings.LatteCustomModifierSettings;
import org.jetbrains.annotations.Nullable;

public interface LatteMacroModifierElement extends PsiNameIdentifierOwner {

    public abstract String getModifierName();

    @Nullable
    public LatteCustomModifierSettings getMacroModifier();

}