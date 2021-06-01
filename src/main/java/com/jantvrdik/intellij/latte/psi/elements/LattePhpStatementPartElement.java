package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.LattePhpStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LattePhpStatementPartElement extends PsiElement {

	public abstract NettePhpType getPhpType();

	@NotNull
	public LattePhpStatement getPhpStatement();

	@Nullable
	public LattePhpStatementPartElement getPrevPhpStatementPart();

	@Nullable
	public BaseLattePhpElement getPhpElement();

}