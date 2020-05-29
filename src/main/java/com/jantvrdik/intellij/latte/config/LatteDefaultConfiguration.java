package com.jantvrdik.intellij.latte.config;

import com.jantvrdik.intellij.latte.config.LatteConfiguration.Vendor;
import com.jantvrdik.intellij.latte.settings.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.jantvrdik.intellij.latte.settings.LatteTagSettings.Type.*;

public class LatteDefaultConfiguration {

	private static Map<Vendor, Map<String, LatteTagSettings>> tags = new HashMap<Vendor, Map<String, LatteTagSettings>>() {{

		put(Vendor.LATTE, new HashMap<String, LatteTagSettings>() {{
			put("_", (new LatteTagSettings("_", PAIR, true, "expression")).setVendor(Vendor.LATTE));
			put("=", (new LatteTagSettings("=", UNPAIRED, true, "expression")).setVendor(Vendor.LATTE));
			put("block", (new LatteTagSettings("block", PAIR, true, "name | $name", true)).setVendor(Vendor.LATTE));
			put("breakIf", (new LatteTagSettings("breakIf", UNPAIRED, "condition")).setVendor(Vendor.LATTE));
			put("capture", (new LatteTagSettings("capture", PAIR, true, "$variable", true)).setVendor(Vendor.LATTE));
			put("case", (new LatteTagSettings("case", UNPAIRED, "condition")).setVendor(Vendor.LATTE));
			put("catch", (new LatteTagSettings("catch", UNPAIRED)).setVendor(Vendor.LATTE));
			put("contentType", (new LatteTagSettings("contentType", UNPAIRED, "content-type")).setVendor(Vendor.LATTE));
			put("continueIf", (new LatteTagSettings("continueIf", UNPAIRED, "condition")).setVendor(Vendor.LATTE));
			put("debugbreak", (new LatteTagSettings("debugbreak", UNPAIRED, "expression")).setVendor(Vendor.LATTE));
			put("default", (new LatteTagSettings("default", UNPAIRED, "[type] $variable = expr, …")).setVendor(Vendor.LATTE));
			put("define", (new LatteTagSettings("define", PAIR, "name|$name , [type] $var, …", true)).setVendor(Vendor.LATTE));
			put("do", (new LatteTagSettings("do", UNPAIRED, "expression")).setVendor(Vendor.LATTE));
			put("dump", (new LatteTagSettings("dump", UNPAIRED, "expression")).setVendor(Vendor.LATTE));
			put("else", (new LatteTagSettings("else", UNPAIRED)).setVendor(Vendor.LATTE));
			put("elseif", (new LatteTagSettings("elseif", UNPAIRED, "condition")).setVendor(Vendor.LATTE));
			put("elseifset", (new LatteTagSettings("elseifset", UNPAIRED, "$var | #block")).setVendor(Vendor.LATTE));
			put("extends", (new LatteTagSettings("extends", UNPAIRED, "file | $file | none")).setVendor(Vendor.LATTE));
			put("first", (new LatteTagSettings("first", PAIR, "width")).setVendor(Vendor.LATTE));
			put("for", (new LatteTagSettings("for", PAIR, "initialization; condition; afterthought", true)).setVendor(Vendor.LATTE));
			put("foreach", (new LatteTagSettings("foreach", PAIR, true, "expression as [$key =>] $value", true)).setVendor(Vendor.LATTE));
			put("if", (new LatteTagSettings("if", PAIR, "condition")).setVendor(Vendor.LATTE));
			put("ifset", (new LatteTagSettings("ifset", PAIR, "$var | #block")).setVendor(Vendor.LATTE));
			put("import", (new LatteTagSettings("import", UNPAIRED, "file | $file")).setVendor(Vendor.LATTE));
			put("include", (new LatteTagSettings("include", UNPAIRED, true, "block | file | $file , var => value, …")).setVendor(Vendor.LATTE));
			put("includeblock", (new LatteTagSettings("includeblock", UNPAIRED, "file | $file")).setVendor(Vendor.LATTE));
			put("l", (new LatteTagSettings("l", UNPAIRED)).setVendor(Vendor.LATTE));
			put("last", (new LatteTagSettings("last", PAIR, "width")).setVendor(Vendor.LATTE));
			put("layout", (new LatteTagSettings("layout", UNPAIRED, "file | $file | none")).setVendor(Vendor.LATTE));
			put("class", (new LatteTagSettings("class", ATTR_ONLY)).setVendor(Vendor.LATTE));
			put("attr", (new LatteTagSettings("attr", ATTR_ONLY)).setVendor(Vendor.LATTE));
			put("ifcontent", (new LatteTagSettings("ifcontent", PAIR)).setVendor(Vendor.LATTE));
			put("php", (new LatteTagSettings("php", UNPAIRED, "expression")).setVendor(Vendor.LATTE));
			put("r", (new LatteTagSettings("r", UNPAIRED)).setVendor(Vendor.LATTE));
			put("sandbox", (new LatteTagSettings("sandbox", UNPAIRED, "block | file | $file , var => value, …")).setVendor(Vendor.LATTE));
			put("sep", (new LatteTagSettings("sep", PAIR, "width")).setVendor(Vendor.LATTE));
			put("snippet", (new LatteTagSettings("snippet", PAIR, "name | $name", true)).setVendor(Vendor.LATTE));
			put("snippetArea", (new LatteTagSettings("snippetArea", PAIR, "name", true)).setVendor(Vendor.LATTE));
			put("spaceless", (new LatteTagSettings("spaceless", PAIR)).setVendor(Vendor.LATTE));
			put("switch", (new LatteTagSettings("switch", PAIR, "expression", true)).setVendor(Vendor.LATTE));
			put("syntax", (new LatteTagSettings("syntax", PAIR, "off | double | latte")).setVendor(Vendor.LATTE));
			put("templatePrint", (new LatteTagSettings("templatePrint", UNPAIRED, "ClassName")).setVendor(Vendor.LATTE));
			put("templateType", (new LatteTagSettings("templateType", UNPAIRED, "ClassName")).setVendor(Vendor.LATTE));
			put("try", (new LatteTagSettings("try", PAIR)).setVendor(Vendor.LATTE));
			put("var", (new LatteTagSettings("var", UNPAIRED, "[type] $variable = expr, ...")).setVendor(Vendor.LATTE));
			put("varPrint", (new LatteTagSettings("varPrint", UNPAIRED, "all")).setVendor(Vendor.LATTE));
			put("varType", (new LatteTagSettings("varType", UNPAIRED, "type $variable")).setVendor(Vendor.LATTE));
			put("while", (new LatteTagSettings("while", PAIR, "condition", true)).setVendor(Vendor.LATTE));
			put("widget", (new LatteTagSettings("widget", PAIR, "block | file | $file , var => value, …")).setVendor(Vendor.LATTE));
			// @deprecated
			put("assign", (new LatteTagSettings("assign", UNPAIRED, "$variable = expr", "Tag {assign ...} is deprecated since Latte 2.1. Use {var ...} instead.")).setVendor(Vendor.LATTE));
			put("?", (new LatteTagSettings("?", UNPAIRED, "expression", "Tag {? ...} is deprecated in Latte 2.4. For variable definitions use {var ...} or {php ...} in other cases.")).setVendor(Vendor.LATTE));
		}});

		put(Vendor.NETTE_APPLICATION, new HashMap<String, LatteTagSettings>() {{
			put("cache", (new LatteTagSettings("cache", PAIR, "if => expr, key, …")).setVendor(Vendor.NETTE_APPLICATION));
			put("control", (new LatteTagSettings("control", UNPAIRED, "name[:part], arg, …")).setVendor(Vendor.NETTE_APPLICATION));
			put("link", (new LatteTagSettings("link", UNPAIRED, "destination , [param, …]")).setVendor(Vendor.NETTE_APPLICATION));
			put("href", (new LatteTagSettings("href", ATTR_ONLY, "destination , [param, …]")).setVendor(Vendor.NETTE_APPLICATION));
			put("nonce", (new LatteTagSettings("nonce", ATTR_ONLY)).setVendor(Vendor.NETTE_APPLICATION));
			put("plink", (new LatteTagSettings("plink", UNPAIRED, "destination , [param, …]")).setVendor(Vendor.NETTE_APPLICATION));
			// @deprecated
			put("ifCurrent", (new LatteTagSettings("ifCurrent", PAIR, "destination , [param, …]", "Tag {ifCurrent} is deprecated in Latte 2.6. Use custom function isLinkCurrent() instead.")).setVendor(Vendor.NETTE_APPLICATION));
		}});

		put(Vendor.NETTE_FORMS, new HashMap<String, LatteTagSettings>() {{
			put("form", (new LatteTagSettings("form", PAIR, "name | $name", true)).setVendor(Vendor.NETTE_FORMS));
			put("formContainer", (new LatteTagSettings("formContainer", PAIR, "name | $name", true)).setVendor(Vendor.NETTE_FORMS));
			put("formPrint", (new LatteTagSettings("formPrint", UNPAIRED, "name | $name")).setVendor(Vendor.NETTE_FORMS));
			put("input", (new LatteTagSettings("input", UNPAIRED, "name | $name | $control")).setVendor(Vendor.NETTE_FORMS));
			put("inputError", (new LatteTagSettings("inputError", UNPAIRED, "name | $name | $control")).setVendor(Vendor.NETTE_FORMS));
			put("label", (new LatteTagSettings("label", AUTO_EMPTY, "name | $name | $control")).setVendor(Vendor.NETTE_FORMS));
			put("name", (new LatteTagSettings("name", ATTR_ONLY, "name | $name | $control")).setVendor(Vendor.NETTE_FORMS));
		}});

	}};

	private static Map<Vendor, Map<String, LatteFilterSettings>> filters = new HashMap<Vendor, Map<String, LatteFilterSettings>>() {{

		put(Vendor.LATTE, new HashMap<String, LatteFilterSettings>() {{
			put("batch", (new LatteFilterSettings("batch", "breaks up an array into groups of arrays with the given number of items", ":(int $length, $item = null)", ":")).setVendor(Vendor.LATTE));
			put("breakLines", (new LatteFilterSettings("breaklines", "inserts <br> tags before all new line characters")).setVendor(Vendor.LATTE));
			put("bytes", (new LatteFilterSettings("bytes", "formats size in bytes", ":(int $precision = 2)")).setVendor(Vendor.LATTE));
			put("capitalize", (new LatteFilterSettings("capitalize", "lower case, the first letter of each word upper case")).setVendor(Vendor.LATTE));
			put("dataStream", (new LatteFilterSettings("dataStream", "Data URI protocol conversion", ":(string $mimetype = null)")).setVendor(Vendor.LATTE));
			put("date", (new LatteFilterSettings("date", "formats date", ":(string $format)", ":")).setVendor(Vendor.LATTE));
			// escape - @internal
			put("escapeCss", (new LatteFilterSettings("escapeCss", "escapes a string for use in CSS")).setVendor(Vendor.LATTE));
			put("escapeHtml", (new LatteFilterSettings("escapeHtml", "escapes a string for use in HTML")).setVendor(Vendor.LATTE));
			put("escapeHtmlComment", (new LatteFilterSettings("escapeHtmlComment", "escapes a string for use in HTML")).setVendor(Vendor.LATTE));
			put("escapeICal", (new LatteFilterSettings("escapeICal", "escapes a string for use in iCal")).setVendor(Vendor.LATTE));
			put("escapeJs", (new LatteFilterSettings("escapeJs", "converts to JSON and escapes for use in JavaScript")).setVendor(Vendor.LATTE));
			put("escapeUrl", (new LatteFilterSettings("escapeUrl", "escapes a parameter in URL")).setVendor(Vendor.LATTE));
			put("escapeXml", (new LatteFilterSettings("escapeXml", "escapes a string for use in XML")).setVendor(Vendor.LATTE));
			put("firstUpper", (new LatteFilterSettings("firstUpper", "makes the first letter upper case")).setVendor(Vendor.LATTE));
			put("checkUrl", (new LatteFilterSettings("checkUrl", "sanitizes string for use inside href attribute")).setVendor(Vendor.LATTE));
			put("implode", (new LatteFilterSettings("implode", "joins an array to a string", ":(string $separator = '')")).setVendor(Vendor.LATTE));
			put("indent", (new LatteFilterSettings("indent", "indents the text from left with number of tabs", ":(int $level = 1, string $char = \"\\t\")")).setVendor(Vendor.LATTE));
			put("length", (new LatteFilterSettings("length", "returns length of a string or array")).setVendor(Vendor.LATTE));
			put("lower", (new LatteFilterSettings("lower", "makes a string lower case")).setVendor(Vendor.LATTE));
			put("noescape", (new LatteFilterSettings("noescape", "prints a variable without escaping")).setVendor(Vendor.LATTE));
			put("nocheck", (new LatteFilterSettings("nocheck", "prevents automatic URL sanitization")).setVendor(Vendor.LATTE));
			put("noiterator", (new LatteFilterSettings("noiterator", "disables object $iterator creation")).setVendor(Vendor.LATTE));
			put("number", (new LatteFilterSettings("number", "format number", ":(int $decimals = 0 , string $decPoint = '.' , string $thousandsSep = ',')")).setVendor(Vendor.LATTE));
			put("padLeft", (new LatteFilterSettings("padLeft", "completes the string to given length from left", ":(int $length, string $pad = ' ')", ":")).setVendor(Vendor.LATTE));
			put("padRight", (new LatteFilterSettings("padRight", "completes the string to given length from right", ":(int $length, string $pad = ' ')", ":")).setVendor(Vendor.LATTE));
			put("repeat", (new LatteFilterSettings("repeat", "repeats the string", ":(int $count)", ":")).setVendor(Vendor.LATTE));
			put("replace", (new LatteFilterSettings("replace", "replaces all occurrences of the search string with the replacement", ":(string $search, string $replace = '')", ":")).setVendor(Vendor.LATTE));
			put("replaceRe", (new LatteFilterSettings("replaceRe", "replaces all occurrences according to regular expression", ":(string $pattern, string $replace = '')", ":")).setVendor(Vendor.LATTE));
			put("reverse", (new LatteFilterSettings("reverse", "reverse an UTF-8 string or array")).setVendor(Vendor.LATTE));
			put("strip", (new LatteFilterSettings("strip", "removes whitespace")).setVendor(Vendor.LATTE));
			put("stripHtml", (new LatteFilterSettings("stripHtml", "strips HTML tags and converts HTML entities to text")).setVendor(Vendor.LATTE));
			put("stripTags", (new LatteFilterSettings("stripTags", "strips HTML tags from the string")).setVendor(Vendor.LATTE));
			put("substr", (new LatteFilterSettings("substr", "returns part of the string", ":(int $offset, int $length = null)", ":")).setVendor(Vendor.LATTE));
			put("trim", (new LatteFilterSettings("trim", "strips whitespace or other characters from the beginning and end of the string", ":(string $characters = 'whitespace')")).setVendor(Vendor.LATTE));
			put("truncate", (new LatteFilterSettings("truncate", "shortens the length preserving whole words", ":(int $length, string $ellipsis = '…'\n)", ":")).setVendor(Vendor.LATTE));
			put("upper", (new LatteFilterSettings("upper", "makes a string upper case")).setVendor(Vendor.LATTE));
			put("webalize", (new LatteFilterSettings("webalize", "adjusts the UTF-8 string to the shape used in the URL")).setVendor(Vendor.LATTE));
		}});

	}};

