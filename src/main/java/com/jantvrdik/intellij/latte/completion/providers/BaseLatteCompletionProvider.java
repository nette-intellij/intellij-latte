package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.psi.LattePhpClassUsage;
import com.jantvrdik.intellij.latte.php.LattePhpUtil;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

abstract class BaseLatteCompletionProvider extends CompletionProvider<CompletionParameters> {
	protected PhpLookupElement getPhpLookupElement(@NotNull PhpNamedElement phpNamedElement, @Nullable String searchedWord) {
		return getPhpLookupElement(phpNamedElement, searchedWord, null);
	}

	protected PhpLookupElement getPhpLookupElement(@NotNull PhpNamedElement phpNamedElement, @Nullable String searchedWord, @Nullable String tailText) {
		PhpLookupElement element = new PhpLookupElement(phpNamedElement) {
			@Override
			public Set<String> getAllLookupStrings() {
				Set<String> original = super.getAllLookupStrings();
				Set<String> strings = new HashSet<>(original.size() + 1);
				strings.addAll(original);
				if (searchedWord != null || this.getNamedElement() != null) {
					strings.add(searchedWord == null ? this.getNamedElement().getFQN() : searchedWord);
				}
				return strings;
			}
		};
		if (tailText != null) {
			element.tailText = tailText;
		}
		return element;
	}

	protected String getNamespaceName(PsiElement element) {
		LattePhpClassUsage classUsage = PsiTreeUtil.getParentOfType(element, LattePhpClassUsage.class);
		String namespaceName = "";
		if (classUsage != null) {
			String className = classUsage.getClassName().substring(1);
			if (className.length() == 1) {
				return "";
			}
			className = classUsage.getClassName();
			int index = className.lastIndexOf("\\");
			namespaceName = className.substring(0, index);
		}
		return LattePhpUtil.normalizeClassName(namespaceName);
	}

}