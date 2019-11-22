package com.jantvrdik.intellij.latte.intentions;

import org.jetbrains.annotations.NotNull;

/**
 * Registers custom nullable variable which can be used only as an classic defined variable.
 */
public class AddCustomNullableVariable extends AddCustomVariable {

	public AddCustomNullableVariable(String variableName) {
		super(variableName);
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@NotNull
	@Override
	public String getText() {
		return "Add custom nullable variable " + defaultVariable.name;
	}
}
