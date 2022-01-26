package com.jantvrdik.intellij.latte.indexes.extensions;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.jantvrdik.intellij.latte.psi.LattePhpType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LattePhpTypeIndex extends StringStubIndexExtension<LattePhpType> {
    public static final StubIndexKey<String, LattePhpType> KEY = StubIndexKey.createIndexKey("latte.phpType.index");

    private static final LattePhpTypeIndex ourInstance = new LattePhpTypeIndex();

    public static LattePhpTypeIndex getInstance() {
        return ourInstance;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + 3;
    }

    @Override
    @NotNull
    public StubIndexKey<String, LattePhpType> getKey() {
        return KEY;
    }

    @Override
    public Collection<LattePhpType> get(@NotNull String key, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), key, project, scope, LattePhpType.class);
    }
}