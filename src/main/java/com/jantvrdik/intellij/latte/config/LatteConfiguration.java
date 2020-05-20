package com.jantvrdik.intellij.latte.config;

import com.intellij.openapi.project.Project;
import com.jantvrdik.intellij.latte.settings.*;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.jantvrdik.intellij.latte.settings.LatteCustomMacroSettings.Type.*;

public class LatteConfiguration {

	public static String LATTE_HELP_URL = "https://latte.nette.org/";
	public static String LATTE_DOCS_XML_FILES_URL = "https://github.com/nette-intellij/intellij-latte/blob/master/docs/en/xmlFilesConfiguration.md";
	public static String FORUM_URL = "https://forum.nette.org/";

	private static Map<Project, LatteConfiguration> instances = new HashMap<>();

	/** list of standard macros, indexed by macro name */
	private Map<String, LatteCustomMacroSettings> standardMacros = new HashMap<>();

	/** list of standard macros, indexed by macro name */
	private Map<String, LatteCustomModifierSettings> standardModifiers = new HashMap<>();

	@NotNull
	private Project project;

	public LatteConfiguration(@NotNull Project project) {
		this.project = project;
		if (getSettings().disableDefaultLoading) {
			return;
		}

		initStandardMacros();
		initStandardModifiers();
	}

	public void reinitialize() {
		standardMacros = new HashMap<>();
		standardModifiers = new HashMap<>();
		if (!getSettings().disableDefaultLoading) {
			initStandardMacros();
			initStandardModifiers();
		}
		LatteFileConfiguration.getInstance(project).reinitialize();
	}

	public static LatteConfiguration getInstance(@NotNull Project project) {
		if (!instances.containsKey(project)) {
			instances.put(project, new LatteConfiguration(project));
		}
		return instances.get(project);
	}

	/**
	 * Initializes standard macros, currently based on Nette 2.1.2.
	 */
	private void initStandardMacros() {
		// Built-in
		addStandardMacro("syntax", PAIR);

		// CoreMacros
		addStandardMacroWithoutModifiers("if", PAIR);
		addStandardMacroWithoutModifiers("ifset", PAIR);
		addStandardMacroWithoutModifiers("else", UNPAIRED, false);
		addStandardMacroWithoutModifiers("elseif", UNPAIRED);
		addStandardMacroWithoutModifiers("elseifset", UNPAIRED);
		addStandardMacroWithoutParameters("ifcontent", PAIR);

		addStandardMacroWithoutModifiers("switch", PAIR);
		addStandardMacroWithoutModifiers("case", UNPAIRED);

		addStandardMacroWithoutModifiers("foreach", PAIR);
		addStandardMacroWithoutModifiers("for", PAIR);
		addStandardMacroWithoutModifiers("while", PAIR);
		addStandardMacroWithoutModifiers("continueIf", UNPAIRED);
		addStandardMacroWithoutModifiers("breakIf", UNPAIRED);

		addStandardMacroWithoutParameters("first", PAIR);
		addStandardMacroWithoutParameters("last", PAIR);
		addStandardMacroWithoutParameters("sep", PAIR);

		addStandardMacroWithoutModifiers("spaceless", PAIR, false);

		addStandardMacroWithoutModifiers("var", UNPAIRED);
		addStandardMacroWithoutModifiers("varType", UNPAIRED);
		addStandardMacroWithoutModifiers("varPrint", UNPAIRED);
		addStandardMacroWithoutModifiers("templateType", UNPAIRED);
		addStandardMacroWithoutModifiers("templatePrint", UNPAIRED);

		addStandardMacro("assign", UNPAIRED);
		addStandardMacro("default", UNPAIRED);
		addStandardMacroWithoutModifiers("debugbreak", UNPAIRED);
		addStandardMacroWithoutModifiers("l", UNPAIRED, false);
		addStandardMacroWithoutModifiers("r", UNPAIRED, false);

		LatteCustomMacroSettings unknown = addStandardMacro("?", UNPAIRED);
		unknown.setDeprecated(true);
		unknown.setDeprecatedMessage("Tag {? ...} is deprecated in Latte 2.4. For variable definitions use {var ...} or {php ...} in other cases.");

		addStandardMacro("=", UNPAIRED);
		addStandardMacro("php", UNPAIRED);
		addStandardMacro("do", UNPAIRED); // alias for {php }

		addStandardMacro("capture", PAIR);
		addStandardMacroWithoutModifiers("include", UNPAIRED);
		addStandardMacroWithoutModifiers("sandbox", UNPAIRED);
		addStandardMacroWithoutModifiers("widget", PAIR);
		addStandardMacroWithoutModifiers("use", UNPAIRED);

		addStandardMacro("class", ATTR_ONLY);
		addStandardMacro("attr", ATTR_ONLY);
		addStandardMacroWithoutParameters("nonce", ATTR_ONLY);

		// BlockMacros
		LatteCustomMacroSettings includeBlock = addStandardMacroWithoutModifiers("includeblock", UNPAIRED);
		includeBlock.setDeprecated(true);
		includeBlock.setDeprecatedMessage("Tag {includeblock} is deprecated in Latte 2.4. Use {include} tag instead.");
		addStandardMacroWithoutModifiers("import", UNPAIRED);
		addStandardMacroWithoutModifiers("extends", UNPAIRED);
		addStandardMacroWithoutModifiers("layout", UNPAIRED);
		addStandardMacroWithoutModifiers("block", PAIR);
		addStandardMacroWithoutModifiers("define", PAIR);

		LatteCustomMacroSettings ifCurrent = addStandardMacroWithoutModifiers("ifCurrent", PAIR);
		ifCurrent.setDeprecated(true);
		ifCurrent.setDeprecatedMessage("Tag {ifCurrent} is deprecated in Latte 2.6. Use custom function isLinkCurrent() instead.");

		addStandardMacroWithoutModifiers("contentType", UNPAIRED);
	}

