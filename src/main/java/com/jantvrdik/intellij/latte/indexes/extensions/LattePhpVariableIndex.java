package com.jantvrdik.intellij.latte.indexes.extensions;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LattePhpVariableIndex extends StringStubIndexExtension<LattePhpVariable> {
    public static final StubIndexKey<String, LattePhpVariable> KEY = StubIndexKey.createIndexKey("latte.phpVariable.index");

    private static final LattePhpVariableIndex ourInstance = new LattePhpVariableIndex();

    public static LattePhpVariableIndex getInstance() {
        return ourInstance;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + 3;
    }

    @Override
    @NotNull
    public StubIndexKey<String, LattePhpVariable> getKey() {
        return KEY;
    }

    @Override
    public Collection<LattePhpVariable> get(@NotNull String key, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), key, project, scope, LattePhpVariable.class);
    }
}