package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.completion.insert.PhpReferenceInsertHandler;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Complete class names
 */
public class LattePhpClassCompletionProvider extends CompletionProvider<CompletionParameters> {

	public LattePhpClassCompletionProvider() {
		super();
	}

	@Override
	protected void addCompletions(
			@NotNull CompletionParameters params,
			ProcessingContext ctx,
			@NotNull CompletionResultSet results
	) {
		PsiElement curr = params.getPosition().getOriginalElement();
		boolean incompleteKey = isIncompleteKey(curr);
		if (!incompleteKey) {
			return;
		}

		PrefixMatcher prefixMatcher = results.getPrefixMatcher();
		String prefix = prefixMatcher.getPrefix();
		if (prefix.contains("\\")) {
			int index = prefix.lastIndexOf("\\");
			prefixMatcher = prefixMatcher.cloneWithPrefix(prefix.substring(index + 1));
		}

		Project project = params.getPosition().getProject();
		Collection<String> classNames = LattePhpUtil.getAllExistingClassNames(project, prefixMatcher);
		Collection<PhpNamedElement> variants = LattePhpUtil.getAllClassNamesAndInterfaces(project, classNames);

		// Add variants
		for (PhpNamedElement item : variants) {
			PhpLookupElement lookupItem = LattePhpClassCompletionProvider.getPhpLookupElement(item, null);
			lookupItem.handler = PhpReferenceInsertHandler.getInstance();
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

	private static boolean isIncompleteKey(PsiElement el) {
		ASTNode node = el.getNode();
		if (node.getElementType() == LatteTypes.T_PHP_VAR_TYPE || node.getElementType() == LatteTypes.T_MACRO_ARGS) {
			return true;
		}
		return false;
	}

}