package com.jantvrdik.intellij.latte.intentions;

import com.jantvrdik.intellij.latte.config.LatteMacro;
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
	public LatteMacro.Type getMacroType() {
		return LatteMacro.Type.PAIR;
	}

	@NotNull
	@Override
	public String getText() {
		return "Add custom pair macro {" + macro.getMacroName() + "}...{/" + macro.getMacroName() + "} and n:" + macro.getMacroName();
	}
}
