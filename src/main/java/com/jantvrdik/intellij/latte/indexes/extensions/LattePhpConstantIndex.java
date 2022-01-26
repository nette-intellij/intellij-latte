package com.jantvrdik.intellij.latte.indexes.extensions;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.jantvrdik.intellij.latte.psi.LattePhpConstant;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LattePhpConstantIndex extends StringStubIndexExtension<LattePhpConstant> {
    public static final StubIndexKey<String, LattePhpConstant> KEY = StubIndexKey.createIndexKey("latte.phpConstant.index");

    private static final LattePhpConstantIndex ourInstance = new LattePhpConstantIndex();

    public static LattePhpConstantIndex getInstance() {
        return ourInstance;
    }

    @Override
    public int getVersion() {
        return super.getVersion() + 3;
    }

    @Override
    @NotNull
    public StubIndexKey<String, LattePhpConstant> getKey() {
        return KEY;
    }

    @Override
    public Collection<LattePhpConstant> get(@NotNull String key, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), key, project, scope, LattePhpConstant.class);
    }
}