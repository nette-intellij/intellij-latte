package com.jantvrdik.intellij.latte.utils;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class LattePhpType {

    private final String[] types;
    private final String name;
    private final boolean nullable;

    public LattePhpType(String type, boolean nullable) {
        this(null, new String[]{type}, nullable);
    }

    public LattePhpType(String[] types, boolean nullable) {
        this(null, types, nullable);
    }

    public LattePhpType(String name, String type) {
        this(name, new String[]{type}, false);
    }

    public LattePhpType(String name, String type, boolean nullable) {
        this(name, new String[]{type}, nullable);
    }

    public LattePhpType(String name, String[] types, boolean nullable) {
        this.name = name == null ? null : LattePhpUtil.normalizePhpVariable(name);
        this.types = types;
        this.nullable = nullable;
    }

    public String getName() {
        return name;
    }

    private static String getWholeType(String type) {
        return LattePhpType.isArrayOf(type) ? "array" : type;
    }

    public boolean hasClass(String className) {
        for (String type : types) {
            String wholeType = LattePhpType.getWholeType(type);
            if (wholeType.equals(className)) {
                return true;
            }
        }
        return false;
    }

    public boolean isNullable() {
        return nullable;
    }

    private static boolean isClassOrInterfaceType(String type) {
        return type != null && type.contains("\\");
    }

    private static boolean isArrayOf(String type) {
        return type != null && type.endsWith("[]");
    }

    public Collection<PhpClass> getPhpClasses(Project project) {
        List<PhpClass> output = new ArrayList<>();
        for (String type : types) {
            String wholeType = LattePhpType.getWholeType(type);
            if (!LattePhpType.isClassOrInterfaceType(wholeType)) {
                continue;
            }
            output.addAll(LattePhpUtil.getClassesByFQN(project, wholeType));
        }
        return output;
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
        return new LattePhpType(name, splited[1].split(Pattern.quote("|")), splited[2].equals("@Nullable"));
    }

    @Override
    public String toString() {
        return "LattePhpType(" + name + "," + String.join("|", types) + "," + (nullable ? "@Nullable" : "@NotNull") + ")";
    }

    public String toReadableString() {
        String out = String.join("|", types);
        if (nullable) {
            out += "|null";
        }
        return out;
    }

    public static String toStringList(LattePhpType lattePhpType) {
        return lattePhpType.toString();
    }
}