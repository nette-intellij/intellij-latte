package com.jantvrdik.intellij.latte.config;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.jantvrdik.intellij.latte.config.LatteMacro.Type.*;

public class LatteConfiguration {

	/** globally available class instance */
	public static final LatteConfiguration INSTANCE = new LatteConfiguration();

	/** list of standard macros, indexed by macro name */
	private Map<String, LatteDefaultVariable> defaultVariables = new HashMap<String, LatteDefaultVariable>();

	/** list of standard macros, indexed by macro name */
	private Map<String, LatteMacro> standardMacros = new HashMap<String, LatteMacro>();

	/** list of custom  macros, indexed by project and macro name */
	private Map<Project, Map<String, LatteMacro>> customMacros = new HashMap<Project, Map<String, LatteMacro>>();

	/** list of custom  macros, indexed by project and macro name */
	private Map<Project, Map<String, LatteDefaultVariable>> customDefaultVariables = new HashMap<Project, Map<String, LatteDefaultVariable>>();

	public LatteConfiguration() {
		initStandardMacros();
		initDefaultVariables();
	}

	/**
	 * Initializes default variables, currently based on Nette 2.4.13.
	 */
	private void initDefaultVariables() {
		addDefaultVariable("control", "\\Nette\\Application\\UI\\Control");
		addDefaultVariable("basePath", "string");
		addDefaultVariable("baseUrl", "string");
		addDefaultVariable("baseUri", "string");
		addDefaultVariable("flashes", "array");
		addDefaultVariable("presenter", "\\Nette\\Application\\UI\\Presenter");
		addDefaultVariable("iterator", "\\Latte\\Runtime\\CachingIterator");
		addDefaultVariable("form", "\\Nette\\Application\\UI\\Form");
		addDefaultVariable("_form", "\\Nette\\Application\\UI\\Form");
		addDefaultVariable("user", "\\Nette\\Security\\User");
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
		addStandardMacro("if", PAIR);
		addStandardMacro("ifset", PAIR);
		addStandardMacro("else", UNPAIRED);
		addStandardMacro("elseif", UNPAIRED);
		addStandardMacro("elseifset", UNPAIRED);
		addStandardMacro("ifcontent", PAIR);

		addStandardMacro("switch", PAIR);
		addStandardMacro("case", UNPAIRED);

		addStandardMacro("foreach", PAIR);
		addStandardMacro("for", PAIR);
		addStandardMacro("while", PAIR);
		addStandardMacro("continueIf", UNPAIRED);
		addStandardMacro("breakIf", UNPAIRED);
		addStandardMacro("first", PAIR);
		addStandardMacro("last", PAIR);
		addStandardMacro("sep", PAIR);

		addStandardMacro("var", UNPAIRED);
		addStandardMacro("varType", UNPAIRED);
		addStandardMacro("templateType", UNPAIRED);
		addStandardMacro("assign", UNPAIRED);
		addStandardMacro("default", UNPAIRED);
		addStandardMacro("dump", UNPAIRED);
		addStandardMacro("debugbreak", UNPAIRED);
		addStandardMacro("l", UNPAIRED);
		addStandardMacro("r", UNPAIRED);

		addStandardMacro("_", PAIR);
		addStandardMacro("=", UNPAIRED);
		addStandardMacro("?", UNPAIRED);
		addStandardMacro("php", UNPAIRED);

		addStandardMacro("capture", PAIR);
		addStandardMacro("include", UNPAIRED);
		addStandardMacro("use", UNPAIRED);

		addStandardMacro("class", ATTR_ONLY);
		addStandardMacro("attr", ATTR_ONLY);

		// FormMacros
		addStandardMacro("form", PAIR);
		addStandardMacro("formContainer", PAIR);
		addStandardMacro("label", AUTO_EMPTY);
		addStandardMacro("input", UNPAIRED);
		addStandardMacro("name", ATTR_ONLY);
		addStandardMacro("inputError", UNPAIRED);

		// BlockMacros
		LatteMacro includeBlock = addStandardMacro("includeblock", UNPAIRED);
		includeBlock.deprecated = true;
		includeBlock.deprecatedMessage = "Macro includeblock is deprecated in Latte 2.4. Use import macro instead.";
		addStandardMacro("import", UNPAIRED);
		addStandardMacro("extends", UNPAIRED);
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
		addStandardMacro("ifCurrent", PAIR);

		addStandardMacro("contentType", UNPAIRED);
		addStandardMacro("status", UNPAIRED);
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
	 * @return variable with given name
	 */
	@Nullable
	public LatteDefaultVariable getVariable(Project project, String name) {
		name = LattePhpUtil.normalizePhpVariable(name);
		if (defaultVariables.containsKey(name)) {
			return defaultVariables.get(name);
		}

		Map<String, LatteDefaultVariable> customVariables = getCustomVariables(project);
		if (customVariables.containsKey(name)) {
			return customVariables.get(name);
		}

		return null;
	}

	/**
	 * @return variable with given name
	 */
	@Nullable
	public List<LatteDefaultVariable> getVariables(Project project) {
		List<LatteDefaultVariable> out = new ArrayList<>(defaultVariables.values());

		Map<String, LatteDefaultVariable> projectMacros = getCustomVariables(project);
		out.addAll(projectMacros.values());

		return out;
	}

	/**
	 * @return list of standard macros
	 */
	@NotNull
	public Map<String, LatteMacro> getStandardMacros() {
		return Collections.unmodifiableMap(standardMacros);
	}

	/**
	 * @return custom (project-specific) macros
	 */
	@NotNull
	public Map<String, LatteMacro> getCustomMacros(Project project) {
		if (!customMacros.containsKey(project)) {
			Map<String, LatteMacro> projectMacros = new HashMap<String, LatteMacro>();
			for (LatteMacro.Type type : LatteMacro.Type.values()) {
				for (String name : getCustomMacroList(project, type)) {
					projectMacros.put(name, new LatteMacro(name, type));
				}
			}
			customMacros.put(project, projectMacros);
		}
		return Collections.unmodifiableMap(customMacros.get(project));
	}

	/**
	 * @return custom (project-specific) macros
	 */
	@NotNull
	public Map<String, LatteDefaultVariable> getCustomVariables(Project project) {
		if (!customDefaultVariables.containsKey(project)) {
			Map<String, LatteDefaultVariable> projectVariables = new HashMap<String, LatteDefaultVariable>();
			for (LattePhpType type : getCustomVariableList(project)) {
				defaultVariables.put(type.getName(), new LatteDefaultVariable(type.getName(), type));
			}
			customDefaultVariables.put(project, projectVariables);
		}
		return Collections.unmodifiableMap(customDefaultVariables.get(project));
	}

	private void addStandardMacro(LatteMacro macro) {
		standardMacros.put(macro.name, macro);
	}

	private LatteMacro addStandardMacro(String name, LatteMacro.Type type) {
		LatteMacro macro = new LatteMacro(name, type);
		addStandardMacro(macro);
		return macro;
	}

	private void addDefaultVariable(LatteDefaultVariable variable) {
		defaultVariables.put(variable.name, variable);
	}

	private LatteDefaultVariable addDefaultVariable(String name, String phpType) {
		LatteDefaultVariable variable = new LatteDefaultVariable(name, new LattePhpType(name, phpType, false));
		addDefaultVariable(variable);
		return variable;
	}

	/**
	 * Registers a custom (project-specific) macro.
	 */
	public void addCustomMacro(Project project, LatteMacro macro) {
		getCustomMacros(project);
		customMacros.get(project).put(macro.name, macro);

		PropertiesComponent storage = PropertiesComponent.getInstance(project);
		String key = getStorageKey(macro.type);
		List<String> list = getCustomMacroList(project, macro.type);
		list.add(macro.name);
		storage.setValues(key, list.toArray(new String[list.size()]));
	}

	/**
	 * Registers a custom (project-specific) variable.
	 */
	public void addCustomVariable(Project project, LatteDefaultVariable defaultVariable) {
		getCustomMacros(project);
		customDefaultVariables.get(project).put(defaultVariable.name, defaultVariable);

		PropertiesComponent storage = PropertiesComponent.getInstance(project);
		List<LattePhpType> found = getCustomVariableList(project).stream()
				.filter(lattePhpType -> lattePhpType.getName().equals(defaultVariable.type.getName()))
				.collect(Collectors.toList());
		if (found.size() > 0) {
			return;
		}

		List<LattePhpType> list = getCustomVariableList(project);
		list.add(defaultVariable.type);

		storage.setValues(
			getStorageKey("variables"),
			list.stream().map(LattePhpType::toStringList).toArray(String[]::new)
		);
	}

	@NotNull
	private List<String> getCustomMacroList(Project project, LatteMacro.Type macroType) {
		PropertiesComponent storage = PropertiesComponent.getInstance(project);
		String key = getStorageKey(macroType);
		String[] values = storage.getValues(key);
		List<String> list = new ArrayList<String>();
		if (values != null) Collections.addAll(list, values);
		return list;
	}

	@NotNull
	private List<LattePhpType> getCustomVariableList(Project project) {
		PropertiesComponent storage = PropertiesComponent.getInstance(project);
		String key = getStorageKey("variables");
		String[] values = storage.getValues(key);
		List<String> list = new ArrayList<String>();
		if (values != null) Collections.addAll(list, values);

		List<LattePhpType> types = new ArrayList<LattePhpType>();
		for (String value : list) {
			types.add(LattePhpType.fromString(value));
		}
		return types;
	}

	@NotNull
	private String getStorageKey(LatteMacro.Type macroType) {
		String key;
		if (macroType == PAIR) key = "pair";
		else if (macroType == UNPAIRED) key = "unpaired";
		else if (macroType == ATTR_ONLY) key = "attr_only";
		else if (macroType == AUTO_EMPTY) key = "auto_empty";
		else key = "wtf";
		return getStorageKey(key);
	}

	@NotNull
	private String getStorageKey(String type) {
		String key = "latte.";
		key += type;
		return key;
	}

}
