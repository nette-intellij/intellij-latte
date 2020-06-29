package com.jantvrdik.intellij.latte.config;

import com.intellij.notification.Notification;
import com.intellij.openapi.project.Project;
import com.jantvrdik.intellij.latte.config.LatteConfiguration.Vendor;
import com.jantvrdik.intellij.latte.indexes.LatteIndexUtil;
import com.jantvrdik.intellij.latte.settings.*;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileData;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileDataFactory;
import com.jantvrdik.intellij.latte.utils.LatteIdeHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LatteDefaultConfiguration {

	private static String[] sourceFiles = new String[] {"Latte.xml", "NetteApplication.xml", "NetteForms.xml"};

	private static Map<Project, LatteDefaultConfiguration> instances = new HashMap<>();

	private Map<Vendor, LatteXmlFileData> xmlData = new HashMap<>();

	@NotNull
	private final Project project;

	@Nullable
	private Notification notification = null;

	public LatteDefaultConfiguration(@NotNull Project project) {
		this.project = project;
		reinitialize();
	}

	public void reinitialize() {
		xmlData = new HashMap<>();
		for (String sourceFile : sourceFiles) {
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
				if (notification == null || notification.isExpired() || LatteIndexUtil.isNotificationOutdated(notification)) {
					notification = LatteIndexUtil.notifyDefaultReparse(project);
				}
			}
		}
	}

	public static LatteDefaultConfiguration getInstance(@NotNull Project project) {
		if (!instances.containsKey(project)) {
			instances.put(project, new LatteDefaultConfiguration(project));
		}
		return instances.get(project);
	}

	public Vendor[] getVendors() {
		return xmlData.keySet().toArray(new Vendor[0]);
	}

	public Map<String, LatteTagSettings> getTags(Vendor vendor) {
		LatteXmlFileData data = xmlData.getOrDefault(vendor, null);
		return data != null ? data.getTags() : Collections.emptyMap();
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

}
