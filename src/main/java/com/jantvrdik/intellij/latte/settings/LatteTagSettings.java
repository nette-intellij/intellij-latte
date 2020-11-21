package com.jantvrdik.intellij.latte.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.*;

public class LatteTagSettings extends BaseLatteSettings implements Serializable {

	private String macroName;
	private String macroType;
	private String arguments = "";
	private boolean allowedModifiers;
	private boolean multiLine;
	private boolean deprecated;
	private String deprecatedMessage;
	private List<LatteArgumentSettings> argumentSettings = new ArrayList<>();
	public LatteTagSettings() {
		super();
	}

	public LatteTagSettings(String macroName, Type macroType) {
		this(macroName, macroType, false, "", false, LatteConfiguration.Vendor.OTHER, "", Collections.emptyList(), "");
	}

	public LatteTagSettings(String macroName, Type macroType, boolean allowedFilters, String arguments, boolean multiLine, String deprecatedMessage, List<LatteArgumentSettings> argumentSettings) {
		this(macroName, macroType, allowedFilters, arguments, multiLine, LatteConfiguration.Vendor.OTHER, "", argumentSettings, deprecatedMessage);
	}

	private LatteTagSettings(
			String macroName,
			Type macroType,
			boolean allowedModifiers,
			String arguments,
			boolean multiLine,
			LatteConfiguration.Vendor vendor,
			String vendorName,
			List<LatteArgumentSettings> argumentSettings,
			String deprecatedMessage
	) {
		super(vendor, vendorName);
		this.macroName = macroName;
		this.macroType = macroType.toString();
		this.allowedModifiers = allowedModifiers;
		this.arguments = arguments;
		this.multiLine = multiLine;
		this.argumentSettings = argumentSettings;
		this.deprecated = deprecatedMessage.length() > 0;
		this.deprecatedMessage = deprecatedMessage;
	}

	public LatteTagSettings setVendor(LatteConfiguration.Vendor vendor) {
		super.setVendor(vendor);
		return this;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
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

	public void setArgumentSettings(List<LatteArgumentSettings> argumentSettings) {
		this.argumentSettings = argumentSettings;
	}

	@Attribute("MacroName")
	public String getMacroName() {
		return macroName;
	}

	@Attribute("MacroType")
	public String getMacroType() {
		return macroType == null ? Type.UNPAIRED.toString() : macroType;
	}

	@Attribute("Arguments")
	public String getArguments() {
		return arguments;
	}

	@Attribute("AllowedModifiers")
	public boolean isAllowedModifiers() {
		return allowedModifiers;
	}

	@Attribute("MultiLine")
	public boolean isMultiLine() {
		return multiLine;
	}

	@Attribute("Deprecated")
	public boolean isDeprecated() {
		return deprecated;
	}

	public boolean isTagBlock() {
		return getMacroName().equals("block");
	}

	@Attribute("DeprecatedMessage")
	public String getDeprecatedMessage() {
		return deprecatedMessage;
	}

	@Attribute("ArgumentSettings")
	public List<LatteArgumentSettings> getArgumentSettings() {
		return argumentSettings;
	}

	public String getArgumentsInfo() {
		String args = arguments.trim();
		if (args.length() > 0) {
			return args;
		}
		if (argumentSettings.size() > 0) {
			List<String> builder = new ArrayList<>();
			for (LatteArgumentSettings argumentSettings : argumentSettings) {
				builder.add(argumentSettings.toReadableString());
			}
			return String.join(" ", builder);
		}
		return "";
	}

	public boolean hasParameters() {
		return (arguments != null && arguments.length() > 0) || argumentSettings.size() > 0;
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

		/** macro is available only as unpaired classic macro, e.g. 'var' or 'link' and as attribute macro without any prefix, e.g. 'n:href' or 'n:class' */
		UNPAIRED_ATTR,

		/** macro is available as pair or unpaired classic macro, e.g. 'block' or 'label' */
		AUTO_EMPTY;

		final public static EnumSet<Type> unpairedSet = java.util.EnumSet.of(UNPAIRED, UNPAIRED_ATTR);
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
				.append(this.arguments)
				.append(this.allowedModifiers)
				.append(this.multiLine)
				.append(this.deprecated)
				.append(this.deprecatedMessage)
				.append(this.argumentSettings)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof LatteTagSettings &&
				Objects.equals(((LatteTagSettings) obj).getMacroName(), this.getMacroName()) &&
				Objects.equals(((LatteTagSettings) obj).getMacroType(), this.getMacroType()) &&
				Objects.equals(((LatteTagSettings) obj).getArguments(), this.getArguments()) &&
				Objects.equals(((LatteTagSettings) obj).isAllowedModifiers(), this.isAllowedModifiers()) &&
				Objects.equals(((LatteTagSettings) obj).isMultiLine(), this.isMultiLine()) &&
				Objects.equals(((LatteTagSettings) obj).isDeprecated(), this.isDeprecated()) &&
				Objects.equals(((LatteTagSettings) obj).getDeprecatedMessage(), this.getDeprecatedMessage()) &&
				Objects.equals(((LatteTagSettings) obj).argumentSettings, this.argumentSettings);
	}

}
