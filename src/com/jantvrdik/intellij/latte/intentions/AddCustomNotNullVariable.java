package com.jantvrdik.intellij.latte.intentions;

import org.jetbrains.annotations.NotNull;

/**
 * Registers custom not null variable which can be used only as an classic defined variable.
 */
public class AddCustomNotNullVariable extends AddCustomVariable {

	public AddCustomNotNullVariable(String variableName) {
		super(variableName);
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@NotNull
	@Override
	public String getText() {
		return "Add custom not null variable " + defaultVariable.name;
	}
}
