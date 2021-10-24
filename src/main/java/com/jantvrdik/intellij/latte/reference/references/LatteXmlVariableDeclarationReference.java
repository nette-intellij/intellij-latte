package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import com.jantvrdik.intellij.latte.indexes.LatteIndexUtil;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LatteXmlVariableDeclarationReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final String name;
    private final Project project;

    public LatteXmlVariableDeclarationReference(@NotNull XmlAttributeValue element, TextRange textRange) {
        super(element, textRange);
        name = element.getValue();
        project = element.getProject();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();

        final Collection<LattePhpVariable> variables = LatteIndexUtil.findVariablesByName(project, name);
        for (LattePhpVariable variable : variables) {
            if (!variable.isDefinition()) {
                continue;
            }
            if (variable.getVariableName().equals(name)) {
                results.add(new PsiElementResolveResult(variable));
            }
        }
        return results.toArray(new ResolveResult[0]);
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

    @Override
    public boolean isSoft() {
        return true;
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        final Collection<LattePhpVariable> variables = LatteIndexUtil.findVariablesByName(project, name);
        /*if (!(getElement() instanceof LattePhpVariable)) {
            return new ResolveResult[0];
        }

        final List<PsiPositionedElement> variables;
        if (((LattePhpVariable) getElement()).isVarTypeDefinition()) {
            variables = LatteUtil.findVariablesInFileAfterElement(getElement(), getElement().getContainingFile().getVirtualFile(), variableName);
        } else {
            variables = LatteUtil.findVariablesInFileBeforeElement(getElement(), getElement().getContainingFile().getVirtualFile(), variableName);
        }*/

        for (LattePhpVariable variable : variables) {
            if (variable.isDefinition()) {
                continue;
            }
            if (variable.getVariableName().equals(name)) {
                return true;
            }
        }
        return super.isReferenceTo(element);
    }

}