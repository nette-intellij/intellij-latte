package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.psi.LattePhpConstant;
import com.jantvrdik.intellij.latte.psi.LattePhpStaticVariable;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LattePhpStaticVariableReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private String variableName;
    private PhpClass phpClass;

    public LattePhpStaticVariableReference(@NotNull LattePhpStaticVariable element, TextRange textRange) {
        super(element, textRange);
        variableName = element.getVariableName();
        phpClass = element.getPhpType().getFirstPhpClass(element.getProject());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        if (phpClass == null) {
            return new ResolveResult[0];
        }

        final Collection<BaseLattePhpElement> methods = LatteUtil.findStaticVariables(getElement().getProject(), variableName, phpClass);
        if (methods.size() == 0) {
            return new ResolveResult[0];
        }

        List<ResolveResult> results = new ArrayList<ResolveResult>();
        for (BaseLattePhpElement method : methods) {
            results.add(new PsiElementResolveResult(method));
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length > 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return LattePhpUtil.normalizePhpVariable(getElement().getText());
    }
/*
    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (element instanceof LattePhpStaticVariable) {
            PhpClass originalClass = ((LattePhpStaticVariable) element).getPhpType().getFirstPhpClass(element.getProject());
            if (originalClass != null) {
                if (LattePhpUtil.isReferenceTo(originalClass, multiResolve(false), element, ((LattePhpStaticVariable) element).getVariableName())) {
                    return true;
                }
            }
        }

        if (!(element instanceof Field)) {
            return false;
        }
        PhpClass originalClass = ((Field) element).getContainingClass();
        if (originalClass == null) {
            return false;
        }
        return LattePhpUtil.isReferenceTo(originalClass, multiResolve(false), element, ((Field) element).getName());
    }
*/
}