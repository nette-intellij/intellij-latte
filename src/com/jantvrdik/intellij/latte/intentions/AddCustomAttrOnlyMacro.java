package com.jantvrdik.intellij.latte.intentions;

import com.jantvrdik.intellij.latte.config.LatteMacro;
import org.jetbrains.annotations.NotNull;

/**
 * Registers custom macro which can be used only as an attribute macro.
 */
public class AddCustomAttrOnlyMacro extends AddCustomMacro {

	public AddCustomAttrOnlyMacro(String macroName) {
		super(macroName);
	}

	@NotNull
	@Override
	public LatteMacro.Type getMacroType() {
		return LatteMacro.Type.ATTR_ONLY;
	}

	@NotNull
	@Override
	public String getText() {
		return "Add custom unpaired attribute macro n:" + macro.getMacroName();
	}
}
