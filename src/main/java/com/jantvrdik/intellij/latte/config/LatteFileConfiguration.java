package com.jantvrdik.intellij.latte.config;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.jantvrdik.intellij.latte.settings.LatteCustomFunctionSettings;
import com.jantvrdik.intellij.latte.settings.LatteCustomMacroSettings;
import com.jantvrdik.intellij.latte.settings.LatteCustomModifierSettings;
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;

import java.util.*;

public class LatteFileConfiguration {
    final private static String FILE_NAME = "latte-intellij.xml";

    private static Map<Project, LatteFileConfiguration> instances = new HashMap<>();

    private Map<String, LatteCustomMacroSettings> tags = new HashMap<>();

    private Map<String, LatteCustomModifierSettings> filters = new HashMap<>();

    private Map<String, LatteVariableSettings> variables = new HashMap<>();

    private Map<String, LatteCustomFunctionSettings> functions = new HashMap<>();

    private Project project;

    public LatteFileConfiguration(Project project) {
        this.project = project;
        initMacros();
    }

    public static LatteFileConfiguration getInstance(Project project) {
        if (!instances.containsKey(project)) {
            instances.put(project, new LatteFileConfiguration(project));
        }
        return instances.get(project);
    }

    private void initMacros() {
        PsiFile[] files = FilenameIndex.getFilesByName(project, FILE_NAME, GlobalSearchScope.allScope(project));
        for (PsiFile file : files) {
            if (!(file instanceof XmlFile)) {
                continue;
            }

            XmlDocument document = ((XmlFile) file).getDocument();
            if (document == null || document.getRootTag() == null) {
                continue;
            }

            XmlTag configuration = document.getRootTag();
            if (configuration == null) {
                continue;
            }
            XmlTag tags = configuration.findFirstSubTag("tags");
            if (tags != null) {
                loadTags(tags);
            }
            XmlTag filters = configuration.findFirstSubTag("filters");
            if (filters != null) {
                loadFilters(filters);
            }
            XmlTag variables = configuration.findFirstSubTag("variables");
            if (variables != null) {
                loadVariables(variables);
            }
            XmlTag functions = configuration.findFirstSubTag("functions");
            if (functions != null) {
                loadFunctions(functions);
            }
        }
    }

    public void reinitialize() {
        tags = new HashMap<>();
        filters = new HashMap<>();
        variables = new HashMap<>();
        functions = new HashMap<>();
        initMacros();
    }

    public Map<String, LatteCustomMacroSettings> getTags() {
        return Collections.unmodifiableMap(tags);
    }

    public Map<String, LatteCustomModifierSettings> getFilters() {
        return Collections.unmodifiableMap(filters);
    }

    public Map<String, LatteVariableSettings> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public Map<String, LatteCustomFunctionSettings> getFunctions() {
        return Collections.unmodifiableMap(functions);
    }

    private void loadTags(XmlTag customTags) {
        for (XmlTag tag : customTags.findSubTags("tag")) {
            XmlAttribute name = tag.getAttribute("name");
            XmlAttribute type = tag.getAttribute("type");
            if (name == null || type == null || tags.containsKey(name.getValue()) || !LatteCustomMacroSettings.isValidType(type.getValue())) {
                continue;
            }

            LatteCustomMacroSettings macro = new LatteCustomMacroSettings(
                    name.getValue(),
                    LatteCustomMacroSettings.Type.valueOf(type.getValue()),
                    isTrue(tag, "allowedModifiers"),
                    isTrue(tag, "hasParameters"),
                    isTrue(tag, "multiLine")
            );

            if (isTrue(tag, "deprecated")) {
                macro.setDeprecated(true);
                macro.setDeprecatedMessage(getTextValue(tag, "deprecatedMessage"));
            }
            tags.put(macro.getMacroName(), macro);
        }
    }

    private void loadFilters(XmlTag customFilters) {
        for (XmlTag filter : customFilters.findSubTags("filter")) {
            XmlAttribute name = filter.getAttribute("name");
            if (name == null || filters.containsKey(name.getValue())) {
                continue;
            }

            LatteCustomModifierSettings modifierSettings = new LatteCustomModifierSettings(
                    name.getValue(),
                    getTextValue(filter, "description"),
                    getTextValue(filter, "arguments"),
                    getTextValue(filter, "insertColons")
            );
            filters.put(modifierSettings.getModifierName(), modifierSettings);
        }
    }

    private void loadVariables(XmlTag customVariables) {
        for (XmlTag filter : customVariables.findSubTags("variable")) {
            XmlAttribute name = filter.getAttribute("name");
            if (name == null || filters.containsKey(name.getValue())) {
                continue;
            }

            String varType = getTextValue(filter, "type");
            LatteVariableSettings instance = new LatteVariableSettings(
                    name.getValue(),
                    varType.length() == 0 ? "mixed" : varType
            );
            variables.put(instance.getVarName(), instance);
        }
    }

    private void loadFunctions(XmlTag customFunctions) {
        for (XmlTag filter : customFunctions.findSubTags("function")) {
            XmlAttribute name = filter.getAttribute("name");
            if (name == null || filters.containsKey(name.getValue())) {
                continue;
            }

            String returnType = getTextValue(filter, "returnType");
            LatteCustomFunctionSettings instance = new LatteCustomFunctionSettings(
                    name.getValue(),
                    returnType.length() == 0 ? "mixed" : returnType,
                    getTextValue(filter, "arguments"),
                    getTextValue(filter, "description")
            );
            functions.put(instance.getFunctionName(), instance);
        }
    }

    private boolean isTrue(XmlTag tag, String attrName) {
        return getTextValue(tag, attrName).equals("true");
    }

    private String getTextValue(XmlTag tag, String attrName) {
        XmlAttribute attribute = tag.getAttribute(attrName);
        if (attribute == null || attribute.getValue() == null) {
            return "";
        }
        return attribute.getValue();
    }

}
