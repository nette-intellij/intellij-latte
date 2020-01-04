package com.jantvrdik.intellij.latte.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.jantvrdik.intellij.latte.psi.LattePhpMethod;
import com.jantvrdik.intellij.latte.psi.LattePhpProperty;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import org.jetbrains.annotations.NotNull;

class LatteRenamePsiElementProcessor extends RenamePsiElementProcessor {

	@Override
	public boolean canProcessElement(@NotNull PsiElement psiElement) {
		if (
				psiElement instanceof LattePhpMethod
				|| psiElement instanceof LattePhpProperty
				|| psiElement instanceof Field
				|| psiElement instanceof Method) {
			return true;
		}
		return false;
	}

}
