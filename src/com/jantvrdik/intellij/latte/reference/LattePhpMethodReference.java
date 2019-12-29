package com.jantvrdik.intellij.latte.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.psi.LattePhpMethod;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
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
/*
    @Override
    public TextRange getRangeInElement() {
        return new TextRange(path.contains("-") ? path.lastIndexOf("-") + 2 : 1, path.length() + 1);
    }
/*
    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        String componentName = ComponentUtil.methodToComponentName(newElementName);
        if (getElement() instanceof StringLiteralExpression && componentName != null) {
            StringLiteralExpression stringLiteral = (StringLiteralExpression) getElement();
            TextRange range = getRangeInElement();
            String name = stringLiteral.getContents();
            name = (range.getStartOffset() > 1 ? name.substring(0, range.getStartOffset() - 1) : "")
                    + componentName
                    + name.substring(range.getEndOffset() - 1);
            stringLiteral.updateText(name);
            return getElement();
        }

        return super.handleElementRename(newElementName);
    }*/
}