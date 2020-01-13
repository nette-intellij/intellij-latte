package com.jantvrdik.intellij.latte.config;

import com.intellij.openapi.project.Project;
import com.jantvrdik.intellij.latte.settings.*;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.jantvrdik.intellij.latte.config.LatteMacro.Type.*;

public class LatteConfiguration {

	public static String LATTE_HELP_URL = "https://latte.nette.org/";
	public static String FORUM_URL = "https://forum.nette.org/";

	/** globally available class instance */
	public static final LatteConfiguration INSTANCE = new LatteConfiguration();

	/** list of standard macros, indexed by macro name */
	private Map<String, LatteMacro> standardMacros = new HashMap<String, LatteMacro>();

	/** list of standard macros, indexed by macro name */
	private Map<String, LatteModifier> standardModifiers = new HashMap<String, LatteModifier>();

	public LatteConfiguration() {
		initStandardMacros();
		initStandardModifiers();
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
		addStandardMacro("ifcontent", PAIR);

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
		addStandardMacroWithoutModifiers("templateType", UNPAIRED);
		addStandardMacroWithoutModifiers("templatePrint", UNPAIRED);

		addStandardMacro("assign", UNPAIRED);
		addStandardMacro("default", UNPAIRED);
		addStandardMacroWithoutModifiers("debugbreak", UNPAIRED);
		addStandardMacroWithoutModifiers("l", UNPAIRED, false);
		addStandardMacroWithoutModifiers("r", UNPAIRED, false);

		addStandardMacro("=", UNPAIRED);
		addStandardMacro("?", UNPAIRED);
		addStandardMacro("php", UNPAIRED);

		addStandardMacro("capture", PAIR);
		addStandardMacro("include", UNPAIRED);
		addStandardMacroWithoutModifiers("use", UNPAIRED);

		addStandardMacro("class", ATTR_ONLY);
		addStandardMacro("attr", ATTR_ONLY);
		addStandardMacroWithoutParameters("nonce", ATTR_ONLY);

		// BlockMacros
		LatteMacro includeBlock = addStandardMacroWithoutModifiers("includeblock", UNPAIRED);
		includeBlock.deprecated = true;
		includeBlock.deprecatedMessage = "Macro includeblock is deprecated in Latte 2.4. Use import macro instead.";
		addStandardMacro("import", UNPAIRED);
		addStandardMacroWithoutModifiers("extends", UNPAIRED);
		addStandardMacro("layout", UNPAIRED);
		addStandardMacro("block", PAIR);
		addStandardMacro("define", PAIR);

		LatteMacro ifCurrent = addStandardMacroWithoutModifiers("ifCurrent", PAIR);
		ifCurrent.deprecated = true;
		ifCurrent.deprecatedMessage = "Macro ifCurrent is deprecated in Latte 2.6. Use custom function isLinkCurrent() instead.";

		addStandardMacroWithoutModifiers("contentType", UNPAIRED);
	}

