package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jantvrdik.intellij.latte.indexes.stubs.LatteFilterStub;
import com.jantvrdik.intellij.latte.settings.LatteFilterSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LatteMacroModifierElement extends LattePsiNamedElement, StubBasedPsiElement<LatteFilterStub> {

    @Override
    default PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }

    String getModifierName();

    @Nullable
    LatteFilterSettings getMacroModifier();

    @Nullable PsiElement getTextElement();

    boolean isVariableModifier();

}