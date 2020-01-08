package com.jantvrdik.intellij.latte.intentions;

import com.jantvrdik.intellij.latte.config.LatteMacro;
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
	public LatteMacro.Type getMacroType() {
		return LatteMacro.Type.UNPAIRED;
	}

	@NotNull
	@Override
	public String getText() {
		return "Add custom unpaired macro {" + macro.getMacroName() + "}";
	}
}
