package com.jantvrdik.intellij.latte.settings;

import com.jantvrdik.intellij.latte.config.LatteMacro;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class DefaultSettings {

	public static LatteVariableSettings[] defaultVariables = new LatteVariableSettings[]{
		new LatteVariableSettings("control", "\\Nette\\Application\\UI\\Control"),
		new LatteVariableSettings("basePath", "string"),
		new LatteVariableSettings("baseUrl", "string"),
		new LatteVariableSettings("baseUri", "string"),
		new LatteVariableSettings("flashes", "mixed[]"),
		new LatteVariableSettings("presenter", "\\Nette\\Application\\UI\\Presenter"),
		new LatteVariableSettings("iterator", "\\Latte\\Runtime\\CachingIterator"),
		new LatteVariableSettings("form", "\\Nette\\Application\\UI\\Form"),
		new LatteVariableSettings("user", "\\Nette\\Security\\User"),
	};

	public static LatteCustomMacroSettings[] defaultMacros = new LatteCustomMacroSettings[]{
		new LatteCustomMacroSettings("href", LatteMacro.Type.ATTR_ONLY),
		new LatteCustomMacroSettings("link", LatteMacro.Type.UNPAIRED),
		new LatteCustomMacroSettings("plink", LatteMacro.Type.UNPAIRED),
		new LatteCustomMacroSettings("control", LatteMacro.Type.UNPAIRED),
		new LatteCustomMacroSettings("snippet", LatteMacro.Type.PAIR),
		new LatteCustomMacroSettings("snippetArea", LatteMacro.Type.PAIR),
		new LatteCustomMacroSettings("form", LatteMacro.Type.PAIR, false, true),
		new LatteCustomMacroSettings("formContainer", LatteMacro.Type.PAIR, false, true),
		new LatteCustomMacroSettings("label", LatteMacro.Type.AUTO_EMPTY, false, true),
		new LatteCustomMacroSettings("input", LatteMacro.Type.UNPAIRED, false, true),
		new LatteCustomMacroSettings("name", LatteMacro.Type.ATTR_ONLY),
		new LatteCustomMacroSettings("inputError", LatteMacro.Type.UNPAIRED, false, true),
		new LatteCustomMacroSettings("_", LatteMacro.Type.PAIR),
		new LatteCustomMacroSettings("dump", LatteMacro.Type.UNPAIRED, false, true),
		new LatteCustomMacroSettings("cache", LatteMacro.Type.PAIR),
	};

	public static LatteCustomFunctionSettings[] defaultCustomFunctions = new LatteCustomFunctionSettings[]{
		new LatteCustomFunctionSettings("isLinkCurrent", "bool", "(string $destination = null, $args = [])"),
		new LatteCustomFunctionSettings("isModuleCurrent", "bool", "(string $moduleName)"),
	};

	public static LatteVariableSettings[] getDefaultVariables() {
		return defaultVariables;
	}

	public static boolean isDefaultVariable(String variableName) {
		final String normalizedName = LattePhpUtil.normalizePhpVariable(variableName);
		return Arrays.stream(defaultVariables).anyMatch(variable -> variable.getVarName().equals(normalizedName));
	}

	@Nullable
	public static LatteVariableSettings getDefaultVariable(String variableName) {
		final String normalizedName = LattePhpUtil.normalizePhpVariable(variableName);
		return Arrays.stream(defaultVariables).filter(variable -> variable.getVarName().equals(normalizedName))
				.findFirst()
				.orElse(null);
	}

	public static LatteCustomMacroSettings[] getDefaultMacros() {
		return defaultMacros;
	}

	public static boolean isDefaultMacro(String macroName) {
		final String normalizedName = LatteUtil.normalizeNAttrNameModifier(macroName);
		return Arrays.stream(defaultMacros).anyMatch(macro -> macro.getMacroName().equals(normalizedName));
	}

	@Nullable
	public static LatteCustomMacroSettings getDefaultMacro(String macroName) {
		final String normalizedName = LatteUtil.normalizeNAttrNameModifier(macroName);
		return Arrays.stream(defaultMacros).filter(macro -> macro.getMacroName().equals(normalizedName))
				.findFirst()
				.orElse(null);
	}

	public static LatteCustomFunctionSettings[] getDefaultCustomFunctions() {
		return defaultCustomFunctions;
	}

	public static boolean isDefaultFunction(String macroName) {
		final String normalizedName = LatteUtil.normalizeNAttrNameModifier(macroName);
		return Arrays.stream(defaultCustomFunctions).anyMatch(function -> function.getFunctionName().equals(normalizedName));
	}

	@Nullable
	public static LatteCustomFunctionSettings getDefaultFunction(String macroName) {
		final String normalizedName = LatteUtil.normalizeNAttrNameModifier(macroName);
		return Arrays.stream(defaultCustomFunctions).filter(function -> function.getFunctionName().equals(normalizedName))
				.findFirst()
				.orElse(null);
	}
}

