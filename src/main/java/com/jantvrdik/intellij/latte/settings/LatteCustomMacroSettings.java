package com.jantvrdik.intellij.latte.settings;

import com.intellij.util.xmlb.annotations.Attribute;

public class LatteCustomMacroSettings {

	private String macroName;
	private String macroType;
	private boolean allowedModifiers;
	private boolean hasParameters;
	private boolean multiLine;
	private boolean deprecated;
	private String deprecatedMessage;

	public LatteCustomMacroSettings() {
	}

	public LatteCustomMacroSettings(String macroName, Type macroType) {
		this(macroName, macroType, true, true);
	}

	public LatteCustomMacroSettings(String macroName, Type macroType, boolean allowedModifiers, boolean hasParameters) {
		this(macroName, macroType, allowedModifiers, hasParameters, false);
	}

	public LatteCustomMacroSettings(String macroName, Type macroType, boolean allowedModifiers, boolean hasParameters, boolean multiLine) {
		this.macroName = macroName;
		this.macroType = macroType.toString();
		this.allowedModifiers = allowedModifiers;
		this.hasParameters = hasParameters;
		this.multiLine = multiLine;
		this.deprecated = false;
		this.deprecatedMessage = "";
	}

	public void setMacroName(String macroName) {
		this.macroName = macroName;
	}

	public void setMacroType(String macroType) {
		this.macroType = macroType;
	}

	public void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}

	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}

	public void setDeprecatedMessage(String deprecatedMessage) {
		this.deprecatedMessage = deprecatedMessage;
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
		return macroType == null ? Type.UNPAIRED.toString() : macroType;
	}

	@Attribute("AllowedModifiers")
	public boolean isAllowedModifiers() {
		return allowedModifiers;
	}

	@Attribute("MultiLine")
	public boolean isMultiLine() {
		return multiLine;
	}

	@Attribute("HasParameters")
	public boolean isHasParameters() {
		return hasParameters();
	}

	@Attribute("Deprecated")
	public boolean isDeprecated() {
		return deprecated;
	}

	@Attribute("DeprecatedMessage")
	public String getDeprecatedMessage() {
		return deprecatedMessage;
	}

	public boolean hasParameters() {
		return hasParameters;
	}

	public Type getType() {
		return Type.valueOf(getMacroType());
	}

	public enum Type {
		/** macro is available only as attribute macro without any prefix, e.g. 'n:href' or 'n:class' */
		ATTR_ONLY,

		/** macro is available as pair classic macro or attribute macro, possible prefixed with 'inner-' or 'tag', e.g. 'foreach' */
		PAIR,

		/** macro is available only as unpaired classic macro, e.g. 'var' or 'link' */
		UNPAIRED,

		AUTO_EMPTY,
	}

	public static boolean isValidType(String type) {

		for (Type c : Type.values()) {
			if (c.name().equals(type)) {
				return true;
			}
		}
		return false;
	}

}
