package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jantvrdik.intellij.latte.utils.PsiPositionedElement;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
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

        List<ResolveResult> results = new ArrayList<ResolveResult>();
        LattePhpType fields = LatteUtil.findFirstLatteTemplateType(getElement().getContainingFile());
        String name = ((BaseLattePhpElement) getElement()).getPhpElementName();
        if (fields != null) {
            for (PhpClass phpClass : fields.getPhpClasses(getElement().getProject())) {
                for (Field field : phpClass.getFields()) {
                    if (!field.isConstant() && field.getName().equals(name)) {
                        results.add(new PsiElementResolveResult(field));
                    }
                }
            }
        }

        //todo: complete resolving for variables
        //final List<PsiPositionedElement> variables = LatteUtil.findVariablesInFileBeforeElement(getElement(), getElement().getContainingFile().getVirtualFile(), variableName);
        final List<PsiPositionedElement> variables = LatteUtil.findVariablesInFile(getElement().getProject(), getElement().getContainingFile().getVirtualFile(), variableName);
        /*if (!(getElement() instanceof LattePhpVariable)) {
            return new ResolveResult[0];
        }

        final List<PsiPositionedElement> variables;
        if (((LattePhpVariable) getElement()).isVarTypeDefinition()) {
            variables = LatteUtil.findVariablesInFileAfterElement(getElement(), getElement().getContainingFile().getVirtualFile(), variableName);
        } else {
            variables = LatteUtil.findVariablesInFileBeforeElement(getElement(), getElement().getContainingFile().getVirtualFile(), variableName);
        }*/

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

}