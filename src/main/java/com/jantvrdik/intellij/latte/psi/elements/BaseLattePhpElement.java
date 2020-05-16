package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import org.jetbrains.annotations.Nullable;

public interface BaseLattePhpElement extends PsiNameIdentifierOwner {

	public abstract LattePhpType getPhpType();

	public abstract String getPhpElementName();

	@Nullable
	public abstract PsiElement getTextElement();

}