package com.jantvrdik.intellij.latte.reference.handlers;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
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

		} else if (parent instanceof LattePhpMethod) {
			Collection<PhpClass> phpClasses = ((LattePhpMethod) parent).getPhpType().getPhpClasses(element.getProject());
			if (phpClasses == null || phpClasses.size() == 0) {
				return new PsiElement[0];
			}

			String methodName = ((LattePhpMethod) parent).getMethodName();
			List<Method> methods = new ArrayList<>();
			for (PhpClass phpClass : phpClasses) {
				for (Method method : phpClass.getMethods()) {
					if (method.getName().equals(methodName)) {
						methods.add(method);
					}
				}
			}
			return methods.toArray(new PsiElement[methods.size()]);

		} else if (parent instanceof LattePhpConstant || parent instanceof LattePhpProperty || parent instanceof LattePhpStaticVariable) {
			LattePhpType phpType;
			String name;
			boolean isConstant;
			if (parent instanceof LattePhpConstant) {
				phpType = ((LattePhpConstant) parent).getPhpType();
				name = ((LattePhpConstant) parent).getConstantName();
				isConstant = true;
			} else if (parent instanceof LattePhpStaticVariable) {
				phpType = ((LattePhpStaticVariable) parent).getPhpType();
				name = ((LattePhpStaticVariable) parent).getVariableName();
				isConstant = false;
			} else {
				phpType = ((LattePhpProperty) parent).getPhpType();
				name = ((LattePhpProperty) parent).getPropertyName();
				isConstant = false;
			}

			Collection<PhpClass> phpClasses = phpType.getPhpClasses(element.getProject());
			if (phpClasses == null || phpClasses.size() == 0) {
				return new PsiElement[0];
			}

			List<Field> fields = new ArrayList<>();
			for (PhpClass phpClass : phpClasses) {
				for (Field field : phpClass.getFields()) {
					if (
							((isConstant && field.isConstant()) || (!isConstant && !field.isConstant()))
							&& field.getName().equals(name)
					) {
						fields.add(field);
					}
				}
			}
			return fields.toArray(new PsiElement[fields.size()]);

		}
		return new PsiElement[0];
	}

	@Nullable
	@Override
	public String getActionText(DataContext context) {
		return null;
	}


}