	private void initStandardModifiers() {
		addStandardModifier("truncate", "shortens the length preserving whole words", ":(length, append = '…')");
		addStandardModifier("substr", "returns part of the string", ":(offset [, length])");
		addStandardModifier("trim", "strips whitespace or other characters from the beginning and end of the string", ":(charset = mezery)");
		addStandardModifier("striptags", "removes HTML tags");
		addStandardModifier("strip", "removes whitespace");
		addStandardModifier("indent", "indents the text from left with number of tabs", ":(level = 1, char = \"\\t\")");
		addStandardModifier("replace", "replaces all occurrences of the search string with the replacement", ":(search, replace = '')");
		addStandardModifier("replaceRE", "replaces all occurrences according to regular expression", ":(pattern, replace = '')");
		addStandardModifier("padLeft", "completes the string to given length from left", ":(length, pad = ' ')");
		addStandardModifier("padRight", "completes the string to given length from right", ":(length, pad = ' ')");
		addStandardModifier("repeat", "repeats the string", ":(count)");
		addStandardModifier("implode", "joins an array to a string", ":(glue = '')");
		addStandardModifier("webalize", "adjusts the UTF-8 string to the shape used in the URL");
		addStandardModifier("breaklines", "inserts HTML line breaks before all newlines");
		addStandardModifier("reverse", "reverse an UTF-8 string or array");
		addStandardModifier("length", "returns length of a string or array");

		addStandardModifier("lower", "makes a string lower case");
		addStandardModifier("upper", "makes a string upper case");
		addStandardModifier("firstUpper", "makes the first letter upper case");
		addStandardModifier("capitalize", "lower case, the first letter of each word upper case");

		addStandardModifier("date", "formats date", ":(format)");
		addStandardModifier("number", "format number", ":(decimals = 0, decPoint = '.')");
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
	public LatteMacro getMacro(Project project, String name) {
		if (standardMacros.containsKey(name)) {
			return standardMacros.get(name);
		}

		Map<String, LatteMacro> projectMacros = getCustomMacros(project);
		if (projectMacros.containsKey(name)) {
			return projectMacros.get(name);
		}

		return null;
	}

	/**
	 * @return macro with given name or null macro is not available
	 */
	@Nullable
	public LatteModifier getModifier(Project project, String name) {
		if (standardModifiers.containsKey(name)) {
			return standardModifiers.get(name);
		}

		Map<String, LatteModifier> projectModifiers = getCustomModifiers(project);
		if (projectModifiers.containsKey(name)) {
			return projectModifiers.get(name);
		}

		return null;
	}

	@Nullable
	public LatteCustomFunctionSettings getFunction(Project project, String name) {
		LatteSettings settings = getSettings(project);
		if (!settings.enableCustomModifiers) {
			return null;
		}

		for (LatteCustomFunctionSettings functionSettings : settings.customFunctionSettings) {
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
	public LatteVariableSettings getVariable(Project project, String name) {
		name = LattePhpUtil.normalizePhpVariable(name);

		LatteSettings settings = getSettings(project);
		if (settings == null || !settings.enableDefaultVariables || settings.variableSettings == null) {
			return null;
		}

		for (LatteVariableSettings variable : settings.variableSettings) {
			if (variable.getVarName().equals(name)) {
				return variable;
			}
		}
		return null;
	}

	public LatteSettings getSettings(Project project) {
		return LatteSettings.getInstance(project);
	}

	@NotNull
	public List<LatteVariableSettings> getVariables(Project project) {
		LatteSettings settings = getSettings(project);
		if (!settings.enableDefaultVariables || settings.variableSettings == null) {
			return Collections.emptyList();
		}
		return new ArrayList<LatteVariableSettings>(settings.variableSettings);
	}

	@NotNull
	public List<LatteCustomFunctionSettings> getFunctions(Project project) {
		LatteSettings settings = getSettings(project);
		if (!settings.enableCustomFunctions) {
			return Collections.emptyList();
		}
		return new ArrayList<LatteCustomFunctionSettings>(settings.customFunctionSettings);
	}

	/**
	 * @return list of standard macros
	 */
	@NotNull
	public Map<String, LatteMacro> getStandardMacros() {
		return Collections.unmodifiableMap(standardMacros);
	}

	/**
	 * @return list of standard modifiers
	 */
	@NotNull
	public Map<String, LatteModifier> getStandardModifiers() {
		return Collections.unmodifiableMap(standardModifiers);
	}

	/**
	 * @return custom (project-specific) macros
	 */
	@NotNull
	public Map<String, LatteMacro> getCustomMacros(Project project) {
		LatteSettings settings = getSettings(project);
		if (!settings.enableCustomMacros) {
			return Collections.emptyMap();
		}

		Map<String, LatteMacro> projectMacros = new HashMap<String, LatteMacro>();
		for (LatteCustomMacroSettings customMacro : settings.customMacroSettings) {
			projectMacros.put(
					customMacro.getMacroName(),
					new LatteMacro(customMacro.getMacroName(), customMacro.getType(), customMacro.isAllowedModifiers(), customMacro.hasParameters())
			);
		}
		return Collections.unmodifiableMap(projectMacros);
	}

	/**
	 * @return custom (project-specific) modifiers
	 */
	@NotNull
	public Map<String, LatteModifier> getCustomModifiers(Project project) {
		LatteSettings settings = getSettings(project);
		if (!settings.enableCustomModifiers) {
			return Collections.emptyMap();
		}

		Map<String, LatteModifier> projectModifiers = new HashMap<String, LatteModifier>();
		for (LatteCustomModifierSettings customModifier : settings.customModifierSettings) {
			projectModifiers.put(
					customModifier.getModifierName(),
					new LatteModifier(customModifier.getModifierName(), customModifier.getModifierHelp(), customModifier.getModifierDescription())
			);
		}
		return Collections.unmodifiableMap(projectModifiers);
	}

	private void addStandardMacro(LatteMacro macro) {
		standardMacros.put(macro.name, macro);
	}

	private void addStandardMacro(String name, LatteMacro.Type type) {
		addStandardMacro(name, type, true);
	}

	private void addStandardMacroWithoutParameters(String name, LatteMacro.Type type) {
		addStandardMacro(name, type, true, false);
	}

	private LatteMacro addStandardMacroWithoutModifiers(String name, LatteMacro.Type type) {
		return addStandardMacro(name, type, false);
	}

	private void addStandardMacroWithoutModifiers(String name, LatteMacro.Type type, boolean hasParameters) {
		addStandardMacro(name, type, false, hasParameters);
	}

	private LatteMacro addStandardMacro(String name, LatteMacro.Type type, boolean allowedModifiers) {
		return addStandardMacro(name, type, allowedModifiers, true);
	}

	private LatteMacro addStandardMacro(String name, LatteMacro.Type type, boolean allowedModifiers, boolean hasParameters) {
		LatteMacro macro = new LatteMacro(name, type, allowedModifiers, hasParameters);
		addStandardMacro(macro);
		return macro;
	}

	private void addStandardModifier(String name, String description) {
		addStandardModifier(name, description, "");
	}

	private void addStandardModifier(String name, String description, String help) {
		LatteModifier modifier = new LatteModifier(name, description, help);
		standardModifiers.put(modifier.name, modifier);
	}

}
