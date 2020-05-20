package com.jantvrdik.intellij.latte.intentions;

import com.jantvrdik.intellij.latte.settings.LatteCustomMacroSettings;
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
	public LatteCustomMacroSettings.Type getMacroType() {
		return LatteCustomMacroSettings.Type.ATTR_ONLY;
	}

	@NotNull
	@Override
	public String getText() {
		return "Add custom unpaired attribute tag n:" + macro.getMacroName();
	}
}
