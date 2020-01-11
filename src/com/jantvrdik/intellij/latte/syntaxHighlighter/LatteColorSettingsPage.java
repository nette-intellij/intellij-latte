package com.jantvrdik.intellij.latte.syntaxHighlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class LatteColorSettingsPage implements ColorSettingsPage {
	private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[] {
		new AttributesDescriptor("Macro name", LatteSyntaxHighlighter.MACRO_NAME),
		new AttributesDescriptor("Macro filters", LatteSyntaxHighlighter.MACRO_MODIFIERS),
		new AttributesDescriptor("Macro delimiters", LatteSyntaxHighlighter.MACRO_DELIMITERS),
		new AttributesDescriptor("Macro comment", LatteSyntaxHighlighter.MACRO_COMMENT),
		new AttributesDescriptor("Attribute macro name", LatteSyntaxHighlighter.HTML_NATTR_NAME),
		new AttributesDescriptor("PHP variable", LatteSyntaxHighlighter.MACRO_ARGS_VAR),
		new AttributesDescriptor("PHP string", LatteSyntaxHighlighter.MACRO_ARGS_STRING),
		new AttributesDescriptor("PHP number", LatteSyntaxHighlighter.MACRO_ARGS_NUMBER),
		new AttributesDescriptor("PHP class", LatteSyntaxHighlighter.PHP_CLASS_NAME),
		new AttributesDescriptor("PHP method, function", LatteSyntaxHighlighter.PHP_METHOD),
		new AttributesDescriptor("PHP keyword", LatteSyntaxHighlighter.PHP_KEYWORD),
		new AttributesDescriptor("PHP property, constant", LatteSyntaxHighlighter.PHP_IDENTIFIER),
		new AttributesDescriptor("PHP type", LatteSyntaxHighlighter.PHP_TYPE),
		new AttributesDescriptor("PHP null", LatteSyntaxHighlighter.PHP_NULL),
		new AttributesDescriptor("Content type", LatteSyntaxHighlighter.PHP_CONTENT_TYPE),
	};

	@Nullable
	@Override
	public Icon getIcon() {
		return LatteIcons.FILE;
	}

	@NotNull
	@Override
	public SyntaxHighlighter getHighlighter() {
		return new LatteSyntaxHighlighter();
	}

	@NotNull
	@Override
	public String getDemoText() {
		return "{contentType text/html}\n" +
			"{* comment *}\n" +
			"{var $string = \"abc\", $number = 123}\n" +
			"<div class=\"perex\" n:if=\"$content\">\n" +
			"    {$content|truncate:250}\n" +
			"</div>\n\n" +
			"{varType \\Foo\\Bar|string|null $var}\n\n" +
			"{var $bar = $object->getFoo()->entity}\n" +
			"{$foo::$staticVariable::CONSTANT}\n\n" +
			"{count($arr)}\n" +
			"{foreach $data as $key => $value}\n" +
			"    {$key} {$value}\n" +
			"{/foreach}";
	}

	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
		return null;
	}

	@NotNull
	@Override
	public AttributesDescriptor[] getAttributeDescriptors() {
		return DESCRIPTORS;
	}

	@NotNull
	@Override
	public ColorDescriptor[] getColorDescriptors() {
		return ColorDescriptor.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return "Latte";
	}
}
