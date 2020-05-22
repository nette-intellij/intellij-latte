package com.jantvrdik.intellij.latte.config;

import com.intellij.notification.Notification;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.indexing.FileBasedIndex;
import com.jantvrdik.intellij.latte.indexes.LatteIndexExtension;
import com.jantvrdik.intellij.latte.indexes.LatteIndexUtil;
import com.jantvrdik.intellij.latte.indexes.LatteXmlFileData;
import com.jantvrdik.intellij.latte.settings.*;
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

    @Nullable
    private LatteXmlFileData initialize(XmlFile file) {
        XmlDocument document = file.getDocument();
        if (document == null || document.getRootTag() == null) {
            return null;
        }

        XmlTag configuration = document.getRootTag();
        if (configuration == null) {
            return null;
        }

        VendorResult vendor = getVendor(document);
        if (vendor == null) {
            return null;
        }

        LatteXmlFileData data = new LatteXmlFileData(vendor);
        XmlTag tags = configuration.findFirstSubTag("tags");
        if (tags != null) {
            loadTags(tags, data);
        }
        XmlTag filters = configuration.findFirstSubTag("filters");
        if (filters != null) {
            loadFilters(filters, data);
        }
        XmlTag variables = configuration.findFirstSubTag("variables");
        if (variables != null) {
            loadVariables(variables, data);
        }
        XmlTag functions = configuration.findFirstSubTag("functions");
        if (functions != null) {
            loadFunctions(functions, data);
        }

        applyXmlData(data);

        return data;
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

    private void removeAllWithVendor(VendorResult vendorResult) {
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
        initialize();
    }

    public LatteXmlFileData reinitialize(XmlFile file) {
        return initialize(file);
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

    private void loadTags(XmlTag customTags, LatteXmlFileData data) {
        for (XmlTag tag : customTags.findSubTags("tag")) {
            XmlAttribute name = tag.getAttribute("name");
            XmlAttribute type = tag.getAttribute("type");
            if (name == null || type == null || !LatteTagSettings.isValidType(type.getValue())) {
                continue;
            }

            LatteTagSettings macro = new LatteTagSettings(
                    name.getValue(),
                    LatteTagSettings.Type.valueOf(type.getValue()),
                    isTrue(tag, "allowedFilters"),
                    isTrue(tag, "hasParameters"),
                    isTrue(tag, "multiLine")
            );
            if (isTrue(tag, "deprecated")) {
                macro.setDeprecated(true);
                macro.setDeprecatedMessage(getTextValue(tag, "deprecatedMessage"));
            }

            data.addTag(macro);
        }
    }

    private void loadFilters(XmlTag customFilters, LatteXmlFileData data) {
        for (XmlTag filterData : customFilters.findSubTags("filter")) {
            XmlAttribute name = filterData.getAttribute("name");
            if (name == null) {
                continue;
            }

            LatteFilterSettings filter = new LatteFilterSettings(
                    name.getValue(),
                    getTextValue(filterData, "description"),
                    getTextValue(filterData, "arguments"),
                    getTextValue(filterData, "insertColons")
            );
            data.addFilter(filter);
        }
    }

    private void loadVariables(XmlTag customVariables, LatteXmlFileData data) {
        for (XmlTag filter : customVariables.findSubTags("variable")) {
            XmlAttribute name = filter.getAttribute("name");
            if (name == null ) {
                continue;
            }

            String varType = getTextValue(filter, "type");
            LatteVariableSettings variable = new LatteVariableSettings(
                    name.getValue(),
                    varType.length() == 0 ? "mixed" : varType
            );
            data.addVariable(variable);
        }
    }

    private void loadFunctions(XmlTag customFunctions, LatteXmlFileData data) {
        for (XmlTag filter : customFunctions.findSubTags("function")) {
            XmlAttribute name = filter.getAttribute("name");
            if (name == null) {
                continue;
            }

            String returnType = getTextValue(filter, "returnType");
            LatteFunctionSettings instance = new LatteFunctionSettings(
                    name.getValue(),
                    returnType.length() == 0 ? "mixed" : returnType,
                    getTextValue(filter, "arguments"),
                    getTextValue(filter, "description")
            );
            data.addFunction(instance);
        }
    }

    private boolean isTrue(XmlTag tag, String attrName) {
        return getTextValue(tag, attrName).equals("true");
    }

    private static String getTextValue(XmlTag tag, String attrName) {
        XmlAttribute attribute = tag.getAttribute(attrName);
        if (attribute == null || attribute.getValue() == null) {
            return "";
        }
        return attribute.getValue();
    }

    @Nullable
    public static VendorResult getVendor(@Nullable XmlDocument document) {
        if (document == null) {
            return null;
        }
        XmlTag configuration = document.getRootTag();
        if (configuration == null) {
            return null;
        }
        return determineVendor(getTextValue(configuration, "vendor"));
    }

    private static VendorResult determineVendor(String vendorText) {
        if (vendorText == null || vendorText.length() == 0) {
            return null;
        }

        String normalized = vendorText.toUpperCase();
        if (LatteConfiguration.isValidVendor(normalized)) {
            return new VendorResult(LatteConfiguration.Vendor.valueOf(normalized), vendorText);
        }
        return new VendorResult(LatteConfiguration.Vendor.OTHER, vendorText);
    }

    public static class VendorResult implements Serializable {
        public static VendorResult CUSTOM = new VendorResult(LatteConfiguration.Vendor.CUSTOM, "");

        public final String vendorName;
        public final LatteConfiguration.Vendor vendor;

        public VendorResult(LatteConfiguration.Vendor vendor, String vendorName) {
            this.vendor = vendor;
            this.vendorName = vendorName;
        }
    }

}
