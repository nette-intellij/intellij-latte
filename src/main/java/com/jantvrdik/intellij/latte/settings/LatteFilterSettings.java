package com.jantvrdik.intellij.latte.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;

import java.io.Serializable;
import java.util.Objects;

public class LatteFilterSettings extends BaseLatteSettings implements Serializable {

	private String modifierName;
	private String modifierDescription;
	private String modifierHelp;
	private String modifierInsert;

	public LatteFilterSettings() {
		super();
	}

	public LatteFilterSettings(String modifierName) {
		this(modifierName, "", "", "");
	}

	public LatteFilterSettings(String modifierName, String description) {
		this(modifierName, description, "", "");
	}

	public LatteFilterSettings(String modifierName, String description, String help) {
		this(modifierName, description, help, "");
	}

	public LatteFilterSettings(String modifierName, String description, String modifierHelp, String insertColons) {
		this(modifierName, description, modifierHelp, insertColons, LatteConfiguration.Vendor.OTHER, "");
	}

	public LatteFilterSettings(
			String modifierName,
			String modifierDescription,
			String modifierHelp,
			String modifierInsert,
			LatteConfiguration.Vendor vendor,
			String vendorName
	) {
		super(vendor, vendorName);
		this.modifierName = modifierName;
		this.modifierDescription = modifierDescription;
		this.modifierHelp = modifierHelp;
		this.modifierInsert = modifierInsert;
	}

	@Override
	public LatteFilterSettings setVendor(LatteConfiguration.Vendor vendor) {
		super.setVendor(vendor);
		return this;
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

	public void setModifierInsert(String modifierInsert) {
		this.modifierInsert = modifierInsert;
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

	@Attribute("ModifierDoubleInsert")
	public String getModifierInsert() {
		return modifierInsert;
	}

	@Override
	public int hashCode() {
		return Objects.hash(modifierName, modifierHelp, modifierDescription, modifierInsert);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof LatteFilterSettings &&
				Objects.equals(((LatteFilterSettings) obj).getModifierName(), this.getModifierName()) &&
				Objects.equals(((LatteFilterSettings) obj).getModifierHelp(), this.getModifierHelp()) &&
				Objects.equals(((LatteFilterSettings) obj).getModifierDescription(), this.getModifierDescription()) &&
				Objects.equals(((LatteFilterSettings) obj).getModifierInsert(), this.getModifierInsert());
	}

}
