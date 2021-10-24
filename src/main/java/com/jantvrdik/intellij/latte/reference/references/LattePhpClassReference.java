package com.jantvrdik.intellij.latte.reference.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.indexes.LatteIndexUtil;
import com.jantvrdik.intellij.latte.psi.LattePhpClassUsage;
import com.jantvrdik.intellij.latte.php.LattePhpUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LattePhpClassReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    final private String className;
    final private Collection<PhpClass> phpClasses;
    final private Project project;

    public LattePhpClassReference(@NotNull LattePhpClassUsage element, TextRange textRange) {
        super(element, textRange);
        className = element.getClassName();
        project = element.getProject();
        phpClasses = element.getReturnType().getPhpClasses(project);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        List<ResolveResult> results = new ArrayList<>();
        for (PhpClass phpClass : phpClasses) {
            if (LattePhpUtil.isReferenceFor(className, phpClass)) {
                results.add(new PsiElementResolveResult(phpClass));
            }
        }

        //for (com.jantvrdik.intellij.latte.psi.LattePhpClassReference classReference : LatteIndexUtil.getClassesByFqn(project, className)) {
        //    results.add(new PsiElementResolveResult(classReference.getPhpClassUsage()));
        //}
        return results.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        for (ResolveResult resolveResult : resolveResults) {
            if (!(resolveResult.getElement() instanceof LattePhpClassUsage)) {
                return resolveResult.getElement();
            }
        }
        return null;
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

    @Override
    public PsiElement handleElementRename(@NotNull String newName) {
        if (getElement() instanceof LattePhpClassUsage) {
            ((LattePhpClassUsage) getElement()).setName(newName);
        }
        return getElement();
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (element instanceof LattePhpClassUsage) {
            return className.equals(((LattePhpClassUsage) element).getClassName());
        }
       /* if (element instanceof LattePhpClassUsage) {
            Collection<PhpClass> originalClasses = ((LattePhpClassUsage) element).getPrevReturnType().getPhpClasses(project);
            if (originalClasses.size() > 0) {
                for (ResolveResult result : multiResolve(false)) {
                    if (!(result.getElement() instanceof LattePhpClassUsage)) {
                        continue;
                    }

                    Collection<PhpClass> targetClasses = ((LattePhpClassUsage) result.getElement()).getPrevReturnType().getPhpClasses(project);
                    if (targetClasses.size() == 0) {
                        continue;
                    }

                    for (PhpClass targetClass : targetClasses) {
                        String targetFqn = targetClass.getFQN();
                        for (PhpClass originalClass : originalClasses) {
                            if (originalClass.getFQN().equals(targetFqn)) {
                                return true;
                            }
                        }
                    }
                }

                for (PhpClass originalClass : originalClasses) {
                    if (LattePhpUtil.isReferenceTo(originalClass, multiResolve(false), element, ((LattePhpClassUsage) element).getClassName())) {
                        return true;
                    }
                }
            }
        }*/

        if (element instanceof PhpClass) {
            return LattePhpUtil.isReferenceTo((PhpClass) element, multiResolve(false), project, ((PhpClass) element).getFQN());
        }

        if (!(element instanceof PhpNamespace)) {
            return false;
        }
        String namespace = ((PhpNamespace) element).getParentNamespaceName() + ((PhpNamespace) element).getName();
        return namespace.equals(className);
    }

}