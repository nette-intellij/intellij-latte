package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jantvrdik.intellij.latte.utils.PsiPositionedElement;
import org.jetbrains.annotations.*;

import java.util.*;

public class LattePhpVariableReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private String variableName;

    public LattePhpVariableReference(@NotNull LattePhpVariable element, TextRange textRange) {
        super(element, textRange);
        variableName = element.getVariableName();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        if (getElement().getContainingFile().getVirtualFile() == null) {
            return new ResolveResult[0];
        }

        final List<PsiPositionedElement> variables = LatteUtil.findVariablesInFileBeforeElement(getElement(), getElement().getContainingFile().getVirtualFile(), variableName);
        /*if (!(getElement() instanceof LattePhpVariable)) {
            return new ResolveResult[0];
        }

        final List<PsiPositionedElement> variables;
        if (((LattePhpVariable) getElement()).isVarTypeDefinition()) {
            variables = LatteUtil.findVariablesInFileAfterElement(getElement(), getElement().getContainingFile().getVirtualFile(), variableName);
        } else {
            variables = LatteUtil.findVariablesInFileBeforeElement(getElement(), getElement().getContainingFile().getVirtualFile(), variableName);
        }*/

        List<ResolveResult> results = new ArrayList<ResolveResult>();
        for (PsiPositionedElement variable : variables) {
            results.add(new PsiElementResolveResult(variable.getElement()));
        }
        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length > 0 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        List<LattePhpVariable> properties = LatteUtil.findVariables(project);
        List<LookupElement> variants = new ArrayList<LookupElement>();
        for (final LattePhpVariable property : properties) {
            if (property.getName() != null && property.getName().length() > 0) {
                variants.add(LookupElementBuilder.create(property)
                        //.withIcon(SimpleIcons.FILE)
                        .withTypeText(property.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }
}