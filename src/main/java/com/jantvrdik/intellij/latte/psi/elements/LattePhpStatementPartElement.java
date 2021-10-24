package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.LattePhpStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LattePhpStatementPartElement extends PsiElement {

	NettePhpType getReturnType();

	@NotNull
	LattePhpStatement getPhpStatement();

	@Nullable
	LattePhpStatementPartElement getPrevPhpStatementPart();

	@Nullable
	BaseLattePhpElement getPhpElement();

}