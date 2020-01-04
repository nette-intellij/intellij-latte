package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.completion.handlers.PhpVariableInsertHandler;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.completion.insert.PhpFieldInsertHandler;
import com.jetbrains.php.completion.insert.PhpMethodInsertHandler;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LattePhpCompletionProvider extends CompletionProvider<CompletionParameters> {

	public LattePhpCompletionProvider() {
		super();
	}

	@Override
	protected void addCompletions(
			@NotNull CompletionParameters parameters,
			ProcessingContext context,
			@NotNull CompletionResultSet result
	) {
		PsiElement element = parameters.getPosition().getParent();
		if (
				element instanceof LattePhpStaticVariable
						|| element instanceof LattePhpConstant
						|| (element instanceof LattePhpMethod && ((LattePhpMethod) element).isStatic())) {
			attachPhpCompletions(result, (BaseLattePhpElement) element, true);

		} else if (element instanceof LattePhpProperty || (element instanceof LattePhpMethod && !((LattePhpMethod) element).isStatic())) {
			attachPhpCompletions(result, (BaseLattePhpElement) element, false);
		}
	}

	private void attachPhpCompletions(@NotNull CompletionResultSet result, @NotNull BaseLattePhpElement psiElement, boolean isStatic) {
		LattePhpType type = psiElement.getPhpType();

		Collection<PhpClass> phpClasses = type.getPhpClasses(psiElement.getProject());
		if (phpClasses == null) {
			return;
		}

		for (PhpClass phpClass : phpClasses) {
			for (Method method : phpClass.getMethods()) {
				PhpModifier modifier = method.getModifier();
				if (modifier.isPublic() && canShowCompletionElement(isStatic, modifier)) {
					PhpLookupElement lookupItem = LattePhpClassCompletionProvider.getPhpLookupElement(method, method.getName());
					lookupItem.handler = PhpMethodInsertHandler.getInstance();
					result.addElement(lookupItem);
				}
			}

			for (Field field : phpClass.getFields()) {
				PhpModifier modifier = field.getModifier();
				if (modifier.isPublic()) {
					if (isStatic) {
						if (field.isConstant()) {
							PhpLookupElement lookupItem = LattePhpClassCompletionProvider.getPhpLookupElement(field, field.getName());
							lookupItem.handler = PhpFieldInsertHandler.getInstance();
							result.addElement(lookupItem);

						} else if (modifier.isStatic()) {
							PhpLookupElement lookupItem = LattePhpClassCompletionProvider.getPhpLookupElement(field, "$" + field.getName());
							lookupItem.handler = PhpVariableInsertHandler.getInstance();
							result.addElement(lookupItem);
						}

					} else {
						if (!field.isConstant() && !modifier.isStatic()) {
							PhpLookupElement lookupItem = LattePhpClassCompletionProvider.getPhpLookupElement(field, field.getName());
							lookupItem.handler = PhpFieldInsertHandler.getInstance();
							result.addElement(lookupItem);
						}
					}
				}
			}

			if (isStatic) {
				for (String nativeConstant : LattePhpUtil.getNativeClassConstants()) {
					result.addElement(LookupElementBuilder.create(nativeConstant));
				}
			}
		}
	}

	private boolean canShowCompletionElement(boolean isStatic, @NotNull PhpModifier modifier) {
		return (isStatic && modifier.isStatic()) || (!isStatic && !modifier.isStatic());
	}

}