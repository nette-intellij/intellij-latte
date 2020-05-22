package com.jantvrdik.intellij.latte.config;

import com.intellij.openapi.project.Project;
import com.jantvrdik.intellij.latte.settings.*;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.jantvrdik.intellij.latte.settings.LatteTagSettings.Type.*;

public class LatteConfiguration {

	public static String LATTE_HELP_URL = "https://latte.nette.org/";
	public static String LATTE_DOCS_XML_FILES_URL = "https://github.com/nette-intellij/intellij-latte/blob/master/docs/en/xmlFilesConfiguration.md";
	public static String FORUM_URL = "https://forum.nette.org/";

	public enum Vendor {
		OTHER, NETTE, LATTE, CUSTOM
	}

	private static Map<Project, LatteConfiguration> instances = new HashMap<>();

	/** list of standard tags, indexed by tag name */
	private Map<String, LatteTagSettings> standardTags = new HashMap<>();

	/** list of standard tags, indexed by tag name */
	private Map<String, LatteFilterSettings> standardFilters = new HashMap<>();

	final public static Map<String, LatteTagSettings> standardNetteTags = new HashMap<String, LatteTagSettings>(){{
		put("href", (new LatteTagSettings("href", ATTR_ONLY)).setVendor(Vendor.NETTE));
		put("link", (new LatteTagSettings("link", UNPAIRED)).setVendor(Vendor.NETTE));
		put("plink", (new LatteTagSettings("plink", UNPAIRED)).setVendor(Vendor.NETTE));
		put("control", (new LatteTagSettings("control", UNPAIRED, false, true)).setVendor(Vendor.NETTE));
		put("snippet", (new LatteTagSettings("snippet", PAIR, false, true, true)).setVendor(Vendor.NETTE));
		put("snippetArea", (new LatteTagSettings("snippetArea", PAIR, false, true, true)).setVendor(Vendor.NETTE));
		put("form", (new LatteTagSettings("form", PAIR, false, true, true)).setVendor(Vendor.NETTE));
		put("formContainer", (new LatteTagSettings("formContainer", PAIR, false, true, true)).setVendor(Vendor.NETTE));
		put("label", (new LatteTagSettings("label", AUTO_EMPTY, false, true)).setVendor(Vendor.NETTE));
		put("input", (new LatteTagSettings("input", UNPAIRED, false, true)).setVendor(Vendor.NETTE));
		put("inputError", (new LatteTagSettings("inputError", UNPAIRED, false, true)).setVendor(Vendor.NETTE));
		put("name", (new LatteTagSettings("name", ATTR_ONLY)).setVendor(Vendor.NETTE));
		put("_", (new LatteTagSettings("_", PAIR)).setVendor(Vendor.NETTE));
		put("dump", (new LatteTagSettings("dump", UNPAIRED, false, true)).setVendor(Vendor.NETTE));
		put("cache", (new LatteTagSettings("cache", PAIR, false, true)).setVendor(Vendor.NETTE));
		put("ifCurrent", (new LatteTagSettings("ifCurrent", PAIR, false, true, false, "Tag {ifCurrent} is deprecated in Latte 2.6. Use custom function isLinkCurrent() instead.")).setVendor(Vendor.NETTE));
	}};

	final public static Map<String, LatteVariableSettings> standardNetteVariables = new HashMap<String, LatteVariableSettings>(){{
		put("control", (new LatteVariableSettings("control", "\\Nette\\Application\\UI\\Control")).setVendor(Vendor.NETTE));
		put("basePath", (new LatteVariableSettings("basePath", "string")).setVendor(Vendor.NETTE));
		put("baseUrl", (new LatteVariableSettings("baseUrl", "string")).setVendor(Vendor.NETTE));
		put("baseUri", (new LatteVariableSettings("baseUri", "string")).setVendor(Vendor.NETTE));
		put("flashes", (new LatteVariableSettings("flashes", "mixed[]")).setVendor(Vendor.NETTE));
		put("presenter", (new LatteVariableSettings("presenter", "\\Nette\\Application\\UI\\Presenter")).setVendor(Vendor.NETTE));
		put("iterator", (new LatteVariableSettings("iterator", "\\Latte\\Runtime\\CachingIterator")).setVendor(Vendor.NETTE));
		put("form", (new LatteVariableSettings("form", "\\Nette\\Application\\UI\\Form")).setVendor(Vendor.NETTE));
		put("user", (new LatteVariableSettings("user", "\\Nette\\Security\\User")).setVendor(Vendor.NETTE));
	}};

