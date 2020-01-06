package com.jantvrdik.intellij.latte.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.jantvrdik.intellij.latte.psi.*;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import org.jetbrains.annotations.NotNull;

class LatteRenamePsiElementProcessor extends RenamePsiElementProcessor {

	@Override
	public boolean canProcessElement(@NotNull PsiElement psiElement) {
		return psiElement instanceof LattePhpMethod
				|| psiElement instanceof LattePhpClass
				|| psiElement instanceof LattePhpConstant
				|| psiElement instanceof LattePhpStaticVariable
				|| psiElement instanceof LattePhpProperty
				|| psiElement instanceof Field
				|| psiElement instanceof Method;
	}
}
