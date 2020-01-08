package com.jantvrdik.intellij.latte.settings;

import com.intellij.util.xmlb.annotations.Attribute;

public class LatteCustomModifierSettings {

	private String modifierName;
	private String modifierDescription;
	private String modifierHelp;

	public LatteCustomModifierSettings() {
	}

	public LatteCustomModifierSettings(String modifierName) {
		this(modifierName, "", "");
	}

	public LatteCustomModifierSettings(String modifierName, String modifierDescription, String modifierHelp) {
		this.modifierName = modifierName;
		this.modifierDescription = modifierDescription;
		this.modifierHelp = modifierHelp;
	}

	public void setModifierName(String modifierName) {
		this.modifierName = modifierName;
	}

	public void setModifierDescription(String modifierDescription) {
		this.modifierDescription = modifierDescription;
	}

	public void setModifierHelp(String modifierHelp) {
		this.modifierHelp = modifierHelp;
	}

	@Attribute("ModifierName")
	public String getModifierName() {
		return modifierName;
	}

	@Attribute("ModifierDescription")
	public String getModifierDescription() {
		return modifierDescription;
	}

	@Attribute("ModifierHelp")
	public String getModifierHelp() {
		return modifierHelp;
	}
}
