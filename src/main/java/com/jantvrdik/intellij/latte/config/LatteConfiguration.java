package com.jantvrdik.intellij.latte.config;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.jantvrdik.intellij.latte.php.LattePhpVariableUtil;
import com.jantvrdik.intellij.latte.settings.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

public class LatteConfiguration {

	public static String LATTE_HELP_URL = "https://latte.nette.org/";
	public static String FORUM_URL = "https://forum.nette.org/";

	public enum Vendor {
		OTHER("Other", (JBColor) JBColor.GREEN.darker()),
		NETTE_APPLICATION("nette/application", JBColor.BLUE),
		NETTE_FORMS("nette/forms", JBColor.BLUE),
		LATTE("Latte", JBColor.ORANGE),
		CUSTOM("Custom", JBColor.GRAY);

		private final String name;

		private final JBColor color;

		Vendor(String name, JBColor color) {
			this.name = name;
			this.color = color;
		}

		public String getName() {
			return name;
		}

		public JBColor getColor() {
			return color;
		}
	}

	private static final Map<Project, LatteConfiguration> instances = new HashMap<>();

	@NotNull
	private final Project project;

	public LatteConfiguration(@NotNull Project project) {
		this.project = project;
	}

	public static LatteConfiguration getInstance(@NotNull Project project) {
		if (!instances.containsKey(project)) {
			instances.put(project, new LatteConfiguration(project));
		}
		return instances.get(project);
	}

	/**
	 * @return tag with given name or null tag is not available
	 */
	@Nullable
	public LatteTagSettings getTag(String name) {
		Map<String, LatteTagSettings> projectTags = getTags();
		if (projectTags.containsKey(name)) {
			return projectTags.get(name);
		}
		return null;
	}

	/**
	 * @return filter with given name or null filter is not available
	 */
	@Nullable
	public LatteFilterSettings getFilter(String name) {
		Map<String, LatteFilterSettings> projectFilters = getFilters();
		if (projectFilters.containsKey(name)) {
			return projectFilters.get(name);
		}

		return null;
	}

	@Nullable
	public LatteFunctionSettings getFunction(String name) {
		for (LatteFunctionSettings functionSettings : getFunctions()) {
			if (functionSettings.getFunctionName().equals(name)) {
				return functionSettings;
			}
		}
		return null;
	}

	/**
	 * @return variable with given name
	 */
	@Nullable
	public LatteVariableSettings getVariable(String name) {
		name = LattePhpVariableUtil.normalizePhpVariable(name);
		for (LatteVariableSettings variable : getVariables()) {
			if (variable.getVarName().equals(name)) {
				return variable;
			}
		}
		return null;
	}

	@NotNull
	private LatteSettings getSettings() {
		return LatteSettings.getInstance(project);
	}

	@NotNull
	public Collection<LatteVariableSettings> getVariables() {
		return getVariables(true).values();
	}

	@NotNull
	public Map<String, LatteVariableSettings> getVariables(boolean enableCustom) {
		LatteSettings settings = getSettings();
		Map<String, LatteVariableSettings> variableSettings = new HashMap<>();
		if (enableCustom && settings.enableDefaultVariables && settings.variableSettings != null) {
			for (LatteVariableSettings variableSetting : settings.variableSettings) {
				variableSettings.put(variableSetting.getVarName(), variableSetting);
			}
		}

		for (Vendor vendor : getDefaultConfiguration().getVendors()) {
			if (settings.isEnabledSourceVendor(vendor)) {
				for (LatteVariableSettings variableSetting : getDefaultConfiguration().getVariables(vendor).values()) {
					if (!variableSettings.containsKey(variableSetting.getVarName())) {
						variableSettings.put(variableSetting.getVarName(), variableSetting);
					}
				}
			}
		}
		return Collections.unmodifiableMap(variableSettings);
	}

	@NotNull
	public Collection<LatteFunctionSettings> getFunctions() {
		return getFunctions(true).values();
	}

	@NotNull
	private Map<String, LatteFunctionSettings> getFunctions(boolean enableCustom) {
		LatteSettings settings = getSettings();

		Map<String, LatteFunctionSettings> functionSettings = new HashMap<>();
		if (enableCustom && settings.enableCustomFunctions && settings.functionSettings != null) {
			for (LatteFunctionSettings functionSetting : settings.functionSettings) {
				functionSettings.put(functionSetting.getFunctionName(), functionSetting);
			}
		}

		for (Vendor vendor : getDefaultConfiguration().getVendors()) {
			if (settings.isEnabledSourceVendor(vendor)) {
				for (LatteFunctionSettings functionSetting : getDefaultConfiguration().getFunctions(vendor).values()) {
					if (!functionSettings.containsKey(functionSetting.getFunctionName())) {
						functionSettings.put(functionSetting.getFunctionName(), functionSetting);
					}
				}
			}
		}
		return Collections.unmodifiableMap(functionSettings);
	}

	@NotNull
	public Map<String, LatteTagSettings> getTags() {
		return getTags(true);
	}

	@NotNull
	private Map<String, LatteTagSettings> getTags(boolean enableCustom) {
		LatteSettings settings = getSettings();

		Map<String, LatteTagSettings> projectTags = new HashMap<>();
		if (enableCustom && settings.enableCustomMacros && settings.tagSettings != null) {
			for (LatteTagSettings tagSetting : settings.tagSettings) {
				projectTags.put(tagSetting.getMacroName(), tagSetting);
			}
		}

		for (Vendor vendor : getDefaultConfiguration().getVendors()) {
			if (settings.isEnabledSourceVendor(vendor)) {
				for (LatteTagSettings tagSetting : getDefaultConfiguration().getTags(vendor).values()) {
					if (!projectTags.containsKey(tagSetting.getMacroName())) {
						projectTags.put(tagSetting.getMacroName(), tagSetting);
					}
				}
			}
		}
		return Collections.unmodifiableMap(projectTags);
	}

	@NotNull
	public Map<String, LatteFilterSettings> getFilters() {
		return getFilters(true);
	}

	@NotNull
	private Map<String, LatteFilterSettings> getFilters(boolean enableCustom) {
		LatteSettings settings = getSettings();
		Map<String, LatteFilterSettings> projectFilters = new HashMap<>();
		if (enableCustom && settings.enableCustomModifiers && settings.filterSettings != null) {
			for (LatteFilterSettings filterSetting : settings.filterSettings) {
				projectFilters.put(filterSetting.getModifierName(), filterSetting);
			}
		}

		for (Vendor vendor : getDefaultConfiguration().getVendors()) {
			if (settings.isEnabledSourceVendor(vendor)) {
				for (LatteFilterSettings filterSetting : getDefaultConfiguration().getFilters(vendor).values()) {
					if (!projectFilters.containsKey(filterSetting.getModifierName())) {
						projectFilters.put(filterSetting.getModifierName(), filterSetting);
					}
				}
			}
		}
		return Collections.unmodifiableMap(projectFilters);
	}

	@NotNull
	public VendorResult getVendorForTag(String name) {
		return getVendorForSettings(getTags(false).getOrDefault(name, null));
	}

	@NotNull
	public VendorResult getVendorForFilter(String name) {
		return getVendorForSettings(getFilters(false).getOrDefault(name, null));
	}

	@NotNull
	public VendorResult getVendorForVariable(String name) {
		return getVendorForSettings(getVariables(false).getOrDefault(name, null));
	}

	@NotNull
	public VendorResult getVendorForFunction(String name) {
		return getVendorForSettings(getFunctions(false).getOrDefault(name, null));
	}

	private VendorResult getVendorForSettings(BaseLatteSettings settings) {
		if (settings != null) {
			return new VendorResult(settings.getVendor(), settings.getVendorName());
		}
		return VendorResult.CUSTOM;
	}

	private LatteDefaultConfiguration getDefaultConfiguration() {
		return LatteDefaultConfiguration.getInstance();
	}

	public static class VendorResult implements Serializable {
		public static VendorResult CUSTOM = new VendorResult(LatteConfiguration.Vendor.CUSTOM, "");

		public final String vendorName;
		public final LatteConfiguration.Vendor vendor;

		public VendorResult(LatteConfiguration.Vendor vendor, String vendorName) {
			this.vendor = vendor;
			this.vendorName = vendorName;
		}
	}

}
