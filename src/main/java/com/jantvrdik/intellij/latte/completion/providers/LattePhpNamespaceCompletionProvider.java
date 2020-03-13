package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.psi.LattePhpContent;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.completion.PhpCompletionUtil;
import com.jetbrains.php.completion.insert.PhpNamespaceInsertHandler;
import org.jetbrains.annotations.NotNull;


public class LattePhpNamespaceCompletionProvider extends CompletionProvider<CompletionParameters> {

	public LattePhpNamespaceCompletionProvider() {
	}

	@Override
	protected void addCompletions(@NotNull CompletionParameters params, ProcessingContext context, @NotNull CompletionResultSet result) {
		PsiElement curr = params.getPosition().getOriginalElement();
		if (PsiTreeUtil.getParentOfType(curr, LattePhpContent.class) == null) {
			return;
		}

		PhpIndex phpIndex = PhpIndex.getInstance(curr.getProject());
		String prefix = result.getPrefixMatcher().getPrefix();
		String namespace = "";
		if (prefix.contains("\\")) {
			int index = prefix.lastIndexOf("\\");
			namespace = prefix.substring(0, index) + "\\";
			prefix = prefix.substring(index + 1);
		}
		PhpCompletionUtil.addSubNamespaces(namespace, result.withPrefixMatcher(prefix), phpIndex, PhpNamespaceInsertHandler.getInstance());
	}
}
