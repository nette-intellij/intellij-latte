package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.psi.LattePhpContent;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.completion.insert.PhpFunctionInsertHandler;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Complete class names
 */
public class LattePhpFunctionCompletionProvider extends CompletionProvider<CompletionParameters> {

	public LattePhpFunctionCompletionProvider() {
		super();
	}

	@Override
	protected void addCompletions(
			@NotNull CompletionParameters params,
			ProcessingContext ctx,
			@NotNull CompletionResultSet results
	) {
		PsiElement curr = params.getPosition().getOriginalElement();
		if (PsiTreeUtil.getParentOfType(curr, LattePhpContent.class) == null) {
			return;
		}

		PrefixMatcher prefixMatcher = results.getPrefixMatcher();
		String prefix = prefixMatcher.getPrefix();
		if (prefix.contains("\\")) {
			int index = prefix.lastIndexOf("\\");
			prefixMatcher = prefixMatcher.cloneWithPrefix(prefix.substring(index + 1));
		}

		Project project = params.getPosition().getProject();
		Collection<String> functionNames = LattePhpUtil.getAllExistingFunctionNames(project, prefixMatcher);
		Collection<Function> variants = LattePhpUtil.getAllFunctions(project, functionNames);

		// Add variants
		for (Function item : variants) {
			PhpLookupElement lookupItem = LattePhpFunctionCompletionProvider.getPhpLookupElement(item, null);
			lookupItem.handler = PhpFunctionInsertHandler.getInstance();
			results.addElement(lookupItem);
		}
	}

	static PhpLookupElement getPhpLookupElement(@NotNull PhpNamedElement phpNamedElement, @Nullable String searchedWord) {
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