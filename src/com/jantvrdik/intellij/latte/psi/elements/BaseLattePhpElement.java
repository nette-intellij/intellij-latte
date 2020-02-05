package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiNameIdentifierOwner;
import com.jantvrdik.intellij.latte.utils.LattePhpType;

public interface BaseLattePhpElement extends PsiNameIdentifierOwner {

	public abstract LattePhpType getPhpType();

	public abstract String getPhpElementName();

}