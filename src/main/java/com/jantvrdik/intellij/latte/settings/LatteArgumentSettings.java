package com.jantvrdik.intellij.latte.settings;

import com.intellij.util.xmlb.annotations.Attribute;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LatteArgumentSettings implements Serializable {

	private String name;
	private Type[] types;
	private String validType;
	private boolean required;
	private boolean repeatable;

	public LatteArgumentSettings() {
		super();
	}

	public LatteArgumentSettings(String name, Type[] types, String validType, boolean required, boolean repeatable) {
		this.name = name;
		this.types = types;
		this.validType = validType;
		this.required = required;
		this.repeatable = repeatable;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTypes(String types) {
		this.types = getTypes(types);
	}

	@Attribute("Name")
	public String getName() {
		return name;
	}

	@Attribute("Types")
	public String getTypes() {
		StringBuilder stringBuilder = new StringBuilder();
		for (Type type : types) {
			stringBuilder.append(type.toString());
		}
		return stringBuilder.toString();
	}

	@Attribute("ValidType")
	public String getValidType() {
		return validType;
	}

	@Attribute("Required")
	public boolean isRequired() {
		return required;
	}

	@Attribute("Repeatable")
	public boolean isRepeatable() {
		return repeatable;
	}

	public Type[] getArgumentTypes() {
		return types;
	}

	public enum Type {
		/** match with foo, bar, foo_123, ... */
		PHP_IDENTIFIER,

		/** match with $var, foo(), \Bar::, ... (-> | :: property|method|constant) */
		PHP_EXPRESSION,

		/** match with $var and mark it as definition */
		VARIABLE_DEFINITION,

		/** match with [$foo, , $bar] and mark all variables as definition */
		VARIABLE_DEFINITION_LIST,

		/** match with [$foo, , $bar] and mark all variables as definition */
		BLOCK,

		/** match with , var => value, … */
		KEY_VALUE,
	}

	@Nullable
	public static Type[] getTypes(@Nullable String type) {
		if (type == null) {
			return null;
		}

		List<Type> out = new ArrayList<>();
		for (String currentType : type.split(",")) {
			currentType = currentType.trim();
			boolean valid = false;
			for (Type c : Type.values()) {
				if (c.name().equals(currentType)) {
					out.add(Type.valueOf(currentType.trim()));
					valid = true;
					break;
				}
			}

			if (!valid) {
				return null;
			}
		}
		return out.toArray(new Type[0]);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(this.name)
				.append(this.getTypes())
				.append(this.required)
				.append(this.validType)
				.append(this.repeatable)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof LatteArgumentSettings &&
				Objects.equals(((LatteArgumentSettings) obj).name, this.name) &&
				Objects.equals(((LatteArgumentSettings) obj).getTypes(), this.getTypes()) &&
				Objects.equals(((LatteArgumentSettings) obj).required, this.required) &&
				Objects.equals(((LatteArgumentSettings) obj).validType, this.validType) &&
				Objects.equals(((LatteArgumentSettings) obj).repeatable, this.repeatable);
	}

}
