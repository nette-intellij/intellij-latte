package com.jantvrdik.intellij.latte.utils;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import gnu.trove.THashSet;

import java.util.ArrayList;
import java.util.Collection;

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

    public static Collection<Method> findMethods(Collection<PhpClass> classes, String methodName) {
        Collection<Method> methods = new ArrayList<Method>();
        classes.stream().map(
                phpClass -> phpClass.getMethods().stream()
                    .filter(method -> method.getName().equals(methodName))
                    .map(methods::add));
        return methods;
    }

    public static String normalizePhpVariable(String name) {
        return name.startsWith("$") ? name.substring(1) : name;
    }

    public static Collection<PhpClass> getClassesByFQN(PsiElement element) {
        return getClassesByFQN(element.getProject(), element.getText());
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