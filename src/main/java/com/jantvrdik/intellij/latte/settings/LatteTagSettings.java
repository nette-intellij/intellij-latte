package com.jantvrdik.intellij.latte.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Objects;

public class LatteTagSettings extends BaseLatteSettings implements Serializable {

	private String macroName;
	private String macroType;
	private boolean allowedModifiers;
	private boolean hasParameters;
	private boolean multiLine;
	private boolean deprecated;
	private String deprecatedMessage;
	public LatteTagSettings() {
		super();
	}

	public LatteTagSettings(String macroName, Type macroType) {
		this(macroName, macroType, true, true);
	}

	public LatteTagSettings(String macroName, Type macroType, boolean allowedFilters, boolean hasParameters) {
		this(macroName, macroType, allowedFilters, hasParameters, false);
	}

	public LatteTagSettings(String macroName, Type macroType, boolean allowedModifiers, boolean hasParameters, boolean multiLine) {
		this(macroName, macroType, allowedModifiers, hasParameters, multiLine, LatteConfiguration.Vendor.OTHER, "", "");
	}

	public LatteTagSettings(String macroName, Type macroType, boolean allowedModifiers, boolean hasParameters, boolean multiLine, String deprecatedMessage) {
		this(macroName, macroType, allowedModifiers, hasParameters, multiLine, LatteConfiguration.Vendor.OTHER, "", "");
	}

	private LatteTagSettings(
			String macroName,
			Type macroType,
			boolean allowedModifiers,
			boolean hasParameters,
			boolean multiLine,
			LatteConfiguration.Vendor vendor,
			String vendorName,
			String deprecatedMessage
	) {
		super(vendor, vendorName);
		this.macroName = macroName;
		this.macroType = macroType.toString();
		this.allowedModifiers = allowedModifiers;
		this.hasParameters = hasParameters;
		this.multiLine = multiLine;
		this.deprecated = deprecatedMessage.length() > 0;
		this.deprecatedMessage = deprecatedMessage;
	}

	public LatteTagSettings setVendor(LatteConfiguration.Vendor vendor) {
		super.setVendor(vendor);
		return this;
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(this.macroName)
				.append(this.macroType)
				.append(this.allowedModifiers)
				.append(this.hasParameters)
				.append(this.multiLine)
				.append(this.deprecated)
				.append(this.deprecatedMessage)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof LatteTagSettings &&
				Objects.equals(((LatteTagSettings) obj).getMacroName(), this.getMacroName()) &&
				Objects.equals(((LatteTagSettings) obj).getMacroType(), this.getMacroType()) &&
				Objects.equals(((LatteTagSettings) obj).isAllowedModifiers(), this.isAllowedModifiers()) &&
				Objects.equals(((LatteTagSettings) obj).hasParameters(), this.hasParameters()) &&
				Objects.equals(((LatteTagSettings) obj).isMultiLine(), this.isMultiLine()) &&
				Objects.equals(((LatteTagSettings) obj).isDeprecated(), this.isDeprecated()) &&
				Objects.equals(((LatteTagSettings) obj).getDeprecatedMessage(), this.getDeprecatedMessage());
	}

}
