package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.LattePhpStatement;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LattePhpStatementPartElement extends PsiElement {

	public abstract LattePhpType getPhpType();

	@NotNull
	public LattePhpStatement getPhpStatement();

	@Nullable
	public LattePhpStatementPartElement getPrevPhpStatementPart();

	@Nullable
	public BaseLattePhpElement getPhpElement();

}