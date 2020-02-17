package com.jantvrdik.intellij.latte.codeStyle;

import com.intellij.application.options.*;
import com.intellij.psi.codeStyle.*;
import com.jantvrdik.intellij.latte.LatteLanguage;
import org.jetbrains.annotations.*;

public class LatteCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
	@Override
	public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
		return new LatteCodeStyleSettings(settings);
	}

	@Nullable
	@Override
	public String getConfigurableDisplayName() {
		return "Latte";
	}

	@NotNull
	public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings, @NotNull CodeStyleSettings modelSettings) {
		return new CodeStyleAbstractConfigurable(settings, modelSettings, this.getConfigurableDisplayName()) {
			@Override
			protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
				return new LatteCodeStyleMainPanel(getCurrentSettings(), settings);
			}
		};
	}

	private static class LatteCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {
		public LatteCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
			super(LatteLanguage.INSTANCE, currentSettings, settings);
		}
	}
}