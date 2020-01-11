package com.jantvrdik.intellij.latte.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.jantvrdik.intellij.latte.config.LatteMacro;

public class LatteCustomMacroSettings {

	private String macroName;
	private String macroType;
	private boolean allowedModifiers;
	private boolean hasParameters;

	public LatteCustomMacroSettings() {
	}

	public LatteCustomMacroSettings(String macroName, LatteMacro.Type macroType) {
		this(macroName, macroType, true, true);
	}

	public LatteCustomMacroSettings(String macroName, LatteMacro.Type macroType, boolean allowedModifiers, boolean hasParameters) {
		this.macroName = macroName;
		this.macroType = macroType.toString();
		this.allowedModifiers = allowedModifiers;
		this.hasParameters = hasParameters;
	}

	public void setMacroName(String macroName) {
		this.macroName = macroName;
	}

	public void setMacroType(String macroType) {
		this.macroType = macroType;
	}

	public void setAllowedModifiers(boolean allowedModifiers) {
		this.allowedModifiers = allowedModifiers;
	}

	public void setHasParameters(boolean hasParameters) {
		this.hasParameters = hasParameters;
	}

	@Attribute("MacroName")
	public String getMacroName() {
		return macroName;
	}

	@Attribute("MacroType")
	public String getMacroType() {
		return macroType == null ? LatteMacro.Type.UNPAIRED.toString() : macroType;
	}

	@Attribute("AllowedModifiers")
	public boolean isAllowedModifiers() {
		return allowedModifiers;
	}

	@Attribute("HasParameters")
	public boolean hasParameters() {
		return hasParameters;
	}

	public LatteMacro.Type getType() {
		return LatteMacro.Type.valueOf(getMacroType());
	}

}
