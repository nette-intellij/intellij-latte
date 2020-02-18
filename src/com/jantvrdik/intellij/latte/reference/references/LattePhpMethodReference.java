package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.psi.LattePhpMethod;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LattePhpMethodReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private String methodName;
    private Collection<PhpClass> phpClasses;

    public LattePhpMethodReference(@NotNull LattePhpMethod element, TextRange textRange) {
        super(element, textRange);
        methodName = element.getMethodName();
        phpClasses = element.getPhpType().getPhpClasses(element.getProject());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        if (((LattePhpMethod) getElement()).isFunction()) {
            return multiResolveFunction();
        } else if (phpClasses.size() == 0) {
            return new ResolveResult[0];
        }
        return multiResolveMethod();
    }

    @NotNull
    public ResolveResult[] multiResolveMethod() {
        List<ResolveResult> results = new ArrayList<ResolveResult>();
        final Collection<LattePhpMethod> methods = LatteUtil.findMethods(getElement().getProject(), methodName, phpClasses);
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

    @NotNull
    public ResolveResult[] multiResolveFunction() {
        List<ResolveResult> results = new ArrayList<ResolveResult>();

        final Collection<BaseLattePhpElement> functions = LatteUtil.findFunctions(getElement().getProject(), methodName);
        for (BaseLattePhpElement function : functions) {
            results.add(new PsiElementResolveResult(function));
        }

        Collection<Function> phpFunctions = LattePhpUtil.getFunctionByName(getElement().getProject(), methodName);
        for (Function currentFunction : phpFunctions) {
            if (currentFunction.getName().equals(methodName)) {
                results.add(new PsiElementResolveResult(currentFunction));
            }
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (((LattePhpMethod) getElement()).isFunction()) {
            List<Function> phpFunctions = new ArrayList<>(LattePhpUtil.getFunctionByName(
                    getElement().getProject(),
                    ((LattePhpMethod) getElement()).getMethodName()
            ));
            return phpFunctions.size() > 0 ? phpFunctions.get(0) : null;

        } else {
            List<Method> phpMethods = LattePhpUtil.getMethodsForPhpElement((LattePhpMethod) getElement());
            return phpMethods.size() > 0 ? phpMethods.get(0) : null;
        }
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (element instanceof LattePhpMethod) {
            Collection<PhpClass> originalClasses = ((LattePhpMethod) element).getPhpType().getPhpClasses(element.getProject());
            if (originalClasses.size() > 0) {
                for (PhpClass originalClass : originalClasses) {
                    if (LattePhpUtil.isReferenceTo(originalClass, multiResolve(false), element, ((LattePhpMethod) element).getMethodName())) {
                        return true;
                    }
                }
            }
        }

        if (element instanceof Method) {
            PhpClass originalClass = ((Method) element).getContainingClass();
            if (originalClass == null) {
                return false;
            }
            return LattePhpUtil.isReferenceTo(originalClass, multiResolve(false), element, ((Method) element).getName());
        } else if (element instanceof Function) {
            String name = ((Function) element).getName();
            for (ResolveResult result : multiResolve(false)) {
                if (!(result.getElement() instanceof BaseLattePhpElement)) {
                    continue;
                }

                if (name.equals(((BaseLattePhpElement) result.getElement()).getPhpElementName())) {
                    return true;
                }
            }
        }
        return false;
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