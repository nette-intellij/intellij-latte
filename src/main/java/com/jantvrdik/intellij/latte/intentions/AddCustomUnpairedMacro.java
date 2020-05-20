package com.jantvrdik.intellij.latte.intentions;

import com.jantvrdik.intellij.latte.settings.LatteCustomMacroSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Registers custom macro which can be used only as an classic unpaired macro.
 */
public class AddCustomUnpairedMacro extends AddCustomMacro {

	public AddCustomUnpairedMacro(String macroName) {
		super(macroName);
	}

	@NotNull
	@Override
	public LatteCustomMacroSettings.Type getMacroType() {
		return LatteCustomMacroSettings.Type.UNPAIRED;
	}

	@NotNull
	@Override
	public String getText() {
		return "Add custom unpaired tag {" + macro.getMacroName() + "}";
	}
}
