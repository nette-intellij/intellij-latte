package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
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

        List<Field> fields = LattePhpUtil.getFieldsForPhpElement((BaseLattePhpElement) getElement());
        String name = ((BaseLattePhpElement) getElement()).getPhpElementName();
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                results.add(new PsiElementResolveResult(field));
            }
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        List<Field> fields = LattePhpUtil.getFieldsForPhpElement((BaseLattePhpElement) getElement());
        return fields.size() > 0 ? fields.get(0) : null;
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

    @Override
    public PsiElement handleElementRename(@NotNull String newName) {
        if (getElement() instanceof LattePhpStaticVariable) {
            ((LattePhpStaticVariable) getElement()).setName(newName);
        }
        return getElement();
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