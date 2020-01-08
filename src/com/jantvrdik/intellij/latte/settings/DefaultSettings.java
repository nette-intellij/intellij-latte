package com.jantvrdik.intellij.latte.settings;

public class DefaultSettings {

	public static LatteVariableSettings[] defaultVariables = new LatteVariableSettings[]{
			new LatteVariableSettings("control", "\\Nette\\Application\\UI\\Control", false),
			new LatteVariableSettings("basePath", "string", false),
			new LatteVariableSettings("baseUrl", "string", false),
			new LatteVariableSettings("baseUri", "string", false),
			new LatteVariableSettings("flashes", "mixed[]", false),
			new LatteVariableSettings("presenter", "\\Nette\\Application\\UI\\Presenter", false),
			new LatteVariableSettings("iterator", "\\Latte\\Runtime\\CachingIterator", false),
			new LatteVariableSettings("form", "\\Nette\\Application\\UI\\Form", false),
			new LatteVariableSettings("user", "\\Nette\\Security\\User", false),
	};

	public static LatteVariableSettings[] getDefaultVariables() {
		return defaultVariables;
	}
}

