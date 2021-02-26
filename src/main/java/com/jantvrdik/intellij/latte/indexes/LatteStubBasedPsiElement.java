package com.jantvrdik.intellij.latte.indexes;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

public class LatteStubBasedPsiElement<T extends StubElement> extends StubBasedPsiElementBase<T> {
    public LatteStubBasedPsiElement(@NotNull T stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LatteStubBasedPsiElement(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + getNode().getElementType().toString() + ")('" + getNode().getText() + "')";
    }

}