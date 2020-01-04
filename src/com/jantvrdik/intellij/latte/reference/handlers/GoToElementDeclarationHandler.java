package com.jantvrdik.intellij.latte.reference.handlers;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jetbrains.php.lang.psi.elements.Field;
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

		} else if (parent instanceof LattePhpConstant || parent instanceof LattePhpStaticVariable || parent instanceof LattePhpVariable) {
			List<Field> out = new ArrayList<Field>();
			List<Field> fields = LattePhpUtil.getFieldsForPhpElement((BaseLattePhpElement) parent);
			String elementName = ((BaseLattePhpElement) parent).getPhpElementName();
			for (Field field : fields) {
				if (field.getName().equals(elementName)) {
					out.add(field);
				}
			}
			return out.toArray(new PsiElement[out.size()]);

		}
		return new PsiElement[0];
	}

	@Nullable
	@Override
	public String getActionText(DataContext context) {
		return null;
	}


}
