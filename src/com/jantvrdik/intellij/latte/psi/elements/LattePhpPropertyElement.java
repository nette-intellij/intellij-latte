package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiNameIdentifierOwner;

public interface LattePhpPropertyElement extends PsiNameIdentifierOwner {

	public abstract String getPropertyName();

}