package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.psi.*;
import org.jetbrains.annotations.NotNull;

public class LattePhpCompletionProvider extends BaseLatteCompletionProvider {

	private final LattePhpClassCompletionProvider classCompletionProvider;

	public LattePhpCompletionProvider() {
		super();
		classCompletionProvider = new LattePhpClassCompletionProvider();
	}

	@Override
	protected void addCompletions(
			@NotNull CompletionParameters parameters,
			ProcessingContext context,
			@NotNull CompletionResultSet result
	) {
		PsiElement current = parameters.getPosition();
		PsiElement element = current.getParent();
		if (!(element instanceof LatteMacroModifier) && !(element instanceof LattePhpString)) {
			classCompletionProvider.addCompletions(parameters, context, result);
		}
	}

}