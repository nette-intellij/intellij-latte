package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.completion.handlers.PhpVariableInsertHandler;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteDefaultVariable;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jantvrdik.intellij.latte.utils.PsiPositionedElement;
import com.jetbrains.php.PhpIcons;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LatteVariableCompletionProvider extends CompletionProvider<CompletionParameters> {

	public LatteVariableCompletionProvider() {
		super();
	}

	@Override
	protected void addCompletions(
			@NotNull CompletionParameters parameters,
			ProcessingContext context,
			@NotNull CompletionResultSet result
	) {
		PsiElement element = parameters.getPosition().getParent();
		if (!(element instanceof LattePhpVariable) || ((LattePhpVariable) element).isDefinition()) {
			return;
		}

		List<LookupElement> elements = attachPhpVariableCompletions(result, element, parameters.getOriginalFile().getVirtualFile());
		result.addAllElements(elements);
	}

	private List<LookupElement> attachPhpVariableCompletions(@NotNull CompletionResultSet result, @NotNull PsiElement psiElement, @NotNull VirtualFile virtualFile) {
		List<LookupElement> lookupElements = new ArrayList<LookupElement>();
		List<String> foundVariables = new ArrayList<String>();

		for (PsiPositionedElement element : LatteUtil.findVariablesDefinitionsInFileBeforeElement(psiElement, virtualFile)) {
			if (!(element.getElement() instanceof LattePhpVariable)) {
				continue;
			}

			String variableName = ((LattePhpVariable) element.getElement()).getVariableName();
			if (foundVariables.stream().anyMatch(variableName::equals)) {
				continue;
			}

			LookupElementBuilder builder = LookupElementBuilder.create(element.getElement(), "$" + variableName);
			builder = builder.withInsertHandler(PhpVariableInsertHandler.getInstance());
			builder = builder.withTypeText(((LattePhpVariable) element.getElement()).getPhpType().toReadableString());
			builder = builder.withIcon(PhpIcons.VARIABLE);
			builder = builder.withBoldness(true);
			lookupElements.add(builder);

			foundVariables.add(variableName);
		}

		List<LatteDefaultVariable> defaultVariables = LatteConfiguration.INSTANCE.getVariables(psiElement.getProject());
		if (defaultVariables == null) {
			return lookupElements;
		}

		for (LatteDefaultVariable variable : defaultVariables) {
			String variableName = variable.name;
			if (foundVariables.stream().anyMatch(variableName::equals)) {
				continue;
			}

			LookupElementBuilder builder = LookupElementBuilder.create("$" + variableName);
			builder = builder.withInsertHandler(PhpVariableInsertHandler.getInstance());
			builder = builder.withTypeText(variable.type.toReadableString());
			builder = builder.withIcon(PhpIcons.VARIABLE_READ_ACCESS);
			builder = builder.withBoldness(false);
			lookupElements.add(builder);

			foundVariables.add(variableName);
		}
		return lookupElements;
	}

}