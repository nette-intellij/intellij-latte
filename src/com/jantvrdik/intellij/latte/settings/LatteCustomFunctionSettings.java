package com.jantvrdik.intellij.latte.settings;

import com.intellij.util.xmlb.annotations.Attribute;

public class LatteCustomFunctionSettings {

	private String functionName;
	private String functionReturnType;
	private String functionHelp;
	private String functionDescription;

	public LatteCustomFunctionSettings() {
	}

	public LatteCustomFunctionSettings(String functionName) {
		this(functionName, "mixed", "", "");
	}

	public LatteCustomFunctionSettings(String functionName, String functionReturnType, String functionHelp) {
		this(functionName, functionReturnType, functionHelp, "");
	}

	public LatteCustomFunctionSettings(String functionName, String functionReturnType, String functionHelp, String functionDescription) {
		this.functionName = functionName;
		this.functionReturnType = functionReturnType;
		this.functionHelp = functionHelp;
		this.functionDescription = functionDescription;
	}

	public void setFunctionName(String modifierName) {
		this.functionName = modifierName;
	}

	public void setFunctionReturnType(String modifierReturnType) {
		this.functionReturnType = modifierReturnType;
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
}
