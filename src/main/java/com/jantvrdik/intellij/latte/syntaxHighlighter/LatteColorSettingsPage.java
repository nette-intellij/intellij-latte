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
		new AttributesDescriptor("Tag Name", LatteSyntaxHighlighter.MACRO_NAME),
		new AttributesDescriptor("Tag Filters", LatteSyntaxHighlighter.MACRO_MODIFIERS),
		new AttributesDescriptor("Tag Delimiters", LatteSyntaxHighlighter.MACRO_DELIMITERS),
		new AttributesDescriptor("Tag Comment", LatteSyntaxHighlighter.MACRO_COMMENT),
		new AttributesDescriptor("Attribute n:tag Name", LatteSyntaxHighlighter.HTML_NATTR_NAME),
		new AttributesDescriptor("Attribute n:tag Value", LatteSyntaxHighlighter.HTML_NATTR_VALUE),
		new AttributesDescriptor("PHP Variable", LatteSyntaxHighlighter.MACRO_ARGS_VAR),
		new AttributesDescriptor("PHP String", LatteSyntaxHighlighter.MACRO_ARGS_STRING),
		new AttributesDescriptor("PHP Number", LatteSyntaxHighlighter.MACRO_ARGS_NUMBER),
		new AttributesDescriptor("PHP Class", LatteSyntaxHighlighter.PHP_CLASS_NAME),
		new AttributesDescriptor("PHP Method, Function", LatteSyntaxHighlighter.PHP_METHOD),
		new AttributesDescriptor("PHP Keyword", LatteSyntaxHighlighter.PHP_KEYWORD),
		new AttributesDescriptor("PHP Property, Constant", LatteSyntaxHighlighter.PHP_IDENTIFIER),
		new AttributesDescriptor("PHP Cast", LatteSyntaxHighlighter.PHP_CAST),
		new AttributesDescriptor("PHP Type", LatteSyntaxHighlighter.PHP_TYPE),
		new AttributesDescriptor("PHP Null", LatteSyntaxHighlighter.PHP_NULL),
		new AttributesDescriptor("Content Type", LatteSyntaxHighlighter.PHP_CONTENT_TYPE),
		new AttributesDescriptor("Assignment Operator (=, +=, ...)", LatteSyntaxHighlighter.PHP_ASSIGNMENT_OPERATOR),
		new AttributesDescriptor("Logic Operator (&&, ||)", LatteSyntaxHighlighter.PHP_LOGIC_OPERATOR),
		new AttributesDescriptor("Other PHP Operators", LatteSyntaxHighlighter.PHP_OPERATOR),
		new AttributesDescriptor("Concatenation (.)", LatteSyntaxHighlighter.PHP_CONCATENATION),
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
			"{var $bar = (string) $object}\n" +
			"{$foo::$staticVariable::CONSTANT}\n\n" +
			"{count($arr)}\n" +
			"{foreach $data as $key => $value}\n" +
			"    {$key} {$value}\n" +
			"{/foreach}\n\n" +
			"{var $text = 'text' . 'suffix'}\n\n" +
			"{var $y = 25 - 46}\n\n" +
			"{if $var !== 25 && $val <= 'foo' || $y % 2 === 0}\n" +
			"    {$value|noescape}\n" +
			"{/if}";
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