	private void initStandardModifiers() {
		addStandardModifier("truncate", "shortens the length preserving whole words", ":(length, append = '…')", ":");
		addStandardModifier("substr", "returns part of the string", ":(offset [, length])", ":");
		addStandardModifier("trim", "strips whitespace or other characters from the beginning and end of the string", ":(charset = mezery)");
		addStandardModifier("stripHtml", "removes HTML tags and converts HTML entities to text");
		addStandardModifier("strip", "removes whitespace");
		addStandardModifier("indent", "indents the text from left with number of tabs", ":(level = 1, char = \"\\t\")");
		addStandardModifier("replace", "replaces all occurrences of the search string with the replacement", ":(search, replace = '')", ":");
		addStandardModifier("replaceRE", "replaces all occurrences according to regular expression", ":(pattern, replace = '')", ":");
		addStandardModifier("padLeft", "completes the string to given length from left", ":(length, pad = ' ')", ":");
		addStandardModifier("padRight", "completes the string to given length from right", ":(length, pad = ' ')", ":");
		addStandardModifier("repeat", "repeats the string", ":(count)", ":");
		addStandardModifier("implode", "joins an array to a string", ":(glue = '')");
		addStandardModifier("webalize", "adjusts the UTF-8 string to the shape used in the URL");
		addStandardModifier("breaklines", "inserts HTML line breaks before all newlines");
		addStandardModifier("reverse", "reverse an UTF-8 string or array");
		addStandardModifier("length", "returns length of a string or array");
		addStandardModifier("batch", "returns length of a string or array", ":(array, length [, item])", "::");

		addStandardModifier("lower", "makes a string lower case");
		addStandardModifier("upper", "makes a string upper case");
		addStandardModifier("firstUpper", "makes the first letter upper case");
		addStandardModifier("capitalize", "lower case, the first letter of each word upper case");

		addStandardModifier("date", "formats date", ":(format)", ":");
		addStandardModifier("number", "format number", ":(decimals = 0, decPoint = '.', thousandsSep = ',')");
		addStandardModifier("bytes", "formats size in bytes", ":(precision = 2)");
		addStandardModifier("dataStream", "Data URI protocol conversion", ":(mimetype = detect)");

		addStandardModifier("noescape", "prints a variable without escaping");
		addStandardModifier("escapeurl", "escapes parameter in URL");

		addStandardModifier("nocheck", "prevents automatic URL sanitization");
		addStandardModifier("checkurl", "sanitizes string for use inside href attribute");
	}

	/**
	 * @return macro with given name or null macro is not available
	 */
	@Nullable
	public LatteCustomMacroSettings getMacro(String name) {
		if (standardMacros.containsKey(name)) {
			return standardMacros.get(name);
		}

		Map<String, LatteCustomMacroSettings> projectMacros = getTags();
		if (projectMacros.containsKey(name)) {
			return projectMacros.get(name);
		}

		return null;
	}

	/**
	 * @return macro with given name or null macro is not available
	 */
	@Nullable
	public LatteCustomModifierSettings getModifier(String name) {
		if (standardModifiers.containsKey(name)) {
			return standardModifiers.get(name);
		}

		Map<String, LatteCustomModifierSettings> projectModifiers = getFilters();
		if (projectModifiers.containsKey(name)) {
			return projectModifiers.get(name);
		}

		return null;
	}

	@Nullable
	public LatteCustomFunctionSettings getFunction(String name) {
		LatteSettings settings = getSettings();
		if (!settings.enableCustomModifiers) {
			return null;
		}

		for (LatteCustomFunctionSettings functionSettings : getFunctions()) {
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

		LatteSettings settings = getSettings();
		if (settings == null) {
			return null;
		}

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
	public List<LatteVariableSettings> getVariables() {
		LatteSettings settings = getSettings();
		List<LatteVariableSettings> variableSettings = new ArrayList<>();
		for (Map.Entry<String, LatteVariableSettings> entry : LatteFileConfiguration.getInstance(project).getVariables().entrySet()) {
			variableSettings.add(entry.getValue());
		}

		if (!settings.enableDefaultVariables || settings.variableSettings == null) {
			return variableSettings;
		}
		variableSettings.addAll(settings.variableSettings);
		return variableSettings;
	}

	@NotNull
	public List<LatteCustomFunctionSettings> getFunctions() {
		LatteSettings settings = getSettings();

		List<LatteCustomFunctionSettings> functionSettings = new ArrayList<>();
		for (Map.Entry<String, LatteCustomFunctionSettings> entry : LatteFileConfiguration.getInstance(project).getFunctions().entrySet()) {
			functionSettings.add(entry.getValue());
		}

		if (!settings.enableCustomFunctions) {
			return Collections.emptyList();
		}
		functionSettings.addAll(settings.customFunctionSettings);
		return functionSettings;
	}

	/**
	 * @return custom (project-specific) macros
	 */
	@NotNull
	public Map<String, LatteCustomMacroSettings> getTags() {
		LatteSettings settings = getSettings();
		Map<String, LatteCustomMacroSettings> projectMacros = new HashMap<>();
		for (Map.Entry<String, LatteCustomMacroSettings> entry : LatteFileConfiguration.getInstance(project).getTags().entrySet()) {
			projectMacros.put(entry.getKey(), entry.getValue());
		}

		if (!settings.enableCustomMacros) {
			return projectMacros;
		}

		for (LatteCustomMacroSettings customMacro : settings.customMacroSettings) {
			projectMacros.put(customMacro.getMacroName(), customMacro);
		}
		return Collections.unmodifiableMap(projectMacros);
	}

	/**
	 * @return custom (project-specific) modifiers
	 */
	@NotNull
	public Map<String, LatteCustomModifierSettings> getFilters() {
		LatteSettings settings = getSettings();
		Map<String, LatteCustomModifierSettings> projectFilters = new HashMap<>();
		for (Map.Entry<String, LatteCustomModifierSettings> entry : LatteFileConfiguration.getInstance(project).getFilters().entrySet()) {
			projectFilters.put(entry.getKey(), entry.getValue());
		}

		if (!settings.enableCustomModifiers) {
			return Collections.emptyMap();
		}

		for (LatteCustomModifierSettings customModifier : settings.customModifierSettings) {
			projectFilters.put(customModifier.getModifierName(), customModifier);
		}
		return Collections.unmodifiableMap(projectFilters);
	}

	private void addStandardMacro(LatteCustomMacroSettings macro) {
		standardMacros.put(macro.getMacroName(), macro);
	}

	private LatteCustomMacroSettings addStandardMacro(String name, LatteCustomMacroSettings.Type type) {
		return addStandardMacro(name, type, true);
	}

	private void addStandardMacroWithoutParameters(String name, LatteCustomMacroSettings.Type type) {
		addStandardMacro(name, type, true, false);
	}

	private LatteCustomMacroSettings addStandardMacroWithoutModifiers(String name, LatteCustomMacroSettings.Type type) {
		return addStandardMacro(name, type, false);
	}

	private void addStandardMacroWithoutModifiers(String name, LatteCustomMacroSettings.Type type, boolean hasParameters) {
		addStandardMacro(name, type, false, hasParameters);
	}

	private LatteCustomMacroSettings addStandardMacro(String name, LatteCustomMacroSettings.Type type, boolean allowedModifiers) {
		return addStandardMacro(name, type, allowedModifiers, true);
	}

	private LatteCustomMacroSettings addStandardMacro(String name, LatteCustomMacroSettings.Type type, boolean allowedModifiers, boolean hasParameters) {
		LatteCustomMacroSettings macro = new LatteCustomMacroSettings(name, type, allowedModifiers, hasParameters);
		addStandardMacro(macro);
		return macro;
	}

	private void addStandardModifier(String name, String description) {
		addStandardModifier(name, description, "", "");
	}

	private void addStandardModifier(String name, String description, String help) {
		addStandardModifier(name, description, help, "");
	}

	private void addStandardModifier(String name, String description, String help, String insertColons) {
		LatteCustomModifierSettings modifier = new LatteCustomModifierSettings(name, description, help, insertColons);
		standardModifiers.put(modifier.getModifierName(), modifier);
	}

}
