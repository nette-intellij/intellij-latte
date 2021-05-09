package com.jantvrdik.intellij.latte.completion.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.completion.handlers.PhpVariableInsertHandler;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jantvrdik.intellij.latte.utils.PsiPositionedElement;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LatteVariableCompletionProvider extends BaseLatteCompletionProvider {

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
		if ((element instanceof LattePhpVariable) && ((LattePhpVariable) element).isDefinition()) {
			return;
		}

		List<LookupElement> elements = attachPhpVariableCompletions(element, parameters.getOriginalFile().getVirtualFile());
		result.addAllElements(elements);

		if (parameters.getOriginalFile() instanceof LatteFile) {
			attachTemplateTypeCompletions(result, element.getProject(), (LatteFile) parameters.getOriginalFile());
		}
	}

	private void attachTemplateTypeCompletions(@NotNull CompletionResultSet result, @NotNull Project project, @NotNull LatteFile file) {
		NettePhpType type = LatteUtil.findFirstLatteTemplateType(file);
		if (type == null) {
			return;
		}

		Collection<PhpClass> phpClasses = type.getPhpClasses(project);
		for (PhpClass phpClass : phpClasses) {
			for (Field field : phpClass.getFields()) {
				if (!field.isConstant() && field.getModifier().isPublic()) {
					LookupElementBuilder builder = LookupElementBuilder.create(field, "$" + field.getName());
					builder = builder.withInsertHandler(PhpVariableInsertHandler.getInstance());
					builder = builder.withTypeText(NettePhpType.create(field.getType()).toString());
					builder = builder.withIcon(PhpIcons.VARIABLE);
					if (field.isDeprecated() || field.isInternal()) {
						builder = builder.withStrikeoutness(true);
					}
					result.addElement(builder);
				}
			}
		}
	}

	private List<LookupElement> attachPhpVariableCompletions(@NotNull PsiElement psiElement, @NotNull VirtualFile virtualFile) {
		List<LookupElement> lookupElements = new ArrayList<>();
		List<String> foundVariables = new ArrayList<>();

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
			builder = builder.withTypeText(((LattePhpVariable) element.getElement()).getPhpType().toString());
			builder = builder.withIcon(PhpIcons.VARIABLE);
			builder = builder.withBoldness(true);
			lookupElements.add(builder);

			foundVariables.add(variableName);
		}

		Collection<LatteVariableSettings> defaultVariables = LatteConfiguration.getInstance(psiElement.getProject()).getVariables();

		for (LatteVariableSettings variable : defaultVariables) {
			String variableName = variable.getVarName();
			if (foundVariables.stream().anyMatch(variableName::equals)) {
				continue;
			}

			LookupElementBuilder builder = LookupElementBuilder.create("$" + variableName);
			builder = builder.withInsertHandler(PhpVariableInsertHandler.getInstance());
			builder = builder.withTypeText(variable.toPhpType().toString());
			builder = builder.withIcon(PhpIcons.VARIABLE_READ_ACCESS);
			builder = builder.withBoldness(false);
			lookupElements.add(builder);

			foundVariables.add(variableName);
		}
		return lookupElements;
	}

}