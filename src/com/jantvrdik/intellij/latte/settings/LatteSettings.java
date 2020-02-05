package com.jantvrdik.intellij.latte.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@State(
		name = "LattePluginSettings",
		storages = {
				@Storage("/latte.xml")
		}
)
public class LatteSettings implements PersistentStateComponent<LatteSettings> {

	public boolean wasFirstInitialized = false;

	public boolean enableDefaultVariables = true;

	public boolean enableCustomMacros = true;

	public boolean enableCustomModifiers = true;

	public boolean enableCustomFunctions = true;

	public boolean codeCompletionEnabled = true;

	public List<LatteVariableSettings> variableSettings = new ArrayList<>();

	public List<LatteCustomMacroSettings> customMacroSettings = new ArrayList<>();

	public List<LatteCustomModifierSettings> customModifierSettings = new ArrayList<>();

	public List<LatteCustomFunctionSettings> customFunctionSettings = new ArrayList<>();

	public static LatteSettings getInstance(Project project) {
		return ServiceManager.getService(project, LatteSettings.class);
	}

	@Nullable
	@Override
	public LatteSettings getState() {
		if (!wasFirstInitialized) {
			if (variableSettings == null) {
				variableSettings = new ArrayList<>();
			}
			variableSettings.addAll(Arrays.asList(DefaultSettings.getDefaultVariables()));

			if (customFunctionSettings == null) {
				customFunctionSettings = new ArrayList<>();
			}
			customFunctionSettings.addAll(Arrays.asList(DefaultSettings.getDefaultCustomFunctions()));

			if (customMacroSettings == null) {
				customMacroSettings = new ArrayList<>();
			}
			customMacroSettings.addAll(Arrays.asList(DefaultSettings.getDefaultMacros()));

			wasFirstInitialized = true;
		}
		return this;
	}

	@Override
	public void loadState(@NotNull LatteSettings settings) {
		XmlSerializerUtil.copyBean(settings, this);
	}
}

