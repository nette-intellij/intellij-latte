package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpVariableStub;
import com.jantvrdik.intellij.latte.utils.LattePhpCachedVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LattePhpVariableElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpVariableStub> {

	String getVariableName();

	@Nullable LattePhpCachedVariable getCachedVariable();

	boolean isDefinition();

	@Nullable PsiElement getVariableContext();

	@NotNull List<LattePhpCachedVariable> getVariableDefinition();

}