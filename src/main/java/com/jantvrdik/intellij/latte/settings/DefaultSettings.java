package com.jantvrdik.intellij.latte.settings;

import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class DefaultSettings {

	public static LatteVariableSettings[] defaultVariables = LatteConfiguration.standardNetteVariables.values().toArray(new LatteVariableSettings[0]);

	public static LatteTagSettings[] defaultTags = LatteConfiguration.standardNetteTags.values().toArray(new LatteTagSettings[0]);

	public static LatteFunctionSettings[] defaultCustomFunctions = LatteConfiguration.standardNetteFunctions.values().toArray(new LatteFunctionSettings[0]);

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

	public static LatteTagSettings[] getDefaultTags() {
		return defaultTags;
	}

	public static boolean isDefaultMacro(String macroName) {
		final String normalizedName = LatteUtil.normalizeNAttrNameModifier(macroName);
		return Arrays.stream(defaultTags).anyMatch(macro -> macro.getMacroName().equals(normalizedName));
	}

	@Nullable
	public static LatteTagSettings getDefaultTag(String macroName) {
		final String normalizedName = LatteUtil.normalizeNAttrNameModifier(macroName);
		return Arrays.stream(defaultTags).filter(macro -> macro.getMacroName().equals(normalizedName))
				.findFirst()
				.orElse(null);
	}

	public static LatteFunctionSettings[] getDefaultCustomFunctions() {
		return defaultCustomFunctions;
	}

	public static boolean isDefaultFunction(String macroName) {
		final String normalizedName = LatteUtil.normalizeNAttrNameModifier(macroName);
		return Arrays.stream(defaultCustomFunctions).anyMatch(function -> function.getFunctionName().equals(normalizedName));
	}

	@Nullable
	public static LatteFunctionSettings getDefaultFunction(String macroName) {
		final String normalizedName = LatteUtil.normalizeNAttrNameModifier(macroName);
		return Arrays.stream(defaultCustomFunctions).filter(function -> function.getFunctionName().equals(normalizedName))
				.findFirst()
				.orElse(null);
	}
}

