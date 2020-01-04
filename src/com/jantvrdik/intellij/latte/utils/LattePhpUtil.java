package com.jantvrdik.intellij.latte.utils;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LattePhpUtil {

    private static String[] nativeClassConstants = new String[]{"class"};

    public static String[] getNativeClassConstants() {
        return nativeClassConstants;
    }

    public static Collection<PhpNamedElement> getAllClassNamesAndInterfaces(Project project, Collection<String> classNames) {
        Collection<PhpNamedElement> variants = new THashSet<PhpNamedElement>();
        PhpIndex phpIndex = getPhpIndex(project);

        for (String name : classNames) {
            variants.addAll(filterClasses(phpIndex.getClassesByName(name), null));
            variants.addAll(filterClasses(phpIndex.getInterfacesByName(name), null));
        }
        return variants;
    }

    public static String normalizePhpVariable(String name) {
        return name.startsWith("$") ? name.substring(1) : name;
    }

    public static boolean isReferenceTo(@NotNull PhpClass originalClass, @NotNull ResolveResult[] results, @NotNull PsiElement element, @NotNull String name)
    {
        for (ResolveResult result : results) {
            if (!(result.getElement() instanceof BaseLattePhpElement)) {
                continue;
            }

            if (!name.equals(((BaseLattePhpElement) result.getElement()).getPhpElementName())) {
                continue;
            }

            PhpClass phpClass = ((BaseLattePhpElement) result.getElement()).getPhpType().getFirstPhpClass(element.getProject());
            if (phpClass == null) {
                continue;
            }

            if (isReferenceFor(originalClass, phpClass)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isReferenceFor(@NotNull PhpClass originalClass, @NotNull PhpClass targetClass)
    {
        if (originalClass.getFQN().equals(targetClass.getFQN())) {
            return true;
        }

        ExtendsList extendsList = targetClass.getExtendsList();
        for (ClassReference reference : extendsList.getReferenceElements()) {
            if (reference.getFQN().equals(originalClass.getFQN())) {
                return true;
            }
        }
        return false;
    }

    public static List<Field> getFieldsForPhpElement(@NotNull BaseLattePhpElement psiElement)
    {
        List<Field> out = new ArrayList<Field>();
        LattePhpType phpType = psiElement.getPhpType();
        String name = psiElement.getPhpElementName();
        boolean isConstant = psiElement instanceof LattePhpConstant;
        if (psiElement instanceof LattePhpVariable) {
            phpType = LatteUtil.findFirstLatteTemplateType(psiElement.getContainingFile());
            if (phpType == null) {
                return out;
            }
            name = ((LattePhpVariable) psiElement).getVariableName();
        }

        Collection<PhpClass> phpClasses = phpType.getPhpClasses(psiElement.getProject());
        if (phpClasses == null || phpClasses.size() == 0) {
            return out;
        }

        List<Field> fields = new ArrayList<>();
        for (PhpClass phpClass : phpClasses) {
            for (Field field : phpClass.getFields()) {
                if (
                        ((isConstant && field.isConstant()) || (!isConstant && !field.isConstant()))
                                && field.getName().equals(name)
                ) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }

    public static List<Method> getMethodsForPhpElement(@NotNull LattePhpMethod psiElement)
    {
        List<Method> out = new ArrayList<Method>();
        Collection<PhpClass> phpClasses = psiElement.getPhpType().getPhpClasses(psiElement.getProject());
        if (phpClasses != null && phpClasses.size() > 0) {
            String methodName = psiElement.getMethodName();
            for (PhpClass phpClass : phpClasses) {
                for (Method currentMethod : phpClass.getMethods()) {
                    if (currentMethod.getName().equals(methodName)) {
                        out.add(currentMethod);
                    }
                }
            }
        }
        return out;
    }

    public static Collection<PhpClass> getClassesByFQN(Project project, String className) {
        return getPhpIndex(project).getAnyByFQN(className);
    }


    public static Collection<String> getAllExistingClassNames(Project project, PrefixMatcher prefixMatcher) {
        return getPhpIndex(project).getAllClassNames(prefixMatcher);
    }

    private static PhpIndex getPhpIndex(Project project) {
        return PhpIndex.getInstance(project);
    }

    private static Collection<PhpClass> filterClasses(Collection<PhpClass> classes, String namespace) {
        if (namespace == null) {
            return classes;
        }
        namespace = "\\" + namespace + "\\";
        Collection<PhpClass> result = new ArrayList<PhpClass>();
        for (PhpClass cls : classes) {
            String classNs = cls.getNamespaceName();
            if (classNs.equals(namespace) || classNs.startsWith(namespace)) {
                result.add(cls);
            }
        }
        return result;
    }
}