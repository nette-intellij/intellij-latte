package com.jantvrdik.intellij.latte.intentions;

import com.jantvrdik.intellij.latte.settings.LatteCustomMacroSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Registers custom macro which can be used only as both classic pair macro and attribute macro.
 */
public class AddCustomPairMacro extends AddCustomMacro {

	public AddCustomPairMacro(String macroName) {
		super(macroName);
	}

	@NotNull
	@Override
	public LatteCustomMacroSettings.Type getMacroType() {
		return LatteCustomMacroSettings.Type.PAIR;
	}

	@NotNull
	@Override
	public String getText() {
		return "Add custom pair tag {" + macro.getMacroName() + "}...{/" + macro.getMacroName() + "} and n:" + macro.getMacroName();
	}
}
