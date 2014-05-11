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
		new AttributesDescriptor("Macro variable", LatteSyntaxHighlighter.MACRO_ARGS_VAR),
		new AttributesDescriptor("Macro string", LatteSyntaxHighlighter.MACRO_ARGS_STRING),
		new AttributesDescriptor("Macro number", LatteSyntaxHighlighter.MACRO_ARGS_NUMBER),
		new AttributesDescriptor("Macro modifiers", LatteSyntaxHighlighter.MACRO_MODIFIERS),
		new AttributesDescriptor("Macro delimiters", LatteSyntaxHighlighter.MACRO_DELIMITERS),
		new AttributesDescriptor("Macro comment", LatteSyntaxHighlighter.MACRO_COMMENT),
		new AttributesDescriptor("Attribute macro name", LatteSyntaxHighlighter.HTML_NATTR_NAME),
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
		return "{* comment *}\n" +
			"{var $string = \"abc\", $number = 123}\n" +
			"<div class=\"perex\" n:if=\"$content\">\n" +
			"    {$content|truncate:250}\n" +
			"</div>";
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
