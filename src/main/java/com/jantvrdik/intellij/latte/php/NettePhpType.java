package com.jantvrdik.intellij.latte.php;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class NettePhpType {
    private static final Map<String, NettePhpType> instances = new HashMap<>();

    final private static String[] nativeClassConstants = new String[]{"class"};

    final private static String[] nativeTypeHints = new String[]{"string", "int", "bool", "object", "float", "array", "mixed", "null", "callable", "iterable"};

    final private static String[] iterableTypes = new String[]{"\\Iterator", "\\Generator"};

    final private static String[] nativeIterableTypeHints = new String[]{"array", "iterable"};

    final private static String[] magicMethods = new String[]{"__construct", "__callstatic", "__call", "__get", "__isset", "__clone", "__set", "__unset"};

    final public static NettePhpType MIXED = new NettePhpType("mixed");
    final public static NettePhpType STRING = new NettePhpType("string");
    final public static NettePhpType INT = new NettePhpType("int");
    final public static NettePhpType BOOL = new NettePhpType("bool");
    final public static NettePhpType OBJECT = new NettePhpType("object");
    final public static NettePhpType FLOAT = new NettePhpType("float");
    final public static NettePhpType ARRAY = new NettePhpType("array");
    final public static NettePhpType NULL = new NettePhpType("null");
    final public static NettePhpType CALLABLE = new NettePhpType("callable");
    final public static NettePhpType ITERABLE = new NettePhpType("iterable");

    final private static Map<String, NettePhpType[]> nativeTypes = new HashMap<String, NettePhpType[]>() {{
        put("string", new NettePhpType[]{STRING, new NettePhpType("mixed|null"), new NettePhpType("mixed[]")});
        put("int", new NettePhpType[]{INT, new NettePhpType("int|null"), new NettePhpType("int[]")});
        put("bool", new NettePhpType[]{BOOL, new NettePhpType("bool|null"), new NettePhpType("bool[]")});
        put("object", new NettePhpType[]{OBJECT, new NettePhpType("object|null"), new NettePhpType("object[]")});
        put("float", new NettePhpType[]{FLOAT, new NettePhpType("float|null"), new NettePhpType("float[]")});
        put("array", new NettePhpType[]{ARRAY, new NettePhpType("array|null"), new NettePhpType("array[]")});
        put("mixed", new NettePhpType[]{MIXED, new NettePhpType("mixed|null"), new NettePhpType("mixed[]")});
        put("null", new NettePhpType[]{NULL, new NettePhpType("null"), new NettePhpType("null[]")});
        put("callable", new NettePhpType[]{CALLABLE, new NettePhpType("callable|null"), new NettePhpType("callable[]")});
        put("iterable", new NettePhpType[]{ITERABLE, new NettePhpType("iterable|null"), new NettePhpType("iterable[]")});
    }};

    private final @Nullable String name;
    private final List<String> types = new ArrayList<>();
    private final Map<Integer, List<String>> wholeTypes = new HashMap<>();
    private final List<Integer> nullable = new ArrayList<>();
    private final List<Integer> mixed = new ArrayList<>();
    private final List<Integer> iterable = new ArrayList<>();
    private final List<Integer> natives = new ArrayList<>();
    private final Map<Integer, List<String>> classes = new HashMap<>();
    private final Map<Integer, NettePhpType> forDepth = new HashMap<>();

    @NotNull
    public static NettePhpType create(final @Nullable String type, boolean nullable) {
        return create(null, type, nullable);
    }

    @NotNull
    public static NettePhpType create(final @Nullable String name, final @Nullable String type) {
        return create(name, type, false);
    }

    @NotNull
    public static NettePhpType create(final @Nullable String type) {
        return create(null, type, false);
    }

    @NotNull
    public static NettePhpType create(final @NotNull PhpType phpType) {
        return create(null, String.join("|", phpType.getTypes()), LattePhpUtil.isNullable(phpType));
    }

    @NotNull
    public static NettePhpType create(final @NotNull List<PhpType> phpTypes) {
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
    public static NettePhpType create(final @Nullable String name, final @Nullable String type, final boolean nullable) {
        if (type == null || type.trim().length() == 0) {
            return NettePhpType.MIXED;
        }

        String trimmed = type.trim().toLowerCase();
        if (isNativeTypeHint(trimmed)) {
            return nativeTypes.get(normalizeTypeHint(trimmed))[0];

        } else if (trimmed.endsWith("[]")) {
            String typeHint = trimmed.substring(0, trimmed.length() - 2);
            if (isNativeTypeHint(typeHint)) {
                return nativeTypes.get(normalizeTypeHint(typeHint))[2];
            }

        }

        if (trimmed.endsWith("|null") || trimmed.startsWith("null|")) {
            String typeHint = trimmed.startsWith("null|") ? trimmed.substring(6) : trimmed.substring(0, trimmed.length() - 5);
            if (isNativeTypeHint(typeHint)) {
                return nativeTypes.get(normalizeTypeHint(typeHint))[1];
            }
        }

        if (!instances.containsKey(type)) {
            instances.put(type, new NettePhpType(name, type, nullable));
        }
        return instances.get(type);
    }

    private NettePhpType(@NotNull String type) {
        this(null, type, false);
    }

    private NettePhpType(final @Nullable String name, final @NotNull String typeString, final boolean nullable) {
        String[] parts = typeString.split("\\|");
        for (String part : parts) {
            part = part.trim();
            if (part.length() == 0) {
                continue;
            }

            NettePhpType.TypePart typePart = new NettePhpType.TypePart(part);
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
                for (int i = typePart.depth - 1; i >= 0; i--) {
                    this.iterable.add(i);
                }
            }

            wholeTypes.putIfAbsent(typePart.depth, new ArrayList<>());
            wholeTypes.get(typePart.depth).add(typePart.getWholePart());

            types.add(typePart.getPart());
        }
        this.name = name == null ? null : LattePhpUtil.normalizePhpVariable(name);

        if (nullable && !this.nullable.contains(0)) {
            this.nullable.add(0);
        }
    }

    @Nullable
    public String getName() {
        return name;
    }

    public boolean containsClasses() {
        return this.classes.containsKey(0);
    }

    public boolean hasUndefinedClass(final @NotNull Project project) {
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

    public boolean hasClass(final @NotNull String className) {
        if (!containsClasses()) {
            return false;
        }
        String normalizedName = LattePhpUtil.normalizeClassName(className);
        return classes.containsKey(0) && classes.get(0).contains(normalizedName);
    }

    public boolean hasClass(final @NotNull Collection<PhpClass> phpClasses) {
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

    public boolean isNative(final int depth) {
        return natives.contains(depth);
    }

    public boolean isMixed() {
        return isMixed(0);
    }

    public boolean isMixed(final int depth) {
        return mixed.contains(depth);
    }

    public boolean isIterable(final @NotNull Project project) {
        return isIterable(project, 0);
    }

    public boolean isIterable(final @NotNull Project project, final int depth) {
        if (iterable.contains(depth)) {
            return true;
        }

        Collection<PhpClass> classes = getPhpClasses(project, depth);
        for (PhpClass phpClass : classes) {
            if (LattePhpUtil.isReferenceFor(getIterableTypes(), phpClass)) {
                return true;
            }
        }
        return false;
    }

    boolean isIterable(final int depth) {
        return iterable.contains(depth);
    }

    boolean isIterable() {
        return isIterable(0);
    }

    @NotNull
    public Collection<PhpClass> getPhpClasses(final @NotNull Project project) {
        return getPhpClasses(project, 0);
    }

    @NotNull
    public Collection<PhpClass> getPhpClasses(final @NotNull Project project, final int depth) {
        List<PhpClass> output = new ArrayList<>();
        for (String wholeType : findClasses(depth)) {
            output.addAll(LattePhpUtil.getClassesByFQN(project, wholeType));
        }
        return output;
    }

    @NotNull
    String[] findClasses() {
        return findClasses(0);
    }

    @NotNull
    String[] findClasses(final int depth) {
        if (!this.classes.containsKey(depth)) {
            return new String[0];
        }
        return this.classes.get(depth).toArray(new String[0]);
    }

    @NotNull
    public NettePhpType withDepth(final int depth) {
        if (depth == 0) {
            return this;
        }

        NettePhpType found = forDepth.get(depth);
        if (found != null) {
            return found;
        }

        List<String> depthTypes = getTypesForDepth(depth);
        if (depthTypes.size() > 0) {
            found = new NettePhpType(String.join("|", depthTypes));
        }
        found = found == null ? NettePhpType.MIXED : found;
        forDepth.put(depth, found);
        return found;
    }

    @NotNull
    private List<String> getTypesForDepth(final int depth) {
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

    private void getTypesForDepth(final @NotNull List<String> depthTypes, final int depth, final int subDepth) {
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
    @NotNull
    public String toString() {
        return String.join("|", types);
    }

    @NotNull
    public List<String> getTypes() {
        return Collections.unmodifiableList(types);
    }

    private static class TypePart {
        int depth = 0;

        private final String part;
        private boolean isNative = false;
        private boolean isNull = false;
        private boolean isMixed = false;
        private boolean isClass = false;
        private @Nullable
        NettePhpType.TypePart arrayOf = null;

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

            } else if (NettePhpType.isNativeTypeHint(part)) {
                part = part.toLowerCase();

                if (NettePhpType.isIterable(part)) {
                    loadArrayOf("mixed", depth);
                } else if (NettePhpType.isNull(part)) {
                    this.isNull = true;
                } else if (NettePhpType.isMixed(part)) {
                    this.isMixed = true;
                }
                this.isNative = true;

            } else {
                part = LattePhpUtil.normalizeClassName(part);
                this.isClass = true;
            }
            this.part = part;
        }

        private void loadArrayOf(final @NotNull String type, final int depth) {
            this.depth = depth;
            this.arrayOf = new NettePhpType.TypePart(type);
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

        @NotNull
        String getPart() {
            return arrayOf == null || isNative
                    ? part
                    : (arrayOf.getPart() + String.join("", Collections.nCopies(depth, "[]")));
        }

        @NotNull
        String getWholePart() {
            return arrayOf == null || isNative ? part : arrayOf.getWholePart();
        }
    }

    @NotNull
    public static String[] getNativeClassConstants() {
        return nativeClassConstants;
    }

    @NotNull
    public static String[] getNativeTypeHints() {
        return nativeTypeHints;
    }

    @NotNull
    public static String[] getIterableTypes() {
        return iterableTypes;
    }

    public static boolean isNativeTypeHint(final @NotNull String value) {
        return Arrays.asList(nativeTypeHints).contains(normalizeTypeHint(value));
    }

    public static boolean isIterable(final @NotNull String value) {
        return Arrays.asList(nativeIterableTypeHints).contains(normalizeTypeHint(value));
    }

    public static boolean isNull(final @NotNull String value) {
        return "null".equals(normalizeTypeHint(value));
    }

    public static boolean isMixed(final @NotNull String value) {
        return "mixed".equals(normalizeTypeHint(value));
    }

    public static boolean isMagicMethod(final @NotNull String value) {
        return Arrays.asList(magicMethods).contains(value.toLowerCase());
    }

    @NotNull
    public static String normalizeTypeHint(@NotNull String value) {
        value = value.toLowerCase();
        return value.startsWith("\\") ? value.substring(1) : value;
    }

}