package com.jantvrdik.intellij.latte.php;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
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
        Collection<PhpNamedElement> variants = new THashSet<>();
        PhpIndex phpIndex = getPhpIndex(project);

        for (String name : classNames) {
            variants.addAll(filterClasses(phpIndex.getClassesByFQN(name), null));
            variants.addAll(filterClasses(phpIndex.getInterfacesByFQN(name), null));
        }
        return variants;
    }

    public static Collection<Function> getAllFunctions(Project project, Collection<String> functionNames) {
        Collection<Function> variants = new THashSet<>();
        PhpIndex phpIndex = getPhpIndex(project);

        for (String name : functionNames) {
            variants.addAll(phpIndex.getFunctionsByName(name));
        }
        return variants;
    }

    public static boolean isReferenceTo(@NotNull PhpClass originalClass, @NotNull ResolveResult[] results, @NotNull Project project, @NotNull String name) {
        for (ResolveResult result : results) {
            if (result.getElement() == originalClass) {
                return true;
            } else if (!(result.getElement() instanceof BaseLattePhpElement)) {
                continue;
            }

            if (!name.equals(((BaseLattePhpElement) result.getElement()).getPhpElementName())) {
                continue;
            }

            Collection<PhpClass> phpClasses = ((BaseLattePhpElement) result.getElement()).getPrevReturnType().getPhpClasses(project);
            if (phpClasses.size() == 0) {
                continue;
            }

            for (PhpClass phpClass : phpClasses) {
                if (isReferenceFor(originalClass, phpClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNullable(@NotNull PhpType type) {
        return false; //todo: replace with type.isNullable()
    }

    public static boolean isReferenceFor(@NotNull PhpClass originalClass, @NotNull PhpClass targetClass) {
        return isReferenceFor(originalClass.getFQN(), targetClass);
    }

    public static boolean isReferenceFor(@NotNull String originalClass, @NotNull PhpClass targetClass) {
        return isReferenceFor(new String[]{originalClass}, targetClass);
    }

    public static boolean isReferenceFor(@NotNull String[] originalClasses, @NotNull PhpClass targetClass) {
        List<String> normalized = new ArrayList<>();
        for (String originalClass : originalClasses) {
            originalClass = normalizeClassName(originalClass);
            normalized.add(originalClass);
            if (originalClass.equals(targetClass.getFQN())) {
                return true;
            }
        }


        ExtendsList extendsList = targetClass.getExtendsList();
        for (ClassReference reference : extendsList.getReferenceElements()) {
            String fqn = reference.getFQN();
            if (fqn == null) {
                continue;
            }

            for (String originalClass : normalized) {
                if (fqn.equals(originalClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<Field> getFieldsForPhpElement(@NotNull BaseLattePhpElement psiElement, @NotNull Project project) {
        List<Field> out = new ArrayList<>();
        NettePhpType phpType = psiElement.getPrevReturnType();
        String name = psiElement.getPhpElementName();
        boolean isConstant = psiElement instanceof LattePhpConstant;
        if (psiElement instanceof LattePhpVariable) {
            phpType = LatteUtil.findFirstLatteTemplateType(psiElement.getContainingFile());
            if (phpType == null) {
                return out;
            }
            name = ((LattePhpVariable) psiElement).getVariableName();
        }

        Collection<PhpClass> phpClasses = phpType.getPhpClasses(project);
        if (phpClasses.size() == 0) {
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

    public static List<Method> getMethodsForPhpElement(@NotNull LattePhpMethod psiElement, Project project) {
        List<Method> out = new ArrayList<>();
        Collection<PhpClass> phpClasses = psiElement.getPrevReturnType().getPhpClasses(project);
        if (phpClasses.size() > 0) {
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

    public static Collection<PhpNamespace> getNamespacesByName(Project project, String className) {
        return getPhpIndex(project).getNamespacesByName(className);
    }

    public static Collection<PhpNamespace> getAlNamespaces(Project project, Collection<String> namespaceNames) {
        Collection<PhpNamespace> variants = new THashSet<>();
        PhpIndex phpIndex = getPhpIndex(project);

        for (String name : namespaceNames) {
            variants.addAll(phpIndex.getNamespacesByName(name));
        }
        return variants;
    }

    public static Collection<String> getAllExistingNamespacesByName(Project project, String className) {
        return getPhpIndex(project).getAllChildNamespacesFqns(className);
    }

    public static Collection<String> getAllExistingFunctionNames(Project project, PrefixMatcher prefixMatcher) {
        return getPhpIndex(project).getAllFunctionNames(prefixMatcher);
    }


    public static Collection<String> getAllExistingClassNames(Project project, PrefixMatcher prefixMatcher) {
        return getPhpIndex(project).getAllClassFqns(prefixMatcher);
    }

    private static PhpIndex getPhpIndex(Project project) {
        return PhpIndex.getInstance(project);
    }

    private static Collection<PhpClass> filterClasses(Collection<PhpClass> classes, String namespace) {
        if (namespace == null) {
            return classes;
        }
        namespace = "\\" + namespace + "\\";
        Collection<PhpClass> result = new ArrayList<>();
        for (PhpClass cls : classes) {
            String classNs = cls.getNamespaceName();
            if (classNs.equals(namespace) || classNs.startsWith(namespace)) {
                result.add(cls);
            }
        }
        return result;
    }

    public static String normalizeClassName(@Nullable String className) {
        if (className != null && className.startsWith("#S")) {
            // since 2019.2.3 phpstorm started to prefix _static_ class type with "#S"
            className = className.substring(2);
        }

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