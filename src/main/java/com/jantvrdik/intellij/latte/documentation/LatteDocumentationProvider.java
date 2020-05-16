package com.jantvrdik.intellij.latte.documentation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteModifier;
import com.jantvrdik.intellij.latte.psi.LatteMacroModifier;
import com.jantvrdik.intellij.latte.psi.LattePhpMethod;
import com.jantvrdik.intellij.latte.settings.LatteCustomFunctionSettings;
import org.jetbrains.annotations.Nullable;

public class LatteDocumentationProvider extends AbstractDocumentationProvider {

	@Override
	public @Nullable String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
		if (element instanceof LatteMacroModifier) {
			LatteModifier modifier = ((LatteMacroModifier) element).getMacroModifier();
			if (modifier == null) {
				return null;
			}
			return createHelpWithDescription(modifier.help, modifier.description);

		} else if (element instanceof LattePhpMethod) {
			if (!((LattePhpMethod) element).isFunction()) {
				return null;
			}

			LatteCustomFunctionSettings customFunction = LatteConfiguration.INSTANCE.getFunction(
					element.getProject(),
					((LattePhpMethod) element).getMethodName()
			);
			if (customFunction == null) {
				return null;
			}

			if (customFunction.getFunctionHelp().length() == 0 && customFunction.getFunctionDescription().length() == 0) {
				return "Custom defined Latte function (can be changed in project settings)";
			}

			return createHelpWithDescription(customFunction.getFunctionHelp(), customFunction.getFunctionDescription());
		}
		return null;
	}

	private String createHelpWithDescription(String help, String description) {
		String helpText = help.trim();
		boolean hasDescription = description.trim().length() > 0;
		if (helpText.length() > 0 && hasDescription) {
			helpText += " - ";
		}
		if (hasDescription) {
			helpText += "\"" + description + "\"";
		}
		return helpText;
	}
}
