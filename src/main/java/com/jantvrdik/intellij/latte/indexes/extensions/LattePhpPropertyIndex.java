package com.jantvrdik.intellij.latte.indexes.extensions;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.jantvrdik.intellij.latte.psi.LattePhpProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LattePhpPropertyIndex extends StringStubIndexExtension<LattePhpProperty> {
    public static final StubIndexKey<String, LattePhpProperty> KEY = StubIndexKey.createIndexKey("latte.phpProperty.index");

    private static final LattePhpPropertyIndex ourInstance = new LattePhpPropertyIndex();

    public static LattePhpPropertyIndex getInstance() {
        return ourInstance;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + 2;
    }

    @Override
    @NotNull
    public StubIndexKey<String, LattePhpProperty> getKey() {
        return KEY;
    }

    @Override
    public Collection<LattePhpProperty> get(@NotNull String key, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), key, project, scope, LattePhpProperty.class);
    }
}