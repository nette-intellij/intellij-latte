package com.jantvrdik.intellij.latte.reference;

import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jantvrdik.intellij.latte.utils.PsiPositionedElement;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

public class LattePhpVariableReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private String key;

    public LattePhpVariableReference(@NotNull LattePhpVariable element, TextRange textRange) {
        super(element, textRange);
        key = element.getVariableName();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        final List<PsiPositionedElement> properties = LatteUtil.findVariablesInFileBeforeElement(getElement(), getElement().getContainingFile().getVirtualFile(), key);
        List<PsiPositionedElement> definitions = properties.stream().filter(
                psiPositionedElement -> psiPositionedElement.getElement() instanceof LattePhpVariable
                && ((LattePhpVariable) psiPositionedElement.getElement()).isDefinition()
        ).collect(Collectors.toList());

        List<ResolveResult> results = new ArrayList<ResolveResult>();
        for (PsiPositionedElement property : definitions) {
            results.add(new PsiElementResolveResult(property.getElement()));
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
                variants.add(LookupElementBuilder.create(property).
                        //withIcon(SimpleIcons.FILE).
                        withTypeText(property.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }
}