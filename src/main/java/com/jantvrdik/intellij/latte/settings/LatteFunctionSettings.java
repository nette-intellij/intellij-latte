package com.jantvrdik.intellij.latte.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;

import java.io.Serializable;
import java.util.Objects;

public class LatteFunctionSettings extends BaseLatteSettings implements Serializable {

	private String functionName;
	private String functionReturnType;
	private String functionHelp;
	private String functionDescription;

	public LatteFunctionSettings() {
		super();
	}

	public LatteFunctionSettings(String functionName) {
		this(functionName, "mixed", "", "");
	}

	public LatteFunctionSettings(String functionName, String functionReturnType, String functionHelp) {
		this(functionName, functionReturnType, functionHelp, "");
	}

	public LatteFunctionSettings(String functionName, String functionReturnType, String functionHelp, String functionDescription) {
		this(functionName, functionReturnType, functionHelp, functionDescription, LatteConfiguration.Vendor.OTHER, "");
	}

	public LatteFunctionSettings(
			String functionName,
			String functionReturnType,
			String functionHelp,
			String functionDescription,
			LatteConfiguration.Vendor vendor,
			String vendorName
	) {
		super(vendor, vendorName);
		this.functionName = functionName;
		this.functionReturnType = functionReturnType;
		this.functionHelp = functionHelp;
		this.functionDescription = functionDescription;
	}

	public LatteFunctionSettings setVendor(LatteConfiguration.Vendor vendor) {
		super.setVendor(vendor);
		return this;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public void setFunctionReturnType(String returnType) {
		this.functionReturnType = returnType;
	}

	public void setFunctionHelp(String functionHelp) {
		this.functionHelp = functionHelp;
	}

	public void setFunctionDescription(String functionDescription) {
		this.functionDescription = functionDescription;
	}

	@Attribute("FunctionName")
	public String getFunctionName() {
		return functionName;
	}

	@Attribute("FunctionReturnType")
	public String getFunctionReturnType() {
		return functionReturnType;
	}

	@Attribute("FunctionHelp")
	public String getFunctionHelp() {
		return functionHelp;
	}

	@Attribute("FunctionDescription")
	public String getFunctionDescription() {
		return functionDescription;
	}

	@Override
	public int hashCode() {
		return Objects.hash(functionName, functionReturnType, functionHelp, functionDescription);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof LatteFunctionSettings &&
				Objects.equals(((LatteFunctionSettings) obj).getFunctionName(), this.getFunctionName()) &&
				Objects.equals(((LatteFunctionSettings) obj).getFunctionReturnType(), this.getFunctionReturnType()) &&
				Objects.equals(((LatteFunctionSettings) obj).getFunctionDescription(), this.getFunctionDescription()) &&
				Objects.equals(((LatteFunctionSettings) obj).getFunctionHelp(), this.getFunctionHelp());
	}
}