	final public static Map<String, LatteFunctionSettings> standardNetteFunctions = new HashMap<String, LatteFunctionSettings>(){{
		put("isLinkCurrent", (new LatteFunctionSettings("isLinkCurrent", "bool", "(string $destination = null, $args = [])")).setVendor(Vendor.NETTE));
		put("isModuleCurrent", (new LatteFunctionSettings("isModuleCurrent", "bool", "(string $moduleName)")).setVendor(Vendor.NETTE));
	}};

	@NotNull
	private Project project;

	public LatteConfiguration(@NotNull Project project) {
		this.project = project;
		initStandardTags();
		initStandardFilters();
	}

	public static LatteConfiguration getInstance(@NotNull Project project) {
		if (!instances.containsKey(project)) {
			instances.put(project, new LatteConfiguration(project));
		}
		return instances.get(project);
	}

	/**
	 * Initializes standard tags, currently based on Nette 2.1.2.
	 */
	private void initStandardTags() {
		// Built-in
		addStandardTag("syntax", PAIR);

		// CoreTags
		addStandardTagWithoutFilters("if", PAIR);
		addStandardTagWithoutFilters("ifset", PAIR);
		addStandardTagWithoutFilters("else", UNPAIRED, false);
		addStandardTagWithoutFilters("elseif", UNPAIRED);
		addStandardTagWithoutFilters("elseifset", UNPAIRED);
		addStandardTagWithoutParameters("ifcontent", PAIR);

		addStandardTagWithoutFilters("switch", PAIR);
		addStandardTagWithoutFilters("case", UNPAIRED);

		addStandardTagWithoutFilters("foreach", PAIR);
		addStandardTagWithoutFilters("for", PAIR);
		addStandardTagWithoutFilters("while", PAIR);
		addStandardTagWithoutFilters("continueIf", UNPAIRED);
		addStandardTagWithoutFilters("breakIf", UNPAIRED);

		addStandardTagWithoutParameters("first", PAIR);
		addStandardTagWithoutParameters("last", PAIR);
		addStandardTagWithoutParameters("sep", PAIR);

		addStandardTagWithoutFilters("spaceless", PAIR, false);

		addStandardTagWithoutFilters("var", UNPAIRED);
		addStandardTagWithoutFilters("varType", UNPAIRED);
		addStandardTagWithoutFilters("varPrint", UNPAIRED);
		addStandardTagWithoutFilters("templateType", UNPAIRED);
		addStandardTagWithoutFilters("templatePrint", UNPAIRED);

		addStandardTag("assign", UNPAIRED);
		addStandardTag("default", UNPAIRED);
		addStandardTagWithoutFilters("debugbreak", UNPAIRED);
		addStandardTagWithoutFilters("l", UNPAIRED, false);
		addStandardTagWithoutFilters("r", UNPAIRED, false);

		LatteTagSettings unknown = addStandardTag("?", UNPAIRED);
		unknown.setDeprecated(true);
		unknown.setDeprecatedMessage("Tag {? ...} is deprecated in Latte 2.4. For variable definitions use {var ...} or {php ...} in other cases.");

		addStandardTag("=", UNPAIRED);
		addStandardTag("php", UNPAIRED);
		addStandardTag("do", UNPAIRED); // alias for {php }

		addStandardTag("capture", PAIR);
		addStandardTagWithoutFilters("include", UNPAIRED);
		addStandardTagWithoutFilters("sandbox", UNPAIRED);
		addStandardTagWithoutFilters("widget", PAIR);
		addStandardTagWithoutFilters("use", UNPAIRED);

		addStandardTag("class", ATTR_ONLY);
		addStandardTag("attr", ATTR_ONLY);
		addStandardTagWithoutParameters("nonce", ATTR_ONLY);

		// BlockTags
		LatteTagSettings includeBlock = addStandardTagWithoutFilters("includeblock", UNPAIRED);
		includeBlock.setDeprecated(true);
		includeBlock.setDeprecatedMessage("Tag {includeblock} is deprecated in Latte 2.4. Use {include} tag instead.");
		addStandardTagWithoutFilters("import", UNPAIRED);
		addStandardTagWithoutFilters("extends", UNPAIRED);
		addStandardTagWithoutFilters("layout", UNPAIRED);
		addStandardTagWithoutFilters("block", PAIR);
		addStandardTagWithoutFilters("define", PAIR);

		addStandardTagWithoutFilters("contentType", UNPAIRED);
	}

