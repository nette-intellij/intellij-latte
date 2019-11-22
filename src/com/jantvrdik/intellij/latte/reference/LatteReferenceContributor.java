package com.jantvrdik.intellij.latte.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.NotNull;

public class LatteReferenceContributor extends PsiReferenceContributor {
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(LatteTypes.PHP_VARIABLE),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (!(element instanceof LattePhpVariable)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        LattePhpVariable variableElement = (LattePhpVariable) element;
                        String value = variableElement.getVariableName();
                        if (value != null && value.startsWith("$")) {
                            return new PsiReference[]{
                                    new LattePhpVariableReference((LattePhpVariable) element, new TextRange(0, value.length()))};
                        }
                        // ClassConstantReference
                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}
