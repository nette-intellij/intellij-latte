package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LatteXmlFilterDeclarationReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final String filterName;

    public LatteXmlFilterDeclarationReference(@NotNull XmlAttributeValue element, TextRange textRange) {
        super(element, textRange);
        filterName = element.getValue();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();
        //for (XmlAttributeValue attributeValue : LatteFileConfiguration.getAllMatchedXmlAttributeValues(getElement().getProject(), "filter", modifierName)) {
        //    if (attributeValue == getElement()) {
        //        continue;
        //    }
        //    results.add(new PsiElementResolveResult(attributeValue));
        //}

        //results.add(new PsiElementResolveResult(getElement()));
        //final Collection<LatteMacroModifier> modifiers = LatteUtil.findModifiers(getElement().getProject(), modifierName);
        //for (LatteMacroModifier modifier : modifiers) {
        //    results.add(new PsiElementResolveResult(modifier));
        //}
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

    /*
    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (super.isReferenceTo(element)) {
            return true;
        }

        PsiElement psiElement = LatteXmlFindUsagesProvider.getPsiElementFromDomTarget(element);
        if (!(psiElement instanceof XmlAttributeValue) || !(psiElement.getParent() instanceof XmlAttribute)) {
            return false;
        }
        if (!(psiElement.getContainingFile() instanceof XmlFile) || !LatteFileConfiguration.isXmlConfiguration((XmlFile) element.getContainingFile())) {
            return false;
        }
        XmlAttribute attribute = (XmlAttribute) psiElement.getParent();
        return attribute.getName().equals("name")
                && attribute.getParent().getName().equals("tag")
                && ((XmlAttributeValue) psiElement).getValue().equals(modifierName);
    }*/
}