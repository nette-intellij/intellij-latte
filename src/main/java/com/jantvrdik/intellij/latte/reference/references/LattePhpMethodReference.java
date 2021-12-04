package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.jantvrdik.intellij.latte.config.LatteFileConfiguration;
import com.jantvrdik.intellij.latte.indexes.LatteIndexUtil;
import com.jantvrdik.intellij.latte.psi.LattePhpMethod;
import com.jantvrdik.intellij.latte.php.LattePhpUtil;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LattePhpMethodReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    final private String methodName;
    final private Collection<PhpClass> phpClasses;
    final private boolean isFunction;
    final private Project project;

    public LattePhpMethodReference(@NotNull LattePhpMethod element, TextRange textRange) {
        super(element, textRange);
        methodName = element.getMethodName();
        project = element.getProject();
        phpClasses = element.getPrevReturnType().getPhpClasses(project);
        isFunction = element.isFunction();
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
        List<ResolveResult> results = new ArrayList<>();
        final Collection<LattePhpMethod> methods = LatteIndexUtil.findMethodsByName(project, methodName);
        for (LattePhpMethod method : methods) {
            if (method.getPrevReturnType().hasClass(phpClasses)) {
                results.add(new PsiElementResolveResult(method));
            }
        }

        List<Method> phpMethods = LattePhpUtil.getMethodsForPhpElement((LattePhpMethod) getElement(), project);
        String methodName = ((LattePhpMethod) getElement()).getMethodName();
        for (Method currentMethod : phpMethods) {
            if (currentMethod.getName().equals(methodName)) {
                results.add(new PsiElementResolveResult(currentMethod));
            }
        }

        return results.toArray(new ResolveResult[0]);
    }

    @NotNull
    public ResolveResult[] multiResolveFunction() {
        List<ResolveResult> results = new ArrayList<>();

        Collection<Function> phpFunctions = LattePhpUtil.getFunctionByName(project, methodName);
        for (Function currentFunction : phpFunctions) {
            if (currentFunction.getName().equals(methodName)) {
                results.add(new PsiElementResolveResult(currentFunction));
            }
        }

        for (XmlAttributeValue attributeValue : LatteFileConfiguration.getAllMatchedXmlAttributeValues(project, "function", methodName)) {
            results.add(new PsiElementResolveResult(attributeValue));
        }
/*
        final Collection<LattePhpMethod> methods = LatteIndexUtil.findMethodsByName(project, methodName);
        for (LattePhpMethod method : methods) {
            if (method.isFunction()) {
                results.add(new PsiElementResolveResult(method));
            }
        }
*/
        return results.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (((LattePhpMethod) getElement()).isFunction()) {
            if (!isFunction) {
                return null;
            }
            ResolveResult[] result =  multiResolve(false);
            return result.length == 1 ? result[0].getElement() : null;

        } else {
            List<Method> phpMethods = LattePhpUtil.getMethodsForPhpElement((LattePhpMethod) getElement(), project);
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
            if (!isFunction && !((LattePhpMethod) element).isFunction()) {
                Collection<PhpClass> originalClasses = ((LattePhpMethod) element).getPrevReturnType().getPhpClasses(project);
                if (originalClasses.size() > 0) {
                    for (PhpClass originalClass : originalClasses) {
                        if (LattePhpUtil.isReferenceTo(originalClass, multiResolve(false), project, ((LattePhpMethod) element).getMethodName())) {
                            return true;
                        }
                    }
                }
            } else if (isFunction && ((LattePhpMethod) element).isFunction()) {
                return ((LattePhpMethod) element).getMethodName().equals(methodName);
            }
        }

        if (isFunction) {
            PsiElement currentElement = element;
            if (element instanceof PomTargetPsiElement) {
                currentElement = LatteFileConfiguration.getPsiElementFromDomTarget("function", element);
                if (currentElement == null) {
                    currentElement = element;
                }
            }
            if (currentElement instanceof XmlAttributeValue && LatteFileConfiguration.hasParentXmlTagName(currentElement, "function")) {
                return ((XmlAttributeValue) currentElement).getValue().equals(methodName);
            }
        }

        if (element instanceof Method) {
            PhpClass originalClass = ((Method) element).getContainingClass();
            if (originalClass == null) {
                return false;
            }
            return LattePhpUtil.isReferenceTo(originalClass, multiResolve(false), project, ((Method) element).getName());
        } else if (element instanceof Function) {
            String name = ((Function) element).getName();
            final Collection<LattePhpMethod> methods = LatteIndexUtil.findMethodsByName(element.getProject(), methodName);
            for (LattePhpMethod method : methods) {
                if (!method.isFunction()) {
                    continue;
                }

                if (name.equals(method.getMethodName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newName) {
        if (getElement() instanceof LattePhpMethod) {
            ((LattePhpMethod) getElement()).setName(newName);
        }
        return getElement();
    }
}