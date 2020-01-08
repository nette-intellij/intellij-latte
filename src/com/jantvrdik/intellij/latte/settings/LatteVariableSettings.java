package com.jantvrdik.intellij.latte.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.jantvrdik.intellij.latte.utils.LattePhpType;

public class LatteVariableSettings {

	private String varName;
	private String varType;
	private boolean nullable;

	public LatteVariableSettings() {
	}

	public LatteVariableSettings(String varName, String varType, boolean nullable) {
		this.varName = varName;
		this.varType = varType;
		this.nullable = nullable;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public void setVarType(String varType) {
		this.varType = varType;
	}

	public LatteVariableSettings setNullable(boolean nullable) {
		this.nullable = nullable;
		return this;
	}

	public LattePhpType toPhpType() {
		return new LattePhpType(varName, varType, nullable);
	}

	@Attribute("VarName")
	public String getVarName() {
		return varName;
	}

	@Attribute("VarType")
	public String getVarType() {
		return varType;
	}

	@Attribute("IsNullable")
	public boolean isNullable() {
		return nullable;
	}

}
