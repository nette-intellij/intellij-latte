package com.jantvrdik.intellij.latte.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.reference.references.*;
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
                        if (value != null) {
                            return new PsiReference[]{
                                    new LattePhpVariableReference((LattePhpVariable) element, new TextRange(0, value.length() + 1))};
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });

        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(LatteTypes.PHP_METHOD),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (!(element instanceof LattePhpMethod)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        LattePhpMethod methodElement = (LattePhpMethod) element;
                        String value = methodElement.getMethodName();
                        if (value != null) {
                            return new PsiReference[]{new LattePhpMethodReference(methodElement, new TextRange(0, value.length()))};
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });

        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(LatteTypes.PHP_PROPERTY),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (!(element instanceof LattePhpProperty)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        LattePhpProperty propertyElement = (LattePhpProperty) element;
                        String value = propertyElement.getPropertyName();
                        if (value != null) {
                            return new PsiReference[]{new LattePhpPropertyReference(propertyElement, new TextRange(0, value.length()))};
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });

        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(LatteTypes.PHP_CONSTANT),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (!(element instanceof LattePhpConstant)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        LattePhpConstant constantElement = (LattePhpConstant) element;
                        String value = constantElement.getConstantName();
                        if (value != null) {
                            return new PsiReference[]{new LattePhpConstantReference(constantElement, new TextRange(0, value.length()))};
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });

        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(LatteTypes.PHP_STATIC_VARIABLE),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (!(element instanceof LattePhpStaticVariable)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        LattePhpStaticVariable constantElement = (LattePhpStaticVariable) element;
                        String value = constantElement.getVariableName();
                        if (value != null) {
                            return new PsiReference[]{new LattePhpStaticVariableReference(constantElement, new TextRange(0, value.length() + 1))};
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });

        registrar.registerReferenceProvider(
                PlatformPatterns.psiElement(LatteTypes.PHP_CLASS),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (!(element instanceof LattePhpClass)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        LattePhpClass constantElement = (LattePhpClass) element;
                        String value = constantElement.getClassName();
                        if (value != null) {
                            return new PsiReference[]{new LattePhpClassReference(constantElement, new TextRange(0, value.length()))};
                        }

                        return PsiReference.EMPTY_ARRAY;
                    }
                });

        registrar.registerReferenceProvider(
                PlatformPatterns.or(
                        PlatformPatterns.psiElement(LatteTypes.MACRO_OPEN_TAG),
                        PlatformPatterns.psiElement(LatteTypes.MACRO_CLOSE_TAG)
                ),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (!(element instanceof LatteMacroTag)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        LatteMacroTag constantElement = (LatteMacroTag) element;
                        String value = constantElement.getMacroName();
                        if (value.length() == 0) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        int length = element instanceof LatteMacroCloseTag ? 2 : 1;
                        return new PsiReference[]{new LatteMacroTagReference(constantElement, new TextRange(1, value.length() + length))};
                    }
                });

        registrar.registerReferenceProvider(
                PlatformPatterns.or(
                        PlatformPatterns.psiElement(LatteTypes.MACRO_MODIFIER)
                ),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (!(element instanceof LatteMacroModifier)) {
                            return PsiReference.EMPTY_ARRAY;
                        }

                        LatteMacroModifier constantElement = (LatteMacroModifier) element;
                        String value = constantElement.getModifierName();
                        if (value.length() == 0) {
                            return PsiReference.EMPTY_ARRAY;
                        }
                        return new PsiReference[]{new LatteMacroModifierReference(constantElement, new TextRange(0, value.length()))};
                    }
                });
    }
}
