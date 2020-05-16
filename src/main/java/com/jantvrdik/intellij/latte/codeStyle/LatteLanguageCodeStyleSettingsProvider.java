package com.jantvrdik.intellij.latte.codeStyle;

import com.intellij.application.options.IndentOptionsEditor;
import com.jantvrdik.intellij.latte.LatteLanguage;
import org.jetbrains.annotations.NotNull;

import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.*;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public class LatteLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
	@NotNull
	@Override
	public Language getLanguage() {
		return LatteLanguage.INSTANCE;
	}

	@Override
	public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
		if (settingsType == SettingsType.SPACING_SETTINGS) {
			consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS");
			consumer.showStandardOptions("SPACE_AROUND_EQUALITY_OPERATORS");
			consumer.showStandardOptions("SPACE_AROUND_LOGICAL_OPERATORS");
			consumer.showStandardOptions("SPACE_AROUND_RELATIONAL_OPERATORS");
			consumer.showStandardOptions("SPACE_AROUND_BITWISE_OPERATORS");
			consumer.showStandardOptions("SPACE_AROUND_ADDITIVE_OPERATORS");
			consumer.showStandardOptions("SPACE_AROUND_MULTIPLICATIVE_OPERATORS");
			consumer.showStandardOptions("SPACE_AROUND_BITWISE_OPERATORS");
			consumer.showCustomOption(
					LatteCodeStyleSettings.class,
					"SPACE_AROUND_CONCATENATION",
					"Around concatenation (.)",
					"Others",
					LatteCodeStyleSettings.SPACE_AROUND_CONCATENATION
			);
			consumer.showStandardOptions("SPACE_AROUND_UNARY_OPERATOR");
			consumer.renameStandardOption("SPACE_AROUND_UNARY_OPERATOR", "Unary operators (++, --)");

		} else if (settingsType == SettingsType.BLANK_LINES_SETTINGS) {
			consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE");

		} else if (settingsType == SettingsType.INDENT_SETTINGS) {
			consumer.showStandardOptions("USE_TAB_CHARACTER");
			consumer.showStandardOptions("INDENT_SIZE");
			consumer.showStandardOptions("TAB_SIZE");
		}
	}

	@Override
	public Set<String> getSupportedFields(SettingsType type) {
		if (type == SettingsType.BLANK_LINES_SETTINGS) {
			return Collections.emptySet();
		}
		return super.getSupportedFields(type);
	}

	@Override
	public @Nullable IndentOptionsEditor getIndentOptionsEditor() {
		return new IndentOptionsEditor();
	}

	@Override
	public String getCodeSample(@NotNull SettingsType settingsType) {
		return "{contentType text/html}\n" +
				"{* comment *}\n" +
				"{var $string = \"abc\", $number = 123}\n\n" +
				"{foreach $data as $key => $value}\n" +
				"    {$key} {$value}\n" +
				"{/foreach}\n\n" +
				"{var $y = 25 - 46}\n\n" +
				"{for $x = 0; $x <= count($foo); $x++}\n" +
				"    {$x | $y}\n" +
				"{/foreach}\n\n" +
				"{var $text = 'text' . 'suffix'}\n\n" +
				"{if $var !== 25 && $val <= 'foo' || $y % 2 === 0}\n" +
				"    {$value|noescape}\n" +
				"{/if}";
	}
}