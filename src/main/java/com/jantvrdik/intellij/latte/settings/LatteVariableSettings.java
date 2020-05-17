package com.jantvrdik.intellij.latte.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import com.jantvrdik.intellij.latte.utils.LattePhpType;

public class LatteVariableSettings {

	private String varName;
	private String varType;

	public LatteVariableSettings() {
	}

	public LatteVariableSettings(String varName, String varType) {
		this.varName = varName;
		this.varType = varType;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public void setVarType(String varType) {
		this.varType = varType;
	}

	public LattePhpType toPhpType() {
		return LattePhpType.create(varName, varType);
	}

	@Attribute("VarName")
	public String getVarName() {
		return varName;
	}

	@Attribute("VarType")
	public String getVarType() {
		return varType;
	}

}
