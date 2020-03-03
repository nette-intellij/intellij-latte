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
import com.jantvrdik.intellij.latte.completion.handlers.MacroInsertHandler;
import com.jantvrdik.intellij.latte.completion.providers.LattePhpCompletionProvider;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteMacro;
import com.jantvrdik.intellij.latte.config.LatteModifier;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.utils.LatteMimeTypes;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Provides basic code completion for names of both classic and attribute macros.
 */
public class LatteCompletionContributor extends CompletionContributor {

	/** cached lookup elements for standard classic macros */
	private List<LookupElement> classicMacrosCompletions;

	/** cached lookup elements for standard classic macros */
	private List<LookupElement> classicPrefixedMacrosCompletions;

	/** cached lookup elements for standard attribute macros */
	private List<LookupElement> attrMacrosCompletions;

	/** cached lookup elements for standard attribute macros */
	private List<LookupElement> classicModifiersCompletions;

	public LatteCompletionContributor() {
		initStandardMacrosCompletions();
		initStandardModifiersCompletions();

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
				Map<String, LatteMacro> customMacros = LatteConfiguration.INSTANCE.getCustomMacros(project);
				result.addAllElements(attrMacrosCompletions);
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
					LatteMacro macro = LatteConfiguration.INSTANCE.getMacro(element.getProject(), macroClassic.getOpenTag().getMacroName());
					if (macro == null || !macro.allowedModifiers) {
						return;
					}
				}

				Map<String, LatteModifier> customModifiers = LatteConfiguration.INSTANCE.getCustomModifiers(element.getProject());
				result.addAllElements(classicModifiersCompletions);
				result.addAllElements(getClassicModifierCompletions(customModifiers));
			}
		});

		extend(
				CompletionType.BASIC,
				PlatformPatterns.psiElement().withLanguage(LatteLanguage.INSTANCE),
				new LattePhpCompletionProvider()
		);
	}

	/**
	 * Prepares lists of lookup elements for both classic and attribute standard macros.
	 */
	private void initStandardMacrosCompletions() {
		Map<String, LatteMacro> macros = LatteConfiguration.INSTANCE.getStandardMacros();
		classicMacrosCompletions = getClassicMacroCompletions(macros, false);
		classicPrefixedMacrosCompletions = getClassicMacroCompletions(macros, true);
		attrMacrosCompletions = getAttrMacroCompletions(macros);
	}

	/**
	 * Prepares lists of lookup elements for both classic and attribute standard macros.
	 */
	private void initStandardModifiersCompletions() {
		Map<String, LatteModifier> modifiers = LatteConfiguration.INSTANCE.getStandardModifiers();
		classicModifiersCompletions = getClassicModifierCompletions(modifiers);
	}

	private void attachContentTypes(@NotNull CompletionResultSet result) {
		for (String contentType : LatteMimeTypes.getDefaultMimeTypes()) {
			result.addElement(LookupElementBuilder.create(contentType));
		}
	}

	private void attachClassicMacrosCompletion(@NotNull Project project, @NotNull CompletionResultSet result, boolean prefix) {
		result.addAllElements(prefix ? classicPrefixedMacrosCompletions : classicMacrosCompletions);
		Map<String, LatteMacro> customMacros = LatteConfiguration.INSTANCE.getCustomMacros(project);
		result.addAllElements(getClassicMacroCompletions(customMacros, prefix));
	}

	/**
	 * Builds list of lookup elements for code completion of classic macros.
	 */
	private List<LookupElement> getClassicMacroCompletions(Map<String, LatteMacro> macros, boolean prefix) {
		List<LookupElement> lookupElements = new ArrayList<LookupElement>(macros.size());
		for (LatteMacro macro : macros.values()) {
			if (macro.type != LatteMacro.Type.ATTR_ONLY && (!prefix || macro.type == LatteMacro.Type.PAIR)) {
				lookupElements.add(createBuilderForMacro(macro, prefix));
			}
		}
		return lookupElements;
	}

	/**
	 * Builds list of lookup elements for code completion of classic macros.
	 */
	private List<LookupElement> getClassicModifierCompletions(Map<String, LatteModifier> modifiers) {
		List<LookupElement> lookupElements = new ArrayList<LookupElement>(modifiers.size());
		for (LatteModifier modifier : modifiers.values()) {
			lookupElements.add(createBuilderWithHelp(modifier));
		}
		return lookupElements;
	}

	private LookupElementBuilder createBuilderWithHelp(LatteModifier modifier) {
		LookupElementBuilder builder = LookupElementBuilder.create(modifier.name);
		if (modifier.description.trim().length() > 0) {
			builder = builder.withTypeText(modifier.description);
		}
		if (modifier.help.trim().length() > 0) {
			builder = builder.withTailText(modifier.help);
		}
		return builder.withIcon(LatteIcons.MODIFIER);
	}

	private LookupElementBuilder createBuilderForMacro(LatteMacro macro, boolean prefix) {
		LookupElementBuilder builder = LookupElementBuilder.create((prefix ? "/" : "") + macro.name);
		builder = builder.withIcon(LatteIcons.MACRO);
		builder = builder.withInsertHandler(MacroInsertHandler.getInstance());
		return builder.withTypeText(macro.type.toString(), true);
	}

	private LookupElementBuilder createBuilderForTag(String name) {
		LookupElementBuilder builder = LookupElementBuilder.create(name);
		builder = builder.withInsertHandler(AttrMacroInsertHandler.getInstance());
		return builder.withIcon(LatteIcons.N_TAG);
	}

	/**
	 * Builds list of lookup elements for code completion of attribute macros.
	 */
	private List<LookupElement> getAttrMacroCompletions(Map<String, LatteMacro> macros) {
		List<LookupElement> lookupElements = new ArrayList<LookupElement>(macros.size());
		for (LatteMacro macro : macros.values()) {
			if (macro.type != LatteMacro.Type.UNPAIRED) {
				lookupElements.add(createBuilderForTag("n:" + macro.name));
				if (macro.type == LatteMacro.Type.PAIR || macro.type == LatteMacro.Type.AUTO_EMPTY) {
					lookupElements.add(createBuilderForTag("n:tag-" + macro.name));
					lookupElements.add(createBuilderForTag("n:inner-" + macro.name));
				}
			}
		}
		return lookupElements;
	}
}
