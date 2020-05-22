package com.jantvrdik.intellij.latte.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jantvrdik.intellij.latte.LatteLanguage;
import com.jantvrdik.intellij.latte.completion.handlers.AttrMacroInsertHandler;
import com.jantvrdik.intellij.latte.completion.handlers.FilterInsertHandler;
import com.jantvrdik.intellij.latte.completion.handlers.MacroInsertHandler;
import com.jantvrdik.intellij.latte.completion.providers.LattePhpCompletionProvider;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.settings.LatteTagSettings;
import com.jantvrdik.intellij.latte.settings.LatteFilterSettings;
import com.jantvrdik.intellij.latte.utils.LatteMimeTypes;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Provides basic code completion for names of both classic and attribute macros.
 */
public class LatteCompletionContributor extends CompletionContributor {

	public LatteCompletionContributor() {

		extend(CompletionType.BASIC, PlatformPatterns.psiElement(LatteTypes.T_MACRO_NAME).withLanguage(LatteLanguage.INSTANCE), new CompletionProvider<CompletionParameters>() {
			@Override
			protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
				PsiElement parent = parameters.getPosition().getParent();
				Project project = parameters.getOriginalFile().getProject();
				attachClassicMacrosCompletion(project, result, parent instanceof LatteMacroCloseTag);
			}
		});

		extend(CompletionType.BASIC, PlatformPatterns.psiElement(LatteTypes.T_HTML_TAG_NATTR_NAME).withLanguage(LatteLanguage.INSTANCE), new CompletionProvider<CompletionParameters>() {
			@Override
			protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
				Project project = parameters.getOriginalFile().getProject();
				Map<String, LatteTagSettings> customMacros = LatteConfiguration.getInstance(project).getTags();
				result.addAllElements(getAttrMacroCompletions(customMacros));
			}
		});

		extend(CompletionType.BASIC, PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE), new CompletionProvider<CompletionParameters>() {
			@Override
			protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
				PsiElement element = parameters.getPosition().getParent();
				if (!LatteUtil.matchParentMacroName(element, "contentType")) {
					return;
				}
				attachContentTypes(result);
			}
		});

		extend(CompletionType.BASIC, PlatformPatterns.psiElement(LatteTypes.T_MACRO_FILTERS).withLanguage(LatteLanguage.INSTANCE), new CompletionProvider<CompletionParameters>() {
			@Override
			protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
				PsiElement element = parameters.getPosition().getParent();
				if (!(element instanceof LatteMacroModifier)) {
					return;
				}
				LatteMacroClassic macroClassic = PsiTreeUtil.getParentOfType(element, LatteMacroClassic.class);
				if (macroClassic == null) {
					return;
				}

				if (!((LatteMacroModifier) element).isVariableModifier()) {
					LatteTagSettings macro = LatteConfiguration.getInstance(element.getProject()).getTag(macroClassic.getOpenTag().getMacroName());
					if (macro == null || !macro.isAllowedModifiers()) {
						return;
					}
				}

				Map<String, LatteFilterSettings> customModifiers = LatteConfiguration.getInstance(element.getProject()).getFilters();
				result.addAllElements(getClassicModifierCompletions(customModifiers));
			}
		});

		extend(
				CompletionType.BASIC,
				PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE),
				new LattePhpCompletionProvider()
		);
	}

	private void attachContentTypes(@NotNull CompletionResultSet result) {
		for (String contentType : LatteMimeTypes.getDefaultMimeTypes()) {
			result.addElement(LookupElementBuilder.create(contentType));
		}
	}

	private void attachClassicMacrosCompletion(@NotNull Project project, @NotNull CompletionResultSet result, boolean prefix) {
		Map<String, LatteTagSettings> customMacros = LatteConfiguration.getInstance(project).getTags();
		result.addAllElements(getClassicMacroCompletions(customMacros, prefix));
	}

	/**
	 * Builds list of lookup elements for code completion of classic macros.
	 */
	private List<LookupElement> getClassicMacroCompletions(Map<String, LatteTagSettings> macros, boolean prefix) {
		List<LookupElement> lookupElements = new ArrayList<LookupElement>(macros.size());
		for (LatteTagSettings macro : macros.values()) {
			if (macro.getType() != LatteTagSettings.Type.ATTR_ONLY && (!prefix || macro.getType() == LatteTagSettings.Type.PAIR)) {
				lookupElements.add(createBuilderForMacro(macro, prefix));
			}
		}
		return lookupElements;
	}

	/**
	 * Builds list of lookup elements for code completion of classic macros.
	 */
	private List<LookupElement> getClassicModifierCompletions(Map<String, LatteFilterSettings> modifiers) {
		List<LookupElement> lookupElements = new ArrayList<LookupElement>(modifiers.size());
		for (LatteFilterSettings modifier : modifiers.values()) {
			lookupElements.add(createBuilderWithHelp(modifier));
		}
		return lookupElements;
	}

	private LookupElementBuilder createBuilderWithHelp(LatteFilterSettings modifier) {
		LookupElementBuilder builder = LookupElementBuilder.create(modifier.getModifierName());
		if (modifier.getModifierDescription().trim().length() > 0) {
			builder = builder.withTypeText(modifier.getModifierDescription());
		}
		if (modifier.getModifierHelp().trim().length() > 0) {
			builder = builder.withTailText(modifier.getModifierHelp());
		}
		builder = builder.withInsertHandler(FilterInsertHandler.getInstance());
		return builder.withIcon(LatteIcons.MODIFIER);
	}

	private LookupElementBuilder createBuilderForMacro(LatteTagSettings macro, boolean prefix) {
		LookupElementBuilder builder = LookupElementBuilder.create((prefix ? "/" : "") + macro.getMacroName());
		builder = builder.withIcon(LatteIcons.MACRO);
		builder = builder.withInsertHandler(MacroInsertHandler.getInstance());
		return builder.withTypeText(macro.getType().toString(), true);
	}

	private LookupElementBuilder createBuilderForTag(String name) {
		LookupElementBuilder builder = LookupElementBuilder.create(name);
		builder = builder.withInsertHandler(AttrMacroInsertHandler.getInstance());
		return builder.withIcon(LatteIcons.N_TAG);
	}

	/**
	 * Builds list of lookup elements for code completion of attribute macros.
	 */
	private List<LookupElement> getAttrMacroCompletions(Map<String, LatteTagSettings> macros) {
		List<LookupElement> lookupElements = new ArrayList<LookupElement>(macros.size());
		for (LatteTagSettings macro : macros.values()) {
			if (macro.getType() != LatteTagSettings.Type.UNPAIRED) {
				lookupElements.add(createBuilderForTag("n:" + macro.getMacroName()));
				if (macro.getType() == LatteTagSettings.Type.PAIR || macro.getType() == LatteTagSettings.Type.AUTO_EMPTY) {
					lookupElements.add(createBuilderForTag("n:tag-" + macro.getMacroName()));
					lookupElements.add(createBuilderForTag("n:inner-" + macro.getMacroName()));
				}
			}
		}
		return lookupElements;
	}
}
