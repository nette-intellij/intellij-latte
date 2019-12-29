package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiNameIdentifierOwner;

public interface LattePhpClassElement extends PsiNameIdentifierOwner {

	public abstract String getClassName();

}