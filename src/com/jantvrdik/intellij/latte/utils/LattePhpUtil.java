package com.jantvrdik.intellij.latte.utils;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LattePhpUtil {

    public static Collection<PhpNamedElement> getAllClassNamesAndInterfaces(Project project, Collection<String> classNames) {
        Collection<PhpNamedElement> variants = new THashSet<PhpNamedElement>();
        PhpIndex phpIndex = getPhpIndex(project);

        for (String name : classNames) {
            variants.addAll(filterClasses(phpIndex.getClassesByName(name), null));
            variants.addAll(filterClasses(phpIndex.getInterfacesByName(name), null));
        }
        return variants;
    }

    public static Collection<Function> getAllFunctions(Project project, Collection<String> functionNames) {
        Collection<Function> variants = new THashSet<Function>();
        PhpIndex phpIndex = getPhpIndex(project);

        for (String name : functionNames) {
            variants.addAll(phpIndex.getFunctionsByName(name));
        }
        return variants;
    }

    public static String normalizePhpVariable(String name) {
        return name.startsWith("$") ? name.substring(1) : name;
    }

    public static boolean isReferenceTo(@NotNull PhpClass originalClass, @NotNull ResolveResult[] results, @NotNull PsiElement element, @NotNull String name) {
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

    public static boolean isNullable(@NotNull PhpType type) {
        try {
            return type.isNullable();

        } catch (NoSuchMethodError e) {
            return false;
        }
    }

    public static boolean isReferenceFor(@NotNull PhpClass originalClass, @NotNull PhpClass targetClass) {
        return isReferenceFor(originalClass.getFQN(), targetClass);
    }

    public static boolean isReferenceFor(@NotNull String originalClass, @NotNull PhpClass targetClass) {
        originalClass = normalizeClassName(originalClass);
        if (originalClass.equals(targetClass.getFQN())) {
            return true;
        }

        ExtendsList extendsList = targetClass.getExtendsList();
        for (ClassReference reference : extendsList.getReferenceElements()) {
            if (reference.getFQN() != null && reference.getFQN().equals(originalClass)) {
                return true;
            }
        }
        return false;
    }

    public static List<Field> getFieldsForPhpElement(@NotNull BaseLattePhpElement psiElement) {
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

    public static List<Method> getMethodsForPhpElement(@NotNull LattePhpMethod psiElement) {
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

    public static Collection<Function> getFunctionByName(Project project, String functionName) {
        return getPhpIndex(project).getFunctionsByName(functionName);
    }

    public static Collection<String> getAllExistingFunctionNames(Project project, PrefixMatcher prefixMatcher) {
        return getPhpIndex(project).getAllFunctionNames(prefixMatcher);
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

    public static String normalizeClassName(@Nullable String className) {
        String normalized = className == null ? "" : (className.startsWith("\\") ? className : ("\\" + className));
        if (normalized.contains("|null")) {
            normalized = normalized.replace("|null", "");
        }
        if (normalized.contains("|NULL")) {
            normalized = normalized.replace("|NULL", "");
        }
        return normalized;
    }
}