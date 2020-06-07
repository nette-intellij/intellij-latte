package com.jantvrdik.intellij.latte.config;

import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.jantvrdik.intellij.latte.settings.*;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileData;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LatteConfiguration {

	public static String LATTE_HELP_URL = "https://latte.nette.org/";
	public static String LATTE_DOCS_XML_FILES_URL = "https://github.com/nette-intellij/intellij-latte/blob/master/docs/en/xmlFilesConfiguration.md";
	public static String FORUM_URL = "https://forum.nette.org/";

	public enum Vendor {
		OTHER("Other", (JBColor) JBColor.GREEN.darker()),
		NETTE_APPLICATION("nette/application", JBColor.BLUE),
		NETTE_FORMS("nette/forms", JBColor.BLUE),
		LATTE("Latte", JBColor.ORANGE),
		CUSTOM("Custom", JBColor.GRAY);

		private String name;

		private JBColor color;

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

	private static Map<Project, LatteConfiguration> instances = new HashMap<>();

	@NotNull
	private Project project;

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
		name = LattePhpUtil.normalizePhpVariable(name);
		for (LatteVariableSettings variable : getVariables()) {
			if (variable.getVarName().equals(name)) {
				return variable;
			}
		}
		return null;
	}

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

		for (LatteVariableSettings variableSetting : LatteFileConfiguration.getInstance(project).getVariables().values()) {
			if (!variableSettings.containsKey(variableSetting.getVarName())) {
				variableSettings.put(variableSetting.getVarName(), variableSetting);
			}
		}

		for (Vendor vendor : LatteDefaultConfiguration.getInstance(project).getVendors()) {
			if (settings.isEnabledSourceVendor(vendor) && !LatteFileConfiguration.getInstance(project).hasVendor(vendor)) {
				for (LatteVariableSettings variableSetting : LatteDefaultConfiguration.getInstance(project).getVariables(vendor).values()) {
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

		for (LatteFunctionSettings functionSetting : LatteFileConfiguration.getInstance(project).getFunctions().values()) {
			if (!functionSettings.containsKey(functionSetting.getFunctionName())) {
				functionSettings.put(functionSetting.getFunctionName(), functionSetting);
			}
		}

		for (Vendor vendor : LatteDefaultConfiguration.getInstance(project).getVendors()) {
			if (settings.isEnabledSourceVendor(vendor) && !LatteFileConfiguration.getInstance(project).hasVendor(vendor)) {
				for (LatteFunctionSettings functionSetting : LatteDefaultConfiguration.getInstance(project).getFunctions(vendor).values()) {
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

		for (LatteTagSettings tagSetting : LatteFileConfiguration.getInstance(project).getTags().values()) {
			if (!projectTags.containsKey(tagSetting.getMacroName())) {
				projectTags.put(tagSetting.getMacroName(), tagSetting);
			}
		}

		for (Vendor vendor : LatteDefaultConfiguration.getInstance(project).getVendors()) {
			if (settings.isEnabledSourceVendor(vendor) && !LatteFileConfiguration.getInstance(project).hasVendor(vendor)) {
				for (LatteTagSettings tagSetting : LatteDefaultConfiguration.getInstance(project).getTags(vendor).values()) {
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

		for (LatteFilterSettings filterSetting : LatteFileConfiguration.getInstance(project).getFilters().values()) {
			if (!projectFilters.containsKey(filterSetting.getModifierName())) {
				projectFilters.put(filterSetting.getModifierName(), filterSetting);
			}
		}

		for (Vendor vendor : LatteDefaultConfiguration.getInstance(project).getVendors()) {
			if (settings.isEnabledSourceVendor(vendor) && !LatteFileConfiguration.getInstance(project).hasVendor(vendor)) {
				for (LatteFilterSettings filterSetting : LatteDefaultConfiguration.getInstance(project).getFilters(vendor).values()) {
					if (!projectFilters.containsKey(filterSetting.getModifierName())) {
						projectFilters.put(filterSetting.getModifierName(), filterSetting);
					}
				}
			}
		}
		return Collections.unmodifiableMap(projectFilters);
	}

	@NotNull
	public LatteXmlFileData.VendorResult getVendorForTag(String name) {
		return getVendorForSettings(getTags(false).getOrDefault(name, null));
	}

	@NotNull
	public LatteXmlFileData.VendorResult getVendorForFilter(String name) {
		return getVendorForSettings(getFilters(false).getOrDefault(name, null));
	}

	@NotNull
	public LatteXmlFileData.VendorResult getVendorForVariable(String name) {
		return getVendorForSettings(getVariables(false).getOrDefault(name, null));
	}

	@NotNull
	public LatteXmlFileData.VendorResult getVendorForFunction(String name) {
		return getVendorForSettings(getFunctions(false).getOrDefault(name, null));
	}

	private LatteXmlFileData.VendorResult getVendorForSettings(BaseLatteSettings settings) {
		if (settings != null) {
			return new LatteXmlFileData.VendorResult(settings.getVendor(), settings.getVendorName());
		}
		return LatteXmlFileData.VendorResult.CUSTOM;
	}

	public static boolean isValidVendor(String type) {

		for (Vendor c : Vendor.values()) {
			if (c.name().equals(type)) {
				return true;
			}
		}
		return false;
	}

}
