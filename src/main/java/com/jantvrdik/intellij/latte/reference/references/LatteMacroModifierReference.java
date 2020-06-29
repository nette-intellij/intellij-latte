package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.psi.LatteMacroModifier;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LatteMacroModifierReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final String modifierName;

    public LatteMacroModifierReference(@NotNull LatteMacroModifier element, TextRange textRange) {
        super(element, textRange);
        modifierName = element.getModifierName();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();
        final Collection<LatteMacroModifier> modifiers = LatteUtil.findModifiers(getElement().getProject(), modifierName);
        for (LatteMacroModifier modifier : modifiers) {
            results.add(new PsiElementResolveResult(modifier));
        }
        return results.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }
}