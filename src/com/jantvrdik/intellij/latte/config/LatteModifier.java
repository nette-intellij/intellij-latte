package com.jantvrdik.intellij.latte.config;

/**
 * Represents a single registered Latte modifier.
 */
public class LatteModifier {

	/** macro modifier, e.g. 'noescape' */
	public final String name;

	/** macro modifier content example. 'truncate (length, append = '…')' */
	public final String help;

	public final String description;

	public LatteModifier(String name, String description, String help) {
		this.name = name;
		this.help = help;
		this.description = description;
	}

}
