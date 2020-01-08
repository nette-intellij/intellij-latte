package com.jantvrdik.intellij.latte.config;

import com.intellij.openapi.project.Project;
import com.jantvrdik.intellij.latte.settings.LatteCustomMacroSettings;
import com.jantvrdik.intellij.latte.settings.LatteCustomModifierSettings;
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.jantvrdik.intellij.latte.config.LatteMacro.Type.*;

public class LatteConfiguration {

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

		// CacheMacro
		addStandardMacro("cache", PAIR);

		// CoreMacros
		addStandardMacroWithoutModifiers("if", PAIR);
		addStandardMacroWithoutModifiers("ifset", PAIR);
		addStandardMacroWithoutModifiers("else", UNPAIRED);
		addStandardMacroWithoutModifiers("elseif", UNPAIRED);
		addStandardMacroWithoutModifiers("elseifset", UNPAIRED);
		addStandardMacro("ifcontent", PAIR);

		addStandardMacro("switch", PAIR);
		addStandardMacro("case", UNPAIRED);

		addStandardMacro("foreach", PAIR);
		addStandardMacro("for", PAIR);
		addStandardMacro("while", PAIR);
		addStandardMacroWithoutModifiers("continueIf", UNPAIRED);
		addStandardMacroWithoutModifiers("breakIf", UNPAIRED);
		addStandardMacro("first", PAIR);
		addStandardMacro("last", PAIR);
		addStandardMacro("sep", PAIR);

		addStandardMacroWithoutModifiers("var", UNPAIRED);
		addStandardMacroWithoutModifiers("varType", UNPAIRED);
		addStandardMacroWithoutModifiers("templateType", UNPAIRED);

		addStandardMacro("assign", UNPAIRED);
		addStandardMacro("default", UNPAIRED);
		addStandardMacroWithoutModifiers("dump", UNPAIRED);
		addStandardMacroWithoutModifiers("debugbreak", UNPAIRED);
		addStandardMacroWithoutModifiers("l", UNPAIRED);
		addStandardMacroWithoutModifiers("r", UNPAIRED);

		addStandardMacro("_", PAIR);
		addStandardMacro("=", UNPAIRED);
		addStandardMacro("?", UNPAIRED);
		addStandardMacro("php", UNPAIRED);

		addStandardMacro("capture", PAIR);
		addStandardMacro("include", UNPAIRED);
		addStandardMacroWithoutModifiers("use", UNPAIRED);

		addStandardMacro("class", ATTR_ONLY);
		addStandardMacro("attr", ATTR_ONLY);

		// FormMacros
		addStandardMacroWithoutModifiers("form", PAIR);
		addStandardMacroWithoutModifiers("formContainer", PAIR);
		addStandardMacroWithoutModifiers("label", AUTO_EMPTY);
		addStandardMacroWithoutModifiers("input", UNPAIRED);
		addStandardMacro("name", ATTR_ONLY);
		addStandardMacroWithoutModifiers("inputError", UNPAIRED);

		// BlockMacros
		LatteMacro includeBlock = addStandardMacroWithoutModifiers("includeblock", UNPAIRED);
		includeBlock.deprecated = true;
		includeBlock.deprecatedMessage = "Macro includeblock is deprecated in Latte 2.4. Use import macro instead.";
		addStandardMacro("import", UNPAIRED);
		addStandardMacroWithoutModifiers("extends", UNPAIRED);
		addStandardMacro("layout", UNPAIRED);
		addStandardMacro("block", PAIR);
		addStandardMacro("define", PAIR);
		addStandardMacro("snippet", PAIR);
		addStandardMacro("snippetArea", PAIR);
		// addStandardMacro("ifset", PAIR);

		addStandardMacro("widget", UNPAIRED);
		addStandardMacro("control", UNPAIRED);
		addStandardMacro("href", ATTR_ONLY);

		addStandardMacro("plink", UNPAIRED);
		addStandardMacro("link", UNPAIRED);
		addStandardMacroWithoutModifiers("ifCurrent", PAIR);

		addStandardMacroWithoutModifiers("contentType", UNPAIRED);
		addStandardMacroWithoutModifiers("status", UNPAIRED);
	}

	private void initStandardModifiers() {
		addStandardModifier("truncate", "shortens the length preserving whole words", "truncate (length, append = '…')");
		addStandardModifier("substr", "returns part of the string", "substr (offset [, length])");
		addStandardModifier("trim", "strips whitespace or other characters from the beginning and end of the string", "trim (charset = mezery)");
		addStandardModifier("striptags", "removes HTML tags");
		addStandardModifier("strip", "removes whitespace");
		addStandardModifier("indent", "indents the text from left with number of tabs", "indent (level = 1, char = \"\\t\")");
		addStandardModifier("replace", "replaces all occurrences of the search string with the replacement", "replace (search, replace = '')");
		addStandardModifier("replaceRE", "replaces all occurrences according to regular expression", "replaceRE (pattern, replace = '')");
		addStandardModifier("padLeft", "completes the string to given length from left", "padLeft (length, pad = ' ')");
		addStandardModifier("padRight", "completes the string to given length from right", "padRight (length, pad = ' ')");
		addStandardModifier("repeat", "repeats the string", "repeat (count)");
		addStandardModifier("implode", "joins an array to a string", "implode (glue = '')");
		addStandardModifier("webalize", "adjusts the UTF-8 string to the shape used in the URL");
		addStandardModifier("breaklines", "inserts HTML line breaks before all newlines");
		addStandardModifier("reverse", "reverse an UTF-8 string or array");
		addStandardModifier("length", "returns length of a string or array");

		addStandardModifier("lower", "makes a string lower case");
		addStandardModifier("upper", "makes a string upper case");
		addStandardModifier("firstUpper", "makes the first letter upper case");
		addStandardModifier("capitalize", "lower case, the first letter of each word upper case");

		addStandardModifier("date", "formats date", "date (format)");
		addStandardModifier("number", "format number", "number (decimals = 0, decPoint = '.')");
		addStandardModifier("bytes", "formats size in bytes", "bytes (precision = 2)");
		addStandardModifier("dataStream", "Data URI protocol conversion", "dataStream (mimetype = detect)");

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

	/**
	 * @return variable with given name
	 */
	@Nullable
	public List<LatteVariableSettings> getVariables(Project project) {
		LatteSettings settings = getSettings(project);
		if (!settings.enableDefaultVariables || settings.variableSettings == null) {
			return null;
		}
		return new ArrayList<LatteVariableSettings>(settings.variableSettings);
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
					new LatteMacro(customMacro.getMacroName(), customMacro.getType(), customMacro.isAllowedModifiers())
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

	private LatteMacro addStandardMacroWithoutModifiers(String name, LatteMacro.Type type) {
		return addStandardMacro(name, type, false);
	}

	private LatteMacro addStandardMacro(String name, LatteMacro.Type type, boolean allowedModifiers) {
		LatteMacro macro = new LatteMacro(name, type, allowedModifiers);
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
