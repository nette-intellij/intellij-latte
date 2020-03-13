package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.completion.handlers.PhpClassInsertHandler;
import com.jantvrdik.intellij.latte.psi.LattePhpContent;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Complete class names
 */
public class LattePhpClassCompletionProvider extends BaseLatteCompletionProvider {

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
		Collection<String> classNames = LattePhpUtil.getAllExistingClassNames(project, prefixMatcher);
		Collection<PhpNamedElement> variants = LattePhpUtil.getAllClassNamesAndInterfaces(project, classNames);

		// Add variants
		for (PhpNamedElement item : variants) {
			PhpLookupElement lookupItem = getPhpLookupElement(item, null);
			lookupItem.handler = PhpClassInsertHandler.getInstance();
			results.addElement(lookupItem);
		}
	}

}