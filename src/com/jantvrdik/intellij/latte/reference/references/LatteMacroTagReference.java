package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.psi.LatteMacroClassic;
import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LatteMacroTagReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    public LatteMacroTagReference(@NotNull LatteMacroTag element, TextRange textRange) {
        super(element, textRange);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        PsiElement parent = getElement().getParent();
        if (!(parent instanceof LatteMacroClassic)) {
            return new ResolveResult[0];
        }

        List<ResolveResult> results = new ArrayList<ResolveResult>();
        results.add(new PsiElementResolveResult(((LatteMacroClassic) parent).getOpenTag()));

        LatteMacroTag closeTag = ((LatteMacroClassic) parent).getCloseTag();
        if (closeTag != null) {
            results.add(new PsiElementResolveResult(closeTag));
        }
        return results.toArray(new ResolveResult[results.size()]);
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