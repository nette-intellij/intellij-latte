package com.jantvrdik.intellij.latte.utils;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class LattePhpType {

    private final String type;
    private final String name;
    private final boolean nullable;

    public LattePhpType(String type, boolean nullable) {
        this(null, type, nullable);
    }

    public LattePhpType(String name, String type) {
        this(name, type, false);
    }

    public LattePhpType(String name, String type, boolean nullable) {
        this.name = name == null ? null : LattePhpUtil.normalizePhpVariable(name);
        this.type = type;
        this.nullable = nullable;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isClassOrInterfaceType() {
        return type != null && type.startsWith("\\");
    }

    @Nullable
    public Collection<PhpClass> getPhpClasses(Project project) {
        if (!isClassOrInterfaceType()) {
            return null;
        }

        return LattePhpUtil.getClassesByFQN(project, getType());
    }

    @Nullable
    public PhpClass getFirstPhpClass(Project project) {
        Collection<PhpClass> phpClasses = getPhpClasses(project);
        if (phpClasses == null) {
            return null;
        }
        return phpClasses.stream().findFirst().isPresent() ? phpClasses.stream().findFirst().get() : null;
    }

    public static LattePhpType fromString(String s) {
        String wholeString = s.substring(13, s.length() - 1);
        String[] splited = wholeString.split(",");

        String name = splited[0].equals("null") ? null : splited[0];
        return new LattePhpType(name, splited[1], splited[2].equals("@Nullable"));
    }

    @Override
    public String toString() {
        return "LattePhpType(" + name + "," + type + "," + (nullable ? "@Nullable" : "@NotNull") + ")";
    }

    public static String toStringList(LattePhpType lattePhpType) {
        return lattePhpType.toString();
    }
}