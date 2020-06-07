package com.jantvrdik.intellij.latte.config;

import com.intellij.notification.Notification;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.indexing.FileBasedIndex;
import com.jantvrdik.intellij.latte.indexes.LatteIndexExtension;
import com.jantvrdik.intellij.latte.indexes.LatteIndexUtil;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileData;
import com.jantvrdik.intellij.latte.settings.*;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileDataFactory;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.*;

public class LatteFileConfiguration implements Serializable {
    final public static String FILE_NAME = "latte-intellij.xml";

    private static Map<Project, LatteFileConfiguration> instances = new HashMap<>();

    private Map<String, LatteTagSettings> tags = new HashMap<>();

    private Map<String, LatteFilterSettings> filters = new HashMap<>();

    private Map<String, LatteVariableSettings> variables = new HashMap<>();

    private Map<String, LatteFunctionSettings> functions = new HashMap<>();

    private List<LatteConfiguration.Vendor> vendors = new ArrayList<>();

    private Project project;

    private Notification notification = null;

    public LatteFileConfiguration(Project project) {
        this.project = project;
        initialize();
    }

    public static LatteFileConfiguration getInstance(Project project) {
        if (!instances.containsKey(project)) {
            instances.put(project, new LatteFileConfiguration(project));
        }
        return instances.get(project);
    }

    private void initialize() {
        if (!LatteSettings.getInstance(project).enableXmlLoading) {
            return;
        }

        if (ActionUtil.isDumbMode(project)) {
            if (notification == null || notification.isExpired() || LatteIndexUtil.isNotificationOutdated(notification)) {
                notification = LatteIndexUtil.notifyReparseFiles(project);
            }
            return;
        }

        Collection<String> val = FileBasedIndex.getInstance().getAllKeys(LatteIndexExtension.KEY, project);
        for (String key : val) {
            Collection<LatteXmlFileData> xmlFileData = FileBasedIndex.getInstance().getValues(
                    LatteIndexExtension.KEY,
                    key,
                    GlobalSearchScope.allScope(project)
            );
            for (LatteXmlFileData data : xmlFileData) {
                if (data == null) {
                    continue;
                }
                applyXmlData(data);
            }
        }
/*
        PsiFile[] files = FilenameIndex.getFilesByName(project, FILE_NAME, GlobalSearchScope.allScope(project));
        List<String> paths = new ArrayList<>();
        for (PsiFile file : files) {
            if (!(file instanceof XmlFile)) {
                continue;
            }

            LatteXmlFileData fileData = initialize((XmlFile) file);
            if (fileData != null) {
                paths.add(file.getVirtualFile().getPath());
            }
        }*/
    }

    public void applyXmlData(LatteXmlFileData data) {
        removeAllWithVendor(data.getVendorResult());

        tags.putAll(data.getTags());
        filters.putAll(data.getFilters());
        variables.putAll(data.getVariables());
        functions.putAll(data.getFunctions());

        if (!vendors.contains(data.getVendorResult().vendor)) {
            vendors.add(data.getVendorResult().vendor);
        }
    }

    public boolean hasVendor(LatteConfiguration.Vendor vendor) {
        return vendors.contains(vendor);
    }

    private void removeAllWithVendor(LatteXmlFileData.VendorResult vendorResult) {
        tags.entrySet().removeIf(entry -> entry.getValue().getVendorName().equals(vendorResult.vendorName));
        filters.entrySet().removeIf(entry -> entry.getValue().getVendorName().equals(vendorResult.vendorName));
        variables.entrySet().removeIf(entry -> entry.getValue().getVendorName().equals(vendorResult.vendorName));
        functions.entrySet().removeIf(entry -> entry.getValue().getVendorName().equals(vendorResult.vendorName));
    }

    public void reinitialize() {
        tags = new HashMap<>();
        filters = new HashMap<>();
        variables = new HashMap<>();
        functions = new HashMap<>();
        vendors = new ArrayList<>();
        initialize();
    }

    @Nullable
    public LatteXmlFileData reinitialize(XmlFile file) {
        LatteXmlFileData data = LatteXmlFileDataFactory.parse(file);
        if (data == null) {
            return null;
        }
        applyXmlData(data);
        return data;
    }

    public Map<String, LatteTagSettings> getTags() {
        return Collections.unmodifiableMap(tags);
    }

    public Map<String, LatteFilterSettings> getFilters() {
        return Collections.unmodifiableMap(filters);
    }

    public Map<String, LatteVariableSettings> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public Map<String, LatteFunctionSettings> getFunctions() {
        return Collections.unmodifiableMap(functions);
    }

}
