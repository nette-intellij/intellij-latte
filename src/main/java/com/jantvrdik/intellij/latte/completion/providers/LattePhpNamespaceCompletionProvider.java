package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.completion.handlers.PhpNamespaceInsertHandler;
import com.jantvrdik.intellij.latte.psi.LattePhpContent;
import com.jantvrdik.intellij.latte.php.LattePhpUtil;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class LattePhpNamespaceCompletionProvider extends BaseLatteCompletionProvider {

	public LattePhpNamespaceCompletionProvider() {
	}

	@Override
	protected void addCompletions(@NotNull CompletionParameters params, ProcessingContext context, @NotNull CompletionResultSet result) {
		PsiElement curr = params.getPosition().getOriginalElement();
		if (PsiTreeUtil.getParentOfType(curr, LattePhpContent.class) == null) {
			return;
		}

		String namespaceName = getNamespaceName(curr);
		Collection<String> namespaceNames = LattePhpUtil.getAllExistingNamespacesByName(curr.getProject(), namespaceName);
		Collection<PhpNamespace> namespaces = LattePhpUtil.getAlNamespaces(curr.getProject(), namespaceNames);
		for (PhpNamespace namespace : namespaces) {
			PhpLookupElement lookupItem = getPhpLookupElement(namespace, null, getTypeText(namespace.getParentNamespaceName()));
			lookupItem.handler = PhpNamespaceInsertHandler.getInstance();
			result.addElement(lookupItem);
		}
	}

	@Nullable
	private String getTypeText(String parentNamespace) {
		if (parentNamespace.length() > 1) {
			if (parentNamespace.startsWith("\\")) {
				parentNamespace = parentNamespace.substring(1);
			}
			if (parentNamespace.endsWith("\\") && parentNamespace.length() > 1) {
				parentNamespace = parentNamespace.substring(0, parentNamespace.length() - 1);
			}
			return " [" + parentNamespace + "]";
		}
		return null;
	}

}
