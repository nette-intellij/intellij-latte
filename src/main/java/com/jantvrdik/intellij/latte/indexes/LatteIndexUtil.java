package com.jantvrdik.intellij.latte.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.jantvrdik.intellij.latte.indexes.extensions.*;
import com.jantvrdik.intellij.latte.php.LattePhpVariableUtil;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.php.LattePhpUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LatteIndexUtil {
    public static Collection<LattePhpMethod> findMethodsByName(@NotNull Project project, String name) {
        return LattePhpMethodIndex.getInstance().get(name, project, GlobalSearchScope.allScope(project));
    }

    public static Collection<LatteMacroModifier> findFiltersByName(@NotNull Project project, String name) {
        return LatteFilterIndex.getInstance().get(name, project, GlobalSearchScope.allScope(project));
    }

    public static Collection<LattePhpConstant> findConstantsByName(@NotNull Project project, String name) {
        return LattePhpConstantIndex.getInstance().get(name, project, GlobalSearchScope.allScope(project));
    }

    public static Collection<LattePhpProperty> findPropertiesByName(@NotNull Project project, String name) {
        return LattePhpPropertyIndex.getInstance().get(name, project, GlobalSearchScope.allScope(project));
    }

    public static Collection<LattePhpStaticVariable> findStaticVariablesByName(@NotNull Project project, String name) {
        return LattePhpStaticVariableIndex.getInstance().get(
                LattePhpVariableUtil.normalizePhpVariable(name),
                project,
                GlobalSearchScope.allScope(project)
        );
    }

    public static Collection<LattePhpNamespaceReference> findNamespacesByFqn(@NotNull Project project, String fqn) {
        return LattePhpNamespaceIndex.getInstance().get(
                LattePhpUtil.normalizeClassName(fqn),
                project,
                GlobalSearchScope.allScope(project)
        );
    }

    public static Collection<LattePhpVariable> findVariablesByName(@NotNull Project project, String name) {
        return LattePhpVariableIndex.getInstance().get(
                LattePhpVariableUtil.normalizePhpVariable(name),
                project,
                GlobalSearchScope.allScope(project)
        );
    }

    public static Collection<LattePhpClassReference> getClassesByFqn(@NotNull Project project, String fqn) {
        return LattePhpClassIndex.getInstance().get(
                LattePhpUtil.normalizeClassName(fqn),
                project,
                GlobalSearchScope.allScope(project)
        );
    }

}