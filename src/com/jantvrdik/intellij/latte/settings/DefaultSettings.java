package com.jantvrdik.intellij.latte.settings;

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

	public static LatteCustomFunctionSettings[] defaultCustomFunctions = new LatteCustomFunctionSettings[]{
			new LatteCustomFunctionSettings("isLinkCurrent", "bool", "(string $destination = null, $args = [])"),
			new LatteCustomFunctionSettings("isModuleCurrent", "bool", "(string $moduleName)"),
	};

	public static LatteVariableSettings[] getDefaultVariables() {
		return defaultVariables;
	}

	public static LatteCustomFunctionSettings[] getDefaultCustomFunctions() {
		return defaultCustomFunctions;
	}
}