	private void initStandardFilters() {
		addStandardFilter("truncate", "shortens the length preserving whole words", ":(length, append = '…')", ":");
		addStandardFilter("substr", "returns part of the string", ":(offset [, length])", ":");
		addStandardFilter("trim", "strips whitespace or other characters from the beginning and end of the string", ":(charset = mezery)");
		addStandardFilter("stripHtml", "removes HTML tags and converts HTML entities to text");
		addStandardFilter("strip", "removes whitespace");
		addStandardFilter("indent", "indents the text from left with number of tabs", ":(level = 1, char = \"\\t\")");
		addStandardFilter("replace", "replaces all occurrences of the search string with the replacement", ":(search, replace = '')", ":");
		addStandardFilter("replaceRE", "replaces all occurrences according to regular expression", ":(pattern, replace = '')", ":");
		addStandardFilter("padLeft", "completes the string to given length from left", ":(length, pad = ' ')", ":");
		addStandardFilter("padRight", "completes the string to given length from right", ":(length, pad = ' ')", ":");
		addStandardFilter("repeat", "repeats the string", ":(count)", ":");
		addStandardFilter("implode", "joins an array to a string", ":(glue = '')");
		addStandardFilter("webalize", "adjusts the UTF-8 string to the shape used in the URL");
		addStandardFilter("breaklines", "inserts HTML line breaks before all newlines");
		addStandardFilter("reverse", "reverse an UTF-8 string or array");
		addStandardFilter("length", "returns length of a string or array");
		addStandardFilter("batch", "returns length of a string or array", ":(array, length [, item])", "::");

		addStandardFilter("lower", "makes a string lower case");
		addStandardFilter("upper", "makes a string upper case");
		addStandardFilter("firstUpper", "makes the first letter upper case");
		addStandardFilter("capitalize", "lower case, the first letter of each word upper case");

		addStandardFilter("date", "formats date", ":(format)", ":");
		addStandardFilter("number", "format number", ":(decimals = 0, decPoint = '.', thousandsSep = ',')");
		addStandardFilter("bytes", "formats size in bytes", ":(precision = 2)");
		addStandardFilter("dataStream", "Data URI protocol conversion", ":(mimetype = detect)");

		addStandardFilter("noescape", "prints a variable without escaping");
		addStandardFilter("escapeurl", "escapes parameter in URL");

		addStandardFilter("nocheck", "prevents automatic URL sanitization");
		addStandardFilter("checkurl", "sanitizes string for use inside href attribute");
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

		if (settings.enableNette && !LatteFileConfiguration.getInstance(project).hasVendor(Vendor.NETTE)) {
			for (LatteVariableSettings variableSetting : standardNetteVariables.values()) {
				if (!variableSettings.containsKey(variableSetting.getVarName())) {
					variableSettings.put(variableSetting.getVarName(), variableSetting);
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

		if (settings.enableNette && !LatteFileConfiguration.getInstance(project).hasVendor(Vendor.NETTE)) {
			for (LatteFunctionSettings functionSetting : standardNetteFunctions.values()) {
				if (!functionSettings.containsKey(functionSetting.getFunctionName())) {
					functionSettings.put(functionSetting.getFunctionName(), functionSetting);
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

		if (!LatteFileConfiguration.getInstance(project).hasVendor(Vendor.LATTE)) {
			for (LatteTagSettings tagSetting : standardTags.values()) {
				if (!projectTags.containsKey(tagSetting.getMacroName())) {
					projectTags.put(tagSetting.getMacroName(), tagSetting);
				}
			}
		}

		if (settings.enableNette && !LatteFileConfiguration.getInstance(project).hasVendor(Vendor.NETTE)) {
			for (LatteTagSettings tagSetting : standardNetteTags.values()) {
				if (!projectTags.containsKey(tagSetting.getMacroName())) {
					projectTags.put(tagSetting.getMacroName(), tagSetting);
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

		if (!LatteFileConfiguration.getInstance(project).hasVendor(Vendor.LATTE)) {
			for (LatteFilterSettings filterSetting : standardFilters.values()) {
				if (!projectFilters.containsKey(filterSetting.getModifierName())) {
					projectFilters.put(filterSetting.getModifierName(), filterSetting);
				}
			}
		}
		return Collections.unmodifiableMap(projectFilters);
	}

	@NotNull
	public LatteFileConfiguration.VendorResult getVendorForTag(String name) {
		return getVendorForSettings(getTags(false).getOrDefault(name, null));
	}

	@NotNull
	public LatteFileConfiguration.VendorResult getVendorForFilter(String name) {
		return getVendorForSettings(getFilters(false).getOrDefault(name, null));
	}

	@NotNull
	public LatteFileConfiguration.VendorResult getVendorForVariable(String name) {
		return getVendorForSettings(getVariables(false).getOrDefault(name, null));
	}

	@NotNull
	public LatteFileConfiguration.VendorResult getVendorForFunction(String name) {
		return getVendorForSettings(getFunctions(false).getOrDefault(name, null));
	}

	private LatteFileConfiguration.VendorResult getVendorForSettings(BaseLatteSettings settings) {
		if (settings != null) {
			return new LatteFileConfiguration.VendorResult(settings.getVendor(), settings.getVendorName());
		}
		return LatteFileConfiguration.VendorResult.CUSTOM;
	}

	private void addStandardTag(LatteTagSettings tag) {
		standardTags.put(tag.getMacroName(), tag);
	}

	private LatteTagSettings addStandardTag(String name, LatteTagSettings.Type type) {
		return addStandardTag(name, type, true);
	}

	private void addStandardTagWithoutParameters(String name, LatteTagSettings.Type type) {
		addStandardTag(name, type, true, false);
	}

	private LatteTagSettings addStandardTagWithoutFilters(String name, LatteTagSettings.Type type) {
		return addStandardTag(name, type, false);
	}

	private void addStandardTagWithoutFilters(String name, LatteTagSettings.Type type, boolean hasParameters) {
		addStandardTag(name, type, false, hasParameters);
	}

	private LatteTagSettings addStandardTag(String name, LatteTagSettings.Type type, boolean allowedFilters) {
		return addStandardTag(name, type, allowedFilters, true);
	}

	private LatteTagSettings addStandardTag(String name, LatteTagSettings.Type type, boolean allowedFilters, boolean hasParameters) {
		LatteTagSettings tag = new LatteTagSettings(name, type, allowedFilters, hasParameters);
		tag.setVendor(Vendor.LATTE);
		addStandardTag(tag);
		return tag;
	}

	private void addStandardFilter(String name, String description) {
		addStandardFilter(name, description, "", "");
	}

	private void addStandardFilter(String name, String description, String help) {
		addStandardFilter(name, description, help, "");
	}

	private void addStandardFilter(String name, String description, String help, String insertColons) {
		LatteFilterSettings filter = new LatteFilterSettings(name, description, help, insertColons);
		filter.setVendor(Vendor.LATTE);
		standardFilters.put(filter.getModifierName(), filter);
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
