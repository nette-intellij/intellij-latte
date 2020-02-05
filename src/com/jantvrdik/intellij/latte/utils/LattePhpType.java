package com.jantvrdik.intellij.latte.utils;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class LattePhpType {

    private final List<TypePart> types = new ArrayList<TypePart>();
    private final String name;
    private final boolean nullable;
    private boolean hasClass = false;

    public LattePhpType(String type) {
        this(null, type, false);
    }

    public LattePhpType(String type, boolean nullable) {
        this(null, type, nullable);
    }

    public LattePhpType(String name, String type) {
        this(name, type, false);
    }

    public LattePhpType(String name, String typeString, boolean nullable) {
        if (typeString == null || typeString.length() == 0) {
            types.add(new TypePart("mixed"));

        } else {
            String[] parts = typeString.split("\\|");
            for (String part : parts) {
                part = part.trim();
                if (part.length() == 0) {
                    continue;
                }

                String lower = part.toLowerCase();
                if (lower.equals("null")) {
                    nullable = true;
                    continue;
                }

                TypePart typePart = new TypePart(part);
                if (typePart.isClass) {
                    this.hasClass = true;
                }
                types.add(typePart);
            }
        }
        this.name = name == null ? null : LattePhpUtil.normalizePhpVariable(name);
        this.nullable = nullable;
    }

    public String getName() {
        return name;
    }

    public boolean containsClasses() {
        return hasClass;
    }

    public boolean hasUndefinedClass(@NotNull Project project) {
        if (!containsClasses()) {
            return false;
        }

        for (String className : findClasses()) {
            if (LattePhpUtil.getClassesByFQN(project, className).size() == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean hasClass(String className) {
        if (!containsClasses()) {
            return false;
        }
        String normalizedName = LattePhpUtil.normalizeClassName(className);
        return types.stream().anyMatch(typePart -> typePart.isClass && typePart.getPart().equals(normalizedName));
    }

    public boolean isNullable() {
        return nullable;
    }

    public Collection<PhpClass> getPhpClasses(Project project) {
        List<PhpClass> output = new ArrayList<>();
        for (String wholeType : findClasses()) {
            output.addAll(LattePhpUtil.getClassesByFQN(project, wholeType));
        }
        return output;
    }

    String[] findClasses() {
        if (!containsClasses()) {
            return new String[0];
        }
        return types.stream()
                .filter(typePart -> typePart.isClass)
                .map(TypePart::getPart)
                .toArray(String[]::new);
    }

    @Nullable
    public PhpClass getFirstPhpClass(Project project) {
        for (String wholeType : findClasses()) {
            List<PhpClass> classes = new ArrayList<>(LattePhpUtil.getClassesByFQN(project, wholeType));
            if (classes.size() > 0) {
                return classes.get(0);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return toReadableString();
    }

    public String toReadableString() {
        String out =  types.stream()
                .map(TypePart::getPart)
                .collect(Collectors.joining("|"));
        if (nullable) {
            out += "|null";
        }
        return out;
    }

    static class TypePart {
        String part;
        boolean isClass = false;
        boolean isNative = false;
        boolean isArrayOf = false;

        TypePart (@NotNull String part) {
            if (part.endsWith("[]")) {
                part = "array";
                this.isArrayOf = true; //todo: add support for types in array

            } else if (LatteTypesUtil.isNativeTypeHint(part)) {
                part = part.toLowerCase();
                this.isNative = true;

            } else {
                part = LattePhpUtil.normalizeClassName(part);
                this.isClass = true;
            }
            this.part = part;
        }

        String getPart() {
            return part;
        }
    }

}