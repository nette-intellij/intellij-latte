package com.jantvrdik.intellij.latte.indexes.extensions;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.jantvrdik.intellij.latte.psi.LattePhpMethod;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LattePhpMethodIndex extends StringStubIndexExtension<LattePhpMethod> {
    public static final StubIndexKey<String, LattePhpMethod> KEY = StubIndexKey.createIndexKey("latte.phpMethod.index");

    private static final LattePhpMethodIndex ourInstance = new LattePhpMethodIndex();

    public static LattePhpMethodIndex getInstance() {
        return ourInstance;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + 1;
    }

    @Override
    @NotNull
    public StubIndexKey<String, LattePhpMethod> getKey() {
        return KEY;
    }

    @Override
    public Collection<LattePhpMethod> get(@NotNull String key, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), key, project, scope, LattePhpMethod.class);
    }
}