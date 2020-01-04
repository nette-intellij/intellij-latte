package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.psi.LattePhpMethod;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LattePhpMethodReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private String key;
    private PhpClass phpClass;

    public LattePhpMethodReference(@NotNull LattePhpMethod element, TextRange textRange) {
        super(element, textRange);
        key = element.getMethodName();
        phpClass = element.getPhpType().getFirstPhpClass(element.getProject());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        if (phpClass == null) {
            return new ResolveResult[0];
        }

        final Collection<BaseLattePhpElement> methods = LatteUtil.findMethods(getElement().getProject(), key, phpClass);
        if (methods.size() == 0) {
            return new ResolveResult[0];
        }

        List<ResolveResult> results = new ArrayList<ResolveResult>();
        for (BaseLattePhpElement method : methods) {
            results.add(new PsiElementResolveResult(method));
        }

        List<Method> phpMethods = LattePhpUtil.getMethodsForPhpElement((LattePhpMethod) getElement());
        String methodName = ((LattePhpMethod) getElement()).getMethodName();
        for (Method currentMethod : phpMethods) {
            if (currentMethod.getName().equals(methodName)) {
                results.add(new PsiElementResolveResult(currentMethod));
            }
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        List<Method> phpMethods = LattePhpUtil.getMethodsForPhpElement((LattePhpMethod) getElement());
        return phpMethods.size() > 0 ? phpMethods.get(0) : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (element instanceof LattePhpMethod) {
            PhpClass originalClass = ((LattePhpMethod) element).getPhpType().getFirstPhpClass(element.getProject());
            if (originalClass != null) {
                if (LattePhpUtil.isReferenceTo(originalClass, multiResolve(false), element, ((LattePhpMethod) element).getMethodName())) {
                    return true;
                }
            }
        }

        if (!(element instanceof Method)) {
            return false;
        }
        PhpClass originalClass = ((Method) element).getContainingClass();
        if (originalClass == null) {
            return false;
        }
        return LattePhpUtil.isReferenceTo(originalClass, multiResolve(false), element, ((Method) element).getName());
    }

/*
    @Override
    public TextRange getRangeInElement() {
        return new TextRange(path.contains("-") ? path.lastIndexOf("-") + 2 : 1, path.length() + 1);
    }*/

    @Override
    public PsiElement handleElementRename(@NotNull String newName) {
        if (getElement() instanceof LattePhpMethod) {
            ((LattePhpMethod) getElement()).setName(newName);
        }
        return getElement();
    }
}