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
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jantvrdik.intellij.latte.utils.PsiPositionedElement;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.completion.PhpLookupElement;
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
		if (!(element instanceof LattePhpVariable) || ((LattePhpVariable) element).isDefinition()) {
			return;
		}

		List<LookupElement> elements = attachPhpVariableCompletions(result, element, parameters.getOriginalFile().getVirtualFile());
		result.addAllElements(elements);

		if (parameters.getOriginalFile() instanceof LatteFile) {
			attachTemplateTypeCompletions(result, element.getProject(), (LatteFile) parameters.getOriginalFile());
		}
	}

	private void attachTemplateTypeCompletions(@NotNull CompletionResultSet result, @NotNull Project project, @NotNull LatteFile file) {
		LattePhpType type = LatteUtil.findFirstLatteTemplateType(file);
		if (type == null) {
			return;
		}

		Collection<PhpClass> phpClasses = type.getPhpClasses(project);
		if (phpClasses != null) {
			for (PhpClass phpClass : phpClasses) {
				for (Field field : phpClass.getFields()) {
					if (!field.isConstant() && field.getModifier().isPublic()) {
						PhpLookupElement lookupItem = getPhpLookupElement(field, "$" + field.getName());
						lookupItem.handler = PhpVariableInsertHandler.getInstance();
						try {
							lookupItem.icon = PhpIcons.PROPERTY_ICON;
						} catch (IllegalAccessError e) {
							// ignored
						}
						lookupItem.bold = true;
						result.addElement(lookupItem);
					}
				}
			}
		}
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

		List<LatteVariableSettings> defaultVariables = LatteConfiguration.INSTANCE.getVariables(psiElement.getProject());
		if (defaultVariables == null) {
			return lookupElements;
		}

		for (LatteVariableSettings variable : defaultVariables) {
			String variableName = variable.getVarName();
			if (foundVariables.stream().anyMatch(variableName::equals)) {
				continue;
			}

			LookupElementBuilder builder = LookupElementBuilder.create("$" + variableName);
			builder = builder.withInsertHandler(PhpVariableInsertHandler.getInstance());
			builder = builder.withTypeText(variable.toPhpType().toReadableString());
			builder = builder.withIcon(PhpIcons.VARIABLE_READ_ACCESS);
			builder = builder.withBoldness(false);
			lookupElements.add(builder);

			foundVariables.add(variableName);
		}
		return lookupElements;
	}

}