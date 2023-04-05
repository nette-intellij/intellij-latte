package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.completion.handlers.MacroCustomFunctionInsertHandler;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.LattePhpContent;
import com.jantvrdik.intellij.latte.settings.LatteFunctionSettings;
import com.jantvrdik.intellij.latte.php.LattePhpUtil;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.completion.insert.PhpFunctionInsertHandler;
import com.jetbrains.php.lang.psi.elements.Function;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Complete class names
 */
public class LattePhpFunctionCompletionProvider extends BaseLatteCompletionProvider {

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
			PhpLookupElement lookupItem = getPhpLookupElement(item, null);
			lookupItem.handler = PhpFunctionInsertHandler.getInstance();
			results.addElement(lookupItem);
		}

		Collection<LatteFunctionSettings> customFunctions = LatteConfiguration.getInstance(project).getFunctions();
		for (LatteFunctionSettings item : customFunctions) {
			LookupElementBuilder builder = createBuilderWithHelp(item);
			results.addElement(builder);
		}
	}

	private LookupElementBuilder createBuilderWithHelp(LatteFunctionSettings settings) {
		LookupElementBuilder builder = LookupElementBuilder.create(settings.getFunctionName());
		builder = builder.withIcon(PhpIcons.FUNCTION);
		builder = builder.withInsertHandler(MacroCustomFunctionInsertHandler.getInstance());
		if (settings.getFunctionHelp().trim().length() > 0) {
			builder = builder.withTailText(settings.getFunctionHelp());
		}
		return builder.withTypeText(settings.getFunctionReturnType());
	}

}