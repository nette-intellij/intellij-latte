package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiNameIdentifierOwner;

public interface LattePhpStaticVariableElement extends PsiNameIdentifierOwner {

	public abstract String getVariableName();

}