package com.jantvrdik.intellij.latte.utils;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class LattePhpType {
    private static Map<String, LattePhpType> instances = new HashMap<>();

    final public static LattePhpType MIXED = new LattePhpType("mixed");
    final public static LattePhpType STRING = new LattePhpType("string");
    final public static LattePhpType INT = new LattePhpType("int");
    final public static LattePhpType BOOL = new LattePhpType("bool");
    final public static LattePhpType OBJECT = new LattePhpType("object");
    final public static LattePhpType FLOAT = new LattePhpType("float");
    final public static LattePhpType ARRAY = new LattePhpType("array");
    final public static LattePhpType NULL = new LattePhpType("null");
    final public static LattePhpType CALLABLE = new LattePhpType("callable");
    final public static LattePhpType ITERABLE = new LattePhpType("iterable");

    final private static Map<String, LattePhpType[]> nativeTypes = new HashMap<String, LattePhpType[]>() {{
        put("string", new LattePhpType[]{STRING, new LattePhpType("mixed|null"), new LattePhpType("mixed[]")});
        put("int", new LattePhpType[]{INT, new LattePhpType("int|null"), new LattePhpType("int[]")});
        put("bool", new LattePhpType[]{BOOL, new LattePhpType("bool|null"), new LattePhpType("bool[]")});
        put("object", new LattePhpType[]{OBJECT, new LattePhpType("object|null"), new LattePhpType("object[]")});
        put("float", new LattePhpType[]{FLOAT, new LattePhpType("float|null"), new LattePhpType("float[]")});
        put("array", new LattePhpType[]{ARRAY, new LattePhpType("array|null"), new LattePhpType("array[]")});
        put("mixed", new LattePhpType[]{MIXED, new LattePhpType("mixed|null"), new LattePhpType("mixed[]")});
        put("null", new LattePhpType[]{NULL, new LattePhpType("null"), new LattePhpType("null[]")});
        put("callable", new LattePhpType[]{CALLABLE, new LattePhpType("callable|null"), new LattePhpType("callable[]")});
        put("iterable", new LattePhpType[]{ITERABLE, new LattePhpType("iterable|null"), new LattePhpType("iterable[]")});
    }};

    private final String name;
    private final List<String> types = new ArrayList<>();
    private final Map<Integer, List<String>> wholeTypes = new HashMap<>();
    private final List<Integer> nullable = new ArrayList<>();
    private final List<Integer> mixed = new ArrayList<>();
    private final List<Integer> iterable = new ArrayList<>();
    private final List<Integer> natives = new ArrayList<>();
    private Map<Integer, List<String>> classes = new HashMap<>();
    private Map<Integer, LattePhpType> forDepth = new HashMap<>();

    @NotNull
    public static LattePhpType create(String type, boolean nullable) {
        return create(null, type, nullable);
    }

    @NotNull
    public static LattePhpType create(String name, String type) {
        return create(name, type, false);
    }

    @NotNull
    public static LattePhpType create(String type) {
        return create(null, type, false);
    }

    @NotNull
    public static LattePhpType create(PhpType phpType) {
        return create(null, String.join("|", phpType.getTypes()), LattePhpUtil.isNullable(phpType));
    }

    @NotNull
    public static LattePhpType create(List<PhpType> phpTypes) {
        List<String> typesStrings = new ArrayList<>();
        for (PhpType type : phpTypes) {
            typesStrings.add(type.toString());
        }
        Set<String> temp = new LinkedHashSet<String>(
                Arrays.asList(String.join("|", typesStrings).split("\\|"))
        );
        return create(null, String.join("|", temp));
    }

    @NotNull
    public static LattePhpType create(String name, String type, boolean nullable) {
        if (type == null || type.trim().length() == 0) {
            return LattePhpType.MIXED;
        }

        String trimmed = type.trim().toLowerCase();
        if (LatteTypesUtil.isNativeTypeHint(trimmed)) {
            return nativeTypes.get(LatteTypesUtil.normalizeTypeHint(trimmed))[0];

        } else if (trimmed.endsWith("[]")) {
            String typeHint = trimmed.substring(0, trimmed.length() - 2);
            if (LatteTypesUtil.isNativeTypeHint(typeHint)) {
                return nativeTypes.get(LatteTypesUtil.normalizeTypeHint(typeHint))[2];
            }

        }

        if (trimmed.endsWith("|null") || trimmed.startsWith("null|")) {
            String typeHint = trimmed.startsWith("null|") ? trimmed.substring(6) : trimmed.substring(0, trimmed.length() - 5);
            if (LatteTypesUtil.isNativeTypeHint(typeHint)) {
                return nativeTypes.get(LatteTypesUtil.normalizeTypeHint(typeHint))[1];
            }
        }

        if (!instances.containsKey(type)) {
            instances.put(type, new LattePhpType(name, type, nullable));
        }
        return instances.get(type);
    }

    private LattePhpType(@NotNull String type) {
        this(null, type, false);
    }

    private LattePhpType(String name, @NotNull String typeString, boolean nullable) {
        String[] parts = typeString.split("\\|");
        for (String part : parts) {
            part = part.trim();
            if (part.length() == 0) {
                continue;
            }

            TypePart typePart = new TypePart(part);
            if (typePart.isClass()) {
                if (!this.classes.containsKey(typePart.depth)) {
                    this.classes.put(typePart.depth, new ArrayList<>());
                }
                String className = typePart.arrayOf != null ? typePart.arrayOf.getPart() : typePart.getPart();
                if (!this.classes.get(typePart.depth).contains(className)) {
                    this.classes.get(typePart.depth).add(className);
                }

            } else if (typePart.isNullable()) {
                this.nullable.add(typePart.depth);

            } else if (typePart.isMixed()) {
                this.mixed.add(typePart.depth);

            } else if (typePart.isNative()) {
                this.natives.add(typePart.depth);
            }

            if (typePart.isIterable()) {
                this.iterable.add(typePart.depth - 1);
            }
            types.add(typePart.getPart());

            wholeTypes.putIfAbsent(typePart.depth, new ArrayList<>());
            wholeTypes.get(typePart.depth).add(typePart.getWholePart());
        }
        this.name = name == null ? null : LattePhpUtil.normalizePhpVariable(name);

        if (nullable && !this.nullable.contains(0)) {
            this.nullable.add(0);
        }
    }

    public String getName() {
        return name;
    }

    public boolean containsClasses() {
        return this.classes.containsKey(0);
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
        return classes.containsKey(0) && classes.get(0).contains(normalizedName);
    }

    public boolean hasClass(Collection<PhpClass> phpClasses) {
        if (!containsClasses()) {
            return false;
        }
        for (PhpClass phpClass : phpClasses) {
            if (hasClass(phpClass.getFQN())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNullable() {
        return isNullable(0);
    }

    public boolean isNullable(int depth) {
        return nullable.contains(depth);
    }

    public boolean isNative() {
        return isNative(0);
    }

    public boolean isNative(int depth) {
        return natives.contains(depth);
    }

    public boolean isMixed() {
        return isMixed(0);
    }

    public boolean isMixed(int depth) {
        return mixed.contains(depth);
    }

    public boolean isIterable(Project project) {
        return isIterable(project, 0);
    }

    public boolean isIterable(Project project, int depth) {
        if (iterable.contains(depth)) {
            return true;
        }

        Collection<PhpClass> classes = getPhpClasses(project, depth);
        for (PhpClass phpClass : classes) {
            if (LattePhpUtil.isReferenceFor(LatteTypesUtil.getIterableInterfaces(), phpClass)) {
                return true;
            }
        }
        return false;
    }

    public Collection<PhpClass> getPhpClasses(Project project) {
        return getPhpClasses(project, 0);
    }

    public Collection<PhpClass> getPhpClasses(Project project, int depth) {
        List<PhpClass> output = new ArrayList<>();
        for (String wholeType : findClasses(depth)) {
            output.addAll(LattePhpUtil.getClassesByFQN(project, wholeType));
        }
        return output;
    }

    String[] findClasses() {
        return findClasses(0);
    }

    String[] findClasses(int depth) {
        if (!this.classes.containsKey(depth)) {
            return new String[0];
        }
        return this.classes.get(depth).toArray(new String[0]);
    }

    public LattePhpType withDepth(int depth) {
        if (depth == 0) {
            return this;
        }

        LattePhpType found = forDepth.get(depth);
        if (found != null) {
            return found;
        }

        List<String> depthTypes = getTypesForDepth(depth);
        if (depthTypes.size() > 0) {
            found = new LattePhpType(String.join("|", depthTypes));
        }
        found = found == null ? LattePhpType.MIXED : found;
        forDepth.put(depth, found);
        return found;
    }

    private List<String> getTypesForDepth(int depth) {
        if (depth == 0) {
            return types;
        }

        List<String> depthTypes = new ArrayList<>();

        for (int key : wholeTypes.keySet()) {
            if (key < depth) {
                continue;
            }
            getTypesForDepth(depthTypes, depth, key - depth);
        }
        return depthTypes;
    }

    private void getTypesForDepth(List<String> depthTypes, int depth, int subDepth) {
        List<String> currentTypes = wholeTypes.get(depth + subDepth);
        if (currentTypes == null) {
            return;
        }
        depthTypes.addAll(
                currentTypes.stream()
                        .map(type -> type + String.join("", Collections.nCopies(subDepth, "[]")))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public String toString() {
        return String.join("|", types);
    }

    private static class TypePart {
        int depth = 0;

        private String part;
        private boolean isNative = false;
        private boolean isNull = false;
        private boolean isMixed = false;
        private boolean isClass = false;
        private @Nullable TypePart arrayOf = null;

        TypePart(@NotNull String part) {
            if (part.endsWith("[]")) {
                String[] parts = part.split("\\[]", -1);
                String type = "mixed";
                int depth = 1;
                if (parts.length > 0) {
                    type = parts[0];
                    depth = parts.length - 1;
                }
                loadArrayOf(type, depth);

            } else if (LatteTypesUtil.isNativeTypeHint(part)) {
                part = part.toLowerCase();

                if (LatteTypesUtil.isIterable(part)) {
                    loadArrayOf("mixed", depth);
                } else if (LatteTypesUtil.isNull(part)) {
                    this.isNull = true;
                } else if (LatteTypesUtil.isMixed(part)) {
                    this.isMixed = true;
                }
                this.isNative = true;

            } else {
                part = LattePhpUtil.normalizeClassName(part);
                this.isClass = true;
            }
            this.part = part;
        }

        private void loadArrayOf(@NotNull String type, int depth) {
            this.depth = depth;
            this.arrayOf = new TypePart(type);
        }

        boolean isIterable() {
            return arrayOf != null;
        }

        boolean isClass() {
            return isClass || (arrayOf != null && arrayOf.isClass());
        }

        boolean isMixed() {
            return isMixed || (arrayOf != null && arrayOf.isMixed());
        }

        boolean isNative() {
            return isNative || (arrayOf != null && arrayOf.isNative());
        }

        boolean isNullable() {
            return isNull || (arrayOf != null && arrayOf.isNullable());
        }

        String getPart() {
            return arrayOf == null || isNative
                    ? part
                    : (arrayOf.getPart() + String.join("", Collections.nCopies(depth, "[]")));
        }

        String getWholePart() {
            return arrayOf == null || isNative ? part : arrayOf.getWholePart();
        }
    }

}