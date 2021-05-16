package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.php.LattePhpUtil;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LattePhpVariableDeclarationReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final String variableName;

    public LattePhpVariableDeclarationReference(@NotNull LattePhpVariable element, TextRange textRange) {
        super(element, textRange);
        variableName = element.getVariableName();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        if (!(getElement() instanceof LattePhpVariable || getElement().getContainingFile().getVirtualFile() == null)) {
            return new ResolveResult[0];
        }
        LattePhpVariable variable = (LattePhpVariable) getElement();
        List<ResolveResult> results = new ArrayList<>();

        /*final List<LattePhpVariableDefinition> variables = LatteUtil.getVariableOtherDefinitions(variable);
        for (LattePhpVariableDefinition variableDefinition : variables) {
            results.add(new PsiElementResolveResult(variableDefinition.getElement()));
        }*/
        return results.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @Override
    public boolean isSoft() {
        return true;
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
        if (getElement() instanceof LattePhpVariable) {
            ((LattePhpVariable) getElement()).setName(newName);
        }
        return getElement();
    }
/*
    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (element instanceof LattePhpVariable) {
            return !(((LattePhpVariable) element).isDefinition()) && ((LattePhpVariable) element).getVariableName().equals(variableName);
        }
        return super.isReferenceTo(element);
    }*/
}