package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

abstract class BaseLatteCompletionProvider extends CompletionProvider<CompletionParameters> {
	PhpLookupElement getPhpLookupElement(@NotNull PhpNamedElement phpNamedElement, @Nullable String searchedWord) {
		PhpLookupElement lookupItem = new PhpLookupElement(phpNamedElement) {
			@Override
			public Set<String> getAllLookupStrings() {
				Set<String> original = super.getAllLookupStrings();
				Set<String> strings = new HashSet<String>(original.size() + 1);
				strings.addAll(original);
				strings.add(searchedWord == null ? this.getNamedElement().getFQN() : searchedWord);
				return strings;
			}
		};
		return lookupItem;
	}

}