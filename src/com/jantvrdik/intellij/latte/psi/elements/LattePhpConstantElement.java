package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiNameIdentifierOwner;

public interface LattePhpConstantElement extends PsiNameIdentifierOwner {

	public abstract String getConstantName();

}