package com.jantvrdik.intellij.latte.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@State(
		name = "LattePluginSettings",
		storages = {
				@Storage("/latte.xml")
		}
)
public class LatteSettings implements PersistentStateComponent<LatteSettings> {

	public boolean enableXmlLoading = true;

	public boolean enableNette = true;

	public boolean enableNetteForms = true;

	public boolean enableDefaultVariables = true;

	public boolean enableCustomMacros = true;

	public boolean enableCustomModifiers = true;

	public boolean enableCustomFunctions = true;

	public List<LatteVariableSettings> variableSettings = new ArrayList<>();

	@Tag("customMacroSettings")
	public List<LatteTagSettings> tagSettings = new ArrayList<>();

	@Tag("customModifierSettings")
	public List<LatteFilterSettings> filterSettings = new ArrayList<>();

	@Tag("customFunctionSettings")
	public List<LatteFunctionSettings> functionSettings = new ArrayList<>();

	public static LatteSettings getInstance(Project project) {
		return ServiceManager.getService(project, LatteSettings.class);
	}

	public boolean isEnabledSourceVendor(LatteConfiguration.Vendor vendor) {
		if (vendor == LatteConfiguration.Vendor.NETTE_APPLICATION) {
			return enableNette;
		} else if (vendor == LatteConfiguration.Vendor.NETTE_FORMS) {
			return enableNetteForms;
		}
		return true;
	}

	@Nullable
	@Override
	public LatteSettings getState() {
		// add initializing here if needed
		return this;
	}

	@Override
	public void loadState(@NotNull LatteSettings settings) {
		XmlSerializerUtil.copyBean(settings, this);
	}
}

