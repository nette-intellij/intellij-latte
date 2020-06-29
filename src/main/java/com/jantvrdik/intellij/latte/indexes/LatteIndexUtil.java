package com.jantvrdik.intellij.latte.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.jantvrdik.intellij.latte.indexes.extensions.LattePhpClassIndex;
import com.jantvrdik.intellij.latte.indexes.extensions.LattePhpMethodIndex;
import com.jantvrdik.intellij.latte.psi.LattePhpClassReference;
import com.jantvrdik.intellij.latte.psi.LattePhpMethod;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LatteIndexUtil {
    public static Collection<LattePhpMethod> findMethodsByName(@NotNull Project project, String methodName) {
        return LattePhpMethodIndex.getInstance().get(methodName, project, GlobalSearchScope.allScope(project));
    }

    public static Collection<LattePhpClassReference> getClassesByFqn(@NotNull Project project, String fqn) {
        return LattePhpClassIndex.getInstance().get(
                LattePhpUtil.normalizeClassName(fqn),
                project,
                GlobalSearchScope.allScope(project)
        );
    }

}