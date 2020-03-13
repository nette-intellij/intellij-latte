package com.jantvrdik.intellij.latte.config;

import com.jantvrdik.intellij.latte.utils.LattePhpType;

/**
 * Represents a single registered default Latte variable.
 */
public class LatteDefaultVariable {

	/** macro name, e.g. 'foreach' */
	public final String name;

	/** macro type */
	public final LattePhpType type;

	public boolean deprecated = false;

	public String deprecatedMessage;

	public LatteDefaultVariable(String name, LattePhpType type) {
		this.name = name;
		this.type = type;
	}
}
