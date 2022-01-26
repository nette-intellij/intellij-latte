package com.jantvrdik.intellij.latte.config;

import com.intellij.openapi.project.Project;
import com.jantvrdik.intellij.latte.config.LatteConfiguration.Vendor;
import com.jantvrdik.intellij.latte.settings.*;
import com.jantvrdik.intellij.latte.settings.LatteTagSettings.*;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileData;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileDataFactory;
import com.jantvrdik.intellij.latte.utils.LatteIdeHelper;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public class LatteDefaultConfiguration {

	public static Map<String, Vendor> sourceFiles = new HashMap<String, Vendor>(){{
		put("Latte.xml", Vendor.LATTE);
		put("NetteApplication.xml", Vendor.NETTE_APPLICATION);
		put("NetteForms.xml", Vendor.NETTE_FORMS);
	}};

	private static final Map<Project, LatteDefaultConfiguration> instances = new HashMap<>();

	private Map<Vendor, LatteXmlFileData> xmlData = new HashMap<>();
	private final Map<Vendor, Map<String, LatteTagSettings>> defaultTags = new HashMap<>();

	private final @NotNull Project project;

	private final boolean disableLoading;

	public LatteDefaultConfiguration(@NotNull Project project, boolean disableLoading) {
		this.project = project;
		this.disableLoading = disableLoading;
		loadDefaults();
		reinitialize();
	}

	public boolean reinitialize() {
		int oldHash = hashCode();
		xmlData = new HashMap<>();
		if (disableLoading) {
			return false;
		}

		LatteIdeHelper.saveFileToProjectTemp(project, "xmlSources/Latte.dtd");
		for (String sourceFile : sourceFiles.keySet()) {
			Path path = LatteIdeHelper.saveFileToProjectTemp(project, "xmlSources/" + sourceFile);
			if (path == null) {
				continue;
			}

			LatteXmlFileData data = LatteXmlFileDataFactory.parse(project, path);
			if (data != null) {
				LatteXmlFileData.VendorResult vendorResult = data.getVendorResult();
				xmlData.remove(vendorResult.vendor);
				xmlData.put(vendorResult.vendor, data);

			} else if (LatteIdeHelper.holdsReadLock()) {
				LatteReparseUtil.getInstance(project).reinitializeDefault();
			}
		}
		return oldHash == hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LatteDefaultConfiguration that = (LatteDefaultConfiguration) o;
		return Objects.equals(xmlData, that.xmlData) && project.equals(that.project);
	}

	@Override
	public int hashCode() {
		return Objects.hash(xmlData, project);
	}

	public static LatteDefaultConfiguration getInstance(@NotNull Project project) {
		return getInstance(project, false);
	}

	public static LatteDefaultConfiguration getInstance(@NotNull Project project, boolean disableLoading) {
		if (!instances.containsKey(project)) {
			instances.put(project, new LatteDefaultConfiguration(project, disableLoading));
		}
		return instances.get(project);
	}

	public Vendor[] getVendors() {
		return xmlData.keySet().toArray(new Vendor[0]);
	}

	public Map<String, LatteTagSettings> getTags(Vendor vendor) {
		return defaultTags.get(vendor);
	}

	public Map<String, LatteFilterSettings> getFilters(Vendor vendor) {
		LatteXmlFileData data = xmlData.getOrDefault(vendor, null);
		return data != null ? data.getFilters() : Collections.emptyMap();
	}

	public Map<String, LatteVariableSettings> getVariables(Vendor vendor) {
		LatteXmlFileData data = xmlData.getOrDefault(vendor, null);
		return data != null ? data.getVariables() : Collections.emptyMap();
	}

	public Map<String, LatteFunctionSettings> getFunctions(Vendor vendor) {
		LatteXmlFileData data = xmlData.getOrDefault(vendor, null);
		return data != null ? data.getFunctions() : Collections.emptyMap();
	}

	private void loadDefaults() {
		loadDefaultLatteTags();
		loadDefaultNetteApplicationTags();
		loadDefaultNetteFormsTags();
	}

	private void loadDefaultLatteTags() {
		addLatteTag(filtersTag("_", Type.AUTO_EMPTY, requiredArgument("expression", "string", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(filtersTag("=", Type.UNPAIRED, requiredArgument("expression", "string", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(filtersMultiTag("block", Type.AUTO_EMPTY, requiredArgument("name", "string", LatteArgumentSettings.Type.PHP_EXPRESSION, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.PHP_IDENTIFIER)));
		addLatteTag(tag("breakIf", Type.UNPAIRED, requiredArgument("condition", "bool", LatteArgumentSettings.Type.PHP_CONDITION)));
		addLatteTag(filtersMultiTag("capture", Type.PAIR, requiredArgument("variable", LatteArgumentSettings.Type.VARIABLE_DEFINITION)));
		addLatteTag(tag("case", Type.UNPAIRED, requiredRepeatableArgument("condition", "bool", LatteArgumentSettings.Type.PHP_CONDITION)));
		addLatteTag(tag("catch", Type.UNPAIRED, requiredArgument("condition", "bool", LatteArgumentSettings.Type.PHP_CONDITION)));
		addLatteTag(tag("contentType", Type.UNPAIRED, requiredArgument("content-type", "string", LatteArgumentSettings.Type.CONTENT_TYPE)));
		addLatteTag(tag("continueIf", Type.UNPAIRED, requiredArgument("condition", "bool", LatteArgumentSettings.Type.PHP_CONDITION)));
		addLatteTag(tag("debugbreak", Type.UNPAIRED, requiredArgument("expression", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(tag("default", Type.UNPAIRED, requiredRepeatableArgument("variable", LatteArgumentSettings.Type.VARIABLE_DEFINITION_EXPRESSION)));
		addLatteTag(tag("parameters", Type.UNPAIRED, requiredRepeatableArgument("variable", LatteArgumentSettings.Type.VARIABLE_DEFINITION_EXPRESSION)));
		addLatteTag(multiTag("define", Type.PAIR, requiredArgument("name", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.PHP_EXPRESSION), repeatableArgument("variable", LatteArgumentSettings.Type.VARIABLE_DEFINITION_ITEM)));
		addLatteTag(tag("do", Type.UNPAIRED, requiredArgument("expression", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(tag("dump", Type.UNPAIRED, requiredArgument("expression", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(tag("else", Type.UNPAIRED_ATTR));
		addLatteTag(tag("elseif", Type.UNPAIRED, requiredArgument("condition", "bool", LatteArgumentSettings.Type.PHP_CONDITION)));
		addLatteTag(tag("elseifset", Type.UNPAIRED, requiredArgument("var", "string", LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.BLOCK)));
		addLatteTag(tag("extends", Type.UNPAIRED, requiredArgument("file", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.PHP_EXPRESSION, LatteArgumentSettings.Type.NONE)));
		addLatteTag(tag("first", Type.UNPAIRED, requiredArgument("width", "int", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(multiTag("for", Type.PAIR, "initialization; condition; afterthought"));
		addLatteTag(filtersMultiTag("foreach", Type.PAIR, "expression as [$key =>] $value"));
		addLatteTag(tag("if", Type.PAIR, requiredArgument("condition", "bool", LatteArgumentSettings.Type.PHP_CONDITION)));
		addLatteTag(tag("ifset", Type.PAIR, requiredArgument("var", "string", LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.BLOCK, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(tag("import", Type.UNPAIRED, requiredArgument("file", "string", LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(filtersTag("include", Type.UNPAIRED, requiredArgument("file", "string", LatteArgumentSettings.Type.BLOCK, LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.PHP_EXPRESSION), repeatableArgument("arguments", LatteArgumentSettings.Type.KEY_VALUE)));
		addLatteTag(tag("includeblock", Type.UNPAIRED, requiredArgument("file", "string", LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(tag("l", Type.UNPAIRED));
		addLatteTag(tag("last", Type.PAIR, requiredArgument("width", "int", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(tag("layout", Type.UNPAIRED, requiredArgument("file", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.PHP_EXPRESSION, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.NONE)));
		addLatteTag(tag("class", Type.ATTR_ONLY, "class"));
		addLatteTag(tag("attr", Type.ATTR_ONLY, "attr"));
		addLatteTag(tag("ifcontent", Type.ATTR_ONLY));
		addLatteTag(tag("php", Type.UNPAIRED, requiredArgument("expression", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(tag("r", Type.UNPAIRED));
		addLatteTag(tag("sandbox", Type.UNPAIRED, requiredArgument("file", "string", LatteArgumentSettings.Type.PHP_EXPRESSION, LatteArgumentSettings.Type.BLOCK, LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE), repeatableArgument("key-value", LatteArgumentSettings.Type.KEY_VALUE)));
		addLatteTag(tag("sep", Type.PAIR, argument("width", "int", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(multiTag("snippet", Type.PAIR, argument("name", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(multiTag("snippetArea", Type.PAIR, requiredArgument("name", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(tag("spaceless", Type.PAIR));
		addLatteTag(multiTag("switch", Type.PAIR, argument("expression", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(multiTag("syntax", Type.PAIR, "off | double | latte"));
		addLatteTag(tag("templatePrint", Type.UNPAIRED, argument("class-name", LatteArgumentSettings.Type.PHP_CLASS_NAME)));
		addLatteTag(tag("templateType", Type.UNPAIRED, requiredArgument("class-name", LatteArgumentSettings.Type.PHP_CLASS_NAME)));
		addLatteTag(tag("try", Type.PAIR));
		addLatteTag(tag("rollback", Type.UNPAIRED));
		addLatteTag(tag("tag", Type.ATTR_ONLY, requiredRepeatableArgument("expression", "string", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(tag("ifchanged", Type.PAIR, requiredRepeatableArgument("expression", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addLatteTag(tag("skipIf", Type.UNPAIRED, requiredArgument("condition", "bool", LatteArgumentSettings.Type.PHP_CONDITION)));
		addLatteTag(tag("var", Type.UNPAIRED, requiredRepeatableArgument("variable", LatteArgumentSettings.Type.VARIABLE_DEFINITION_EXPRESSION)));
		addLatteTag(tag("trace", Type.UNPAIRED));
		addLatteTag(tag("varPrint", Type.UNPAIRED, "all"));
		addLatteTag(tag("varType", Type.UNPAIRED, requiredArgument("file", LatteArgumentSettings.Type.PHP_TYPE), requiredArgument("variable", LatteArgumentSettings.Type.VARIABLE_DEFINITION)));
		addLatteTag(multiTag("while", Type.PAIR, requiredArgument("condition", "bool", LatteArgumentSettings.Type.PHP_CONDITION)));
		addLatteTag(multiTag("iterateWhile", Type.PAIR));
		addLatteTag(multiTag("embed", Type.PAIR, requiredArgument("file", "string", LatteArgumentSettings.Type.BLOCK_USAGE, LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.PHP_EXPRESSION), repeatableArgument("key-value", LatteArgumentSettings.Type.KEY_VALUE)));
	}

	private void loadDefaultNetteApplicationTags() {
		addNetteTag(tag("cache", Type.PAIR, "if => expr, key, …", requiredArgument("name[:part]", "string", LatteArgumentSettings.Type.KEY_VALUE), repeatableArgument("arguments", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addNetteTag(tag("control", Type.UNPAIRED, requiredArgument("name[:part]", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.PHP_EXPRESSION), repeatableArgument("arguments", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addNetteTag(tag("link", Type.UNPAIRED, requiredArgument("destination", "string", LatteArgumentSettings.Type.LINK_DESTINATION, LatteArgumentSettings.Type.PHP_EXPRESSION), repeatableArgument("arguments", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addNetteTag(tag("href", Type.ATTR_ONLY, requiredArgument("destination", "string", LatteArgumentSettings.Type.LINK_DESTINATION, LatteArgumentSettings.Type.PHP_EXPRESSION), repeatableArgument("arguments", LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addNetteTag(tag("nonce", Type.ATTR_ONLY));
		addNetteTag(tag("plink", Type.UNPAIRED, requiredArgument("destination", "string", LatteArgumentSettings.Type.LINK_DESTINATION, LatteArgumentSettings.Type.PHP_EXPRESSION), repeatableArgument("arguments", LatteArgumentSettings.Type.PHP_EXPRESSION)));

		LatteTagSettings ifCurrent = tag("ifCurrent", Type.PAIR, requiredArgument("destination", "string", LatteArgumentSettings.Type.LINK_DESTINATION, LatteArgumentSettings.Type.PHP_EXPRESSION), repeatableArgument("arguments", LatteArgumentSettings.Type.PHP_EXPRESSION));
		ifCurrent.setDeprecated(true);
		ifCurrent.setDeprecatedMessage("Tag {ifCurrent} is deprecated in Latte 2.6. Use custom function isLinkCurrent() instead.");
		addNetteTag(ifCurrent);
	}

	private void loadDefaultNetteFormsTags() {
		addFormsTag(multiTag("form", Type.PAIR, requiredArgument("name", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addFormsTag(multiTag("formContainer", Type.PAIR, requiredArgument("name", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addFormsTag(tag("formPrint", Type.UNPAIRED, requiredArgument("name", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addFormsTag(tag("input", Type.UNPAIRED, requiredArgument("name", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.CONTROL, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addFormsTag(tag("inputError", Type.UNPAIRED, requiredArgument("name", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.CONTROL, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addFormsTag(tag("label", Type.AUTO_EMPTY, requiredArgument("name", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.CONTROL, LatteArgumentSettings.Type.PHP_EXPRESSION)));
		addFormsTag(tag("name", Type.ATTR_ONLY, requiredArgument("name", "string", LatteArgumentSettings.Type.PHP_IDENTIFIER, LatteArgumentSettings.Type.VARIABLE, LatteArgumentSettings.Type.CONTROL, LatteArgumentSettings.Type.PHP_EXPRESSION)));
	}

	private void addLatteTag(LatteTagSettings tag) {
		addTag(Vendor.LATTE, tag);
	}

	private void addNetteTag(LatteTagSettings tag) {
		addTag(Vendor.NETTE_APPLICATION, tag);
	}

	private void addFormsTag(LatteTagSettings tag) {
		addTag(Vendor.NETTE_FORMS, tag);
	}

	private void addTag(Vendor vendor, LatteTagSettings tag) {
		Map<String, LatteTagSettings> map;
		if (defaultTags.containsKey(vendor)) {
			map = defaultTags.get(vendor);
		} else {
			map = new HashMap<>();
			defaultTags.put(vendor, map);
		}
		map.put(tag.getMacroName(), tag);
	}

	private LatteTagSettings filtersTag(String macroName, Type macroType, LatteArgumentSettings ...argumentSettings) {
		return new LatteTagSettings(macroName, macroType, true, false, Vendor.LATTE, Arrays.asList(argumentSettings));
	}

	private LatteTagSettings tag(String macroName, Type macroType, LatteArgumentSettings ...argumentSettings) {
		return new LatteTagSettings(macroName, macroType, false, false, Vendor.LATTE, Arrays.asList(argumentSettings));
	}

	private LatteTagSettings tag(String macroName, Type macroType, String arguments) {
		return new LatteTagSettings(macroName, macroType, false, false, Vendor.LATTE, arguments);
	}

	private LatteTagSettings tag(String macroName, Type macroType, String arguments, LatteArgumentSettings ...argumentSettings) {
		return new LatteTagSettings(macroName, macroType, false, false, Vendor.LATTE, arguments, Arrays.asList(argumentSettings));
	}

	private LatteTagSettings filtersMultiTag(String macroName, Type macroType, LatteArgumentSettings ...argumentSettings) {
		return new LatteTagSettings(macroName, macroType, true, true, Vendor.LATTE, Arrays.asList(argumentSettings));
	}

	private LatteTagSettings filtersMultiTag(String macroName, Type macroType, String arguments) {
		return new LatteTagSettings(macroName, macroType, true, true, Vendor.LATTE, arguments);
	}

	private LatteTagSettings multiTag(String macroName, Type macroType, LatteArgumentSettings ...argumentSettings) {
		return new LatteTagSettings(macroName, macroType, false, true, Vendor.LATTE, Arrays.asList(argumentSettings));
	}

	private LatteTagSettings multiTag(String macroName, Type macroType, String arguments) {
		return new LatteTagSettings(macroName, macroType, false, true, Vendor.LATTE, arguments);
	}

	private LatteArgumentSettings argument(String name, String validType, LatteArgumentSettings.Type ...types) {
		return new LatteArgumentSettings(name, types, validType, false, false);
	}

	private LatteArgumentSettings argument(String name, LatteArgumentSettings.Type ...types) {
		return new LatteArgumentSettings(name, types, "", false, false);
	}

	private LatteArgumentSettings requiredArgument(String name, String validType, LatteArgumentSettings.Type ...types) {
		return new LatteArgumentSettings(name, types, validType, true, false);
	}

	private LatteArgumentSettings requiredArgument(String name, LatteArgumentSettings.Type ...types) {
		return new LatteArgumentSettings(name, types, "", true, false);
	}

	private LatteArgumentSettings repeatableArgument(String name, LatteArgumentSettings.Type ...types) {
		return new LatteArgumentSettings(name, types, "", false, true);
	}

	private LatteArgumentSettings requiredRepeatableArgument(String name, String validType, LatteArgumentSettings.Type ...types) {
		return new LatteArgumentSettings(name, types, validType, true, true);
	}

	private LatteArgumentSettings requiredRepeatableArgument(String name, LatteArgumentSettings.Type ...types) {
		return new LatteArgumentSettings(name, types, "", true, true);
	}

}
