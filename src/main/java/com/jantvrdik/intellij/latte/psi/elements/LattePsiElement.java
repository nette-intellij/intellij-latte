package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import org.jetbrains.annotations.Nullable;

public interface LattePsiElement extends PsiElement {

	@Nullable LatteFile getLatteFile();

}