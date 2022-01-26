package com.jantvrdik.intellij.latte.settings.xml;

import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.settings.LatteFunctionSettings;
import com.jantvrdik.intellij.latte.settings.LatteTagSettings;
import com.jantvrdik.intellij.latte.settings.LatteFilterSettings;
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LatteXmlFileData implements Serializable {
    final private LatteXmlFileData.VendorResult vendorResult;

    private final Map<String, LatteTagSettings> tags = new HashMap<>();
    private final Map<String, LatteFilterSettings> filters = new HashMap<>();
    private final Map<String, LatteVariableSettings> variables = new HashMap<>();
    private final Map<String, LatteFunctionSettings> functions = new HashMap<>();

    public LatteXmlFileData(LatteXmlFileData.VendorResult vendorResult) {
        this.vendorResult = vendorResult;
    }

    public LatteXmlFileData.VendorResult getVendorResult() {
        return vendorResult;
    }

    public void removeTag(String name) {
        tags.remove(name);
    }

    public void addTag(LatteTagSettings customMacroSettings) {
        removeTag(customMacroSettings.getMacroName());

        tags.put(customMacroSettings.getMacroName(), customMacroSettings);
    }

    public Map<String, LatteTagSettings> getTags() {
        tags.values().forEach(tag -> {
            tag.setVendor(vendorResult.vendor);
            tag.setVendorName(vendorResult.vendorName);
        });
        return Collections.unmodifiableMap(tags);
    }

    public void removeFilter(String name) {
        filters.remove(name);
    }

    public void addFilter(LatteFilterSettings filter) {
        removeFilter(filter.getModifierName());
        filters.put(filter.getModifierName(), filter);
    }

    public Map<String, LatteFilterSettings> getFilters() {
        tags.values().forEach(filter -> {
            filter.setVendor(vendorResult.vendor);
            filter.setVendorName(vendorResult.vendorName);
        });
        return Collections.unmodifiableMap(filters);
    }

    public void removeVariable(String name) {
        variables.remove(name);
    }

    public void addVariable(LatteVariableSettings variable) {
        removeVariable(variable.getVarName());
        variables.put(variable.getVarName(), variable);
    }

    public Map<String, LatteVariableSettings> getVariables() {
        variables.values().forEach(variable -> {
            variable.setVendor(vendorResult.vendor);
            variable.setVendorName(vendorResult.vendorName);
        });
        return Collections.unmodifiableMap(variables);
    }

    public void removeFunction(String name) {
        functions.remove(name);
    }

    public void addFunction(LatteFunctionSettings function) {
        removeFunction(function.getFunctionName());
        functions.put(function.getFunctionName(), function);
    }

    public Map<String, LatteFunctionSettings> getFunctions() {
        functions.values().forEach(function -> {
            function.setVendor(vendorResult.vendor);
            function.setVendorName(vendorResult.vendorName);
        });
        return Collections.unmodifiableMap(functions);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.vendorResult.vendor.toString())
                .append(this.vendorResult.vendorName)
                .append(this.tags)
                .append(this.filters)
                .append(this.variables)
                .append(this.functions)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LatteXmlFileData &&
                Objects.equals(((LatteXmlFileData) obj).getVendorResult().vendor, this.vendorResult.vendor) &&
                Objects.equals(((LatteXmlFileData) obj).getVendorResult().vendorName, this.vendorResult.vendorName) &&
                Objects.equals(((LatteXmlFileData) obj).tags, this.tags) &&
                Objects.equals(((LatteXmlFileData) obj).filters, this.filters) &&
                Objects.equals(((LatteXmlFileData) obj).variables, this.variables) &&
                Objects.equals(((LatteXmlFileData) obj).functions, this.functions);
    }

    public static class VendorResult implements Serializable {
        public static LatteXmlFileData.VendorResult CUSTOM = new LatteXmlFileData.VendorResult(LatteConfiguration.Vendor.CUSTOM, "");

        public final String vendorName;
        public final LatteConfiguration.Vendor vendor;

        public VendorResult(LatteConfiguration.Vendor vendor, String vendorName) {
            this.vendor = vendor;
            this.vendorName = vendorName;
        }
    }
}