	private static Map<Vendor, Map<String, LatteVariableSettings>> variables = new HashMap<Vendor, Map<String, LatteVariableSettings>>() {{

		put(Vendor.NETTE_APPLICATION, new HashMap<String, LatteVariableSettings>() {{
			put("control", (new LatteVariableSettings("control", "\\Nette\\Application\\UI\\Control")).setVendor(Vendor.NETTE_APPLICATION));
			put("basePath", (new LatteVariableSettings("basePath", "string")).setVendor(Vendor.NETTE_APPLICATION));
			put("baseUrl", (new LatteVariableSettings("baseUrl", "string")).setVendor(Vendor.NETTE_APPLICATION));
			put("baseUri", (new LatteVariableSettings("baseUri", "string")).setVendor(Vendor.NETTE_APPLICATION));
			put("flashes", (new LatteVariableSettings("flashes", "mixed[]")).setVendor(Vendor.NETTE_APPLICATION));
			put("presenter", (new LatteVariableSettings("presenter", "\\Nette\\Application\\UI\\Presenter")).setVendor(Vendor.NETTE_APPLICATION));
			put("iterator", (new LatteVariableSettings("iterator", "\\Latte\\Runtime\\CachingIterator")).setVendor(Vendor.NETTE_APPLICATION));
			put("form", (new LatteVariableSettings("form", "\\Nette\\Application\\UI\\Form")).setVendor(Vendor.NETTE_APPLICATION));
			put("user", (new LatteVariableSettings("user", "\\Nette\\Security\\User")).setVendor(Vendor.NETTE_APPLICATION));
		}});

	}};

	private static Map<Vendor, Map<String, LatteFunctionSettings>> functions = new HashMap<Vendor, Map<String, LatteFunctionSettings>>() {{

		put(Vendor.NETTE_APPLICATION, new HashMap<String, LatteFunctionSettings>() {{
			put("isLinkCurrent", (new LatteFunctionSettings("isLinkCurrent", "bool", "(string $destination = null, $args = [])")).setVendor(Vendor.NETTE_APPLICATION));
			put("isModuleCurrent", (new LatteFunctionSettings("isModuleCurrent", "bool", "(string $moduleName)")).setVendor(Vendor.NETTE_APPLICATION));
		}});

	}};

	private static Map<Integer, Vendor[]> vendors = new HashMap<>();

	public static Map<String, LatteTagSettings> getTags(Vendor vendor) {
		return tags.getOrDefault(vendor, Collections.emptyMap());
	}

	public static Vendor[] getTagVendors() {
		if (!vendors.containsKey(0)) {
			vendors.put(0, tags.keySet().toArray(new Vendor[0]));
		}
		return vendors.get(0);
	}

	public static Map<String, LatteFilterSettings> getFilters(Vendor vendor) {
		return filters.getOrDefault(vendor, Collections.emptyMap());
	}

	public static Vendor[] getFilterVendors() {
		if (!vendors.containsKey(1)) {
			vendors.put(1, tags.keySet().toArray(new Vendor[0]));
		}
		return vendors.get(1);
	}

	public static Map<String, LatteVariableSettings> getVariables(Vendor vendor) {
		return variables.getOrDefault(vendor, Collections.emptyMap());
	}

	public static Vendor[] getVariableVendors() {
		if (!vendors.containsKey(2)) {
			vendors.put(2, tags.keySet().toArray(new Vendor[0]));
		}
		return vendors.get(2);
	}

	public static Map<String, LatteFunctionSettings> getFunctions(Vendor vendor) {
		return functions.getOrDefault(vendor, Collections.emptyMap());
	}

	public static Vendor[] getFunctionVendors() {
		if (!vendors.containsKey(3)) {
			vendors.put(3, tags.keySet().toArray(new Vendor[0]));
		}
		return vendors.get(3);
	}

}
