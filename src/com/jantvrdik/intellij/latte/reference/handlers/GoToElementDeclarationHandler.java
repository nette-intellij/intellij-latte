package com.jantvrdik.intellij.latte.reference.handlers;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.*;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GoToElementDeclarationHandler implements GotoDeclarationHandler {

	@Nullable
	@Override
	public PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement element, int offset, Editor editor) {
		if (element == null || element.getParent() == null) {
			return new PsiElement[0];
		}

		PsiElement parent = element.getParent();
		if (parent instanceof LattePhpClass) {
			Collection<PhpClass> classes = ((LattePhpClass) parent).getPhpType().getPhpClasses(element.getProject());
			return classes.toArray(new PsiElement[classes.size()]);

		}
		return new PsiElement[0];
	}

	@Nullable
	@Override
	public String getActionText(DataContext context) {
		return null;
	}


}
