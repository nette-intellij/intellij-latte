package com.jantvrdik.intellij.latte.settings.xml;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.settings.*;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LatteXmlFileDataFactory {
    @Nullable
    public static LatteXmlFileData parse(Project project, Path path) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(path.toString());
        if (virtualFile == null) {
            return null;
        }

        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (!(psiFile instanceof XmlFile)) {
            return null;
        }
        return parse((XmlFile) psiFile);
    }

    @Nullable
    public static LatteXmlFileData parse(XmlFile file) {
        XmlDocument document = file.getDocument();
        if (document == null || document.getRootTag() == null) {
            return null;
        }

        XmlTag configuration = document.getRootTag();
        if (configuration == null) {
            return null;
        }

        LatteXmlFileData.VendorResult vendor = getVendor(document);
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
        return data;
    }

    private static void loadTags(XmlTag customTags, LatteXmlFileData data) {
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
                    getTextValue(tag, "arguments"),
                    isTrue(tag, "multiLine"),
                    getTextValue(tag, "deprecatedMessage").trim(),
                    getArguments(tag)
            );

            data.addTag(macro);
        }
    }

    private static void loadFilters(XmlTag customFilters, LatteXmlFileData data) {
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

    private static void loadVariables(XmlTag customVariables, LatteXmlFileData data) {
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

    private static void loadFunctions(XmlTag customFunctions, LatteXmlFileData data) {
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

    private static boolean isTrue(XmlTag tag, String attrName) {
        return getTextValue(tag, attrName).equals("true");
    }

    private static String getTextValue(XmlTag tag, String attrName) {
        XmlAttribute attribute = tag.getAttribute(attrName);
        if (attribute == null || attribute.getValue() == null) {
            return "";
        }
        return attribute.getValue();
    }

    private static List<LatteArgumentSettings> getArguments(XmlTag tag) {
        XmlTag arguments = tag.findFirstSubTag("arguments");
        if (arguments == null) {
            return Collections.emptyList();
        }

        List<LatteArgumentSettings> out = new ArrayList<>();
        for (XmlTag argument : arguments.findSubTags("argument")) {
            XmlAttribute name = argument.getAttribute("name");
            XmlAttribute typesString = argument.getAttribute("types");
            if (name == null || typesString == null) {
                continue;
            }

            LatteArgumentSettings.Type[] types = LatteArgumentSettings.getTypes(typesString.getValue());
            if (types == null) {
                continue;
            }

            String validType = getTextValue(argument, "validType");
            LatteArgumentSettings instance = new LatteArgumentSettings(
                    name.getValue(),
                    types,
                    validType.length() == 0 ? "mixed" : validType,
                    isTrue(argument, "required"),
                    isTrue(argument, "repeatable")
            );
            out.add(instance);
        }
        return out;
    }

    @Nullable
    public static LatteXmlFileData.VendorResult getVendor(@Nullable XmlDocument document) {
        if (document == null) {
            return null;
        }
        XmlTag configuration = document.getRootTag();
        if (configuration == null) {
            return null;
        }
        return determineVendor(getTextValue(configuration, "vendor"));
    }

    private static LatteXmlFileData.VendorResult determineVendor(String vendorText) {
        if (vendorText == null || vendorText.length() == 0) {
            return null;
        }

        String normalized = vendorText.toUpperCase().replace("/", "_");
        if (LatteConfiguration.isValidVendor(normalized)) {
            return new LatteXmlFileData.VendorResult(LatteConfiguration.Vendor.valueOf(normalized), vendorText);
        }
        return new LatteXmlFileData.VendorResult(LatteConfiguration.Vendor.OTHER, vendorText);
    }
}
