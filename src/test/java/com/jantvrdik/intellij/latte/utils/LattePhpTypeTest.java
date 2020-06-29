package com.jantvrdik.intellij.latte.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class LattePhpTypeTest {
	@Test
	@SuppressWarnings("unchecked")
	public void testReadableString() throws Exception {
		assertLattePhpType("string", "string");
		assertLattePhpType("int", "Int");
		assertLattePhpType("callable", "callable");
		assertLattePhpType("iterable|null", "Iterable|NULL");
		assertLattePhpType("iterable[]|null", "Iterable[]|null");
		assertLattePhpType("\\Foo\\Bar\\TestClass", "\\Foo\\Bar\\TestClass");
		assertLattePhpType("\\Foo\\Bar\\TestClass|\\Bar\\TestClass|null", "Foo\\Bar\\TestClass|\\Bar\\TestClass|null");
		assertLattePhpType("\\Foo\\Bar\\TestClass|\\Bar\\TestClass|null", "Foo\\Bar\\TestClass|Bar\\TestClass|null");
		assertLattePhpType("\\Foo\\Bar\\TestClass|string|null", "Foo\\Bar\\TestClass|String|null");
		assertLattePhpType("\\Foo\\Bar\\TestClass[][]|string|null", "Foo\\Bar\\TestClass[][]|String|null");
		assertLattePhpType("\\Unknown|string|null", "Unknown|String|NULL");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testClassNames() throws Exception {
		assertLattePhpTypeClasses(new String[]{}, "string");
		assertLattePhpTypeClasses(new String[]{}, "Int");
		assertLattePhpTypeClasses(new String[]{}, "callable");
		assertLattePhpTypeClasses(new String[]{}, "Iterable|NULL");
		assertLattePhpTypeClasses(new String[]{}, "Iterable[]|null");
		assertLattePhpTypeClasses(new String[]{"\\Foo\\Bar\\TestClass"}, "\\Foo\\Bar\\TestClass");
		assertLattePhpTypeClasses(new String[]{"\\Foo\\Bar\\TestClass", "\\Bar\\TestClass"}, "Foo\\Bar\\TestClass|\\Bar\\TestClass|null");
		assertLattePhpTypeClasses(new String[]{"\\Foo\\Bar\\TestClass", "\\Bar\\TestClass"}, "Foo\\Bar\\TestClass|Bar\\TestClass|null");
		assertLattePhpTypeClasses(new String[]{"\\Foo\\Bar\\TestClass"}, "Foo\\Bar\\TestClass|String|null");
		assertLattePhpTypeClasses(new String[]{}, "Foo\\Bar\\TestClass[][]|String|null");
		assertLattePhpTypeClasses(new String[]{"\\Unknown"}, "Unknown|String|NULL");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testClassNamesForDepth() throws Exception {
		assertLattePhpTypeClasses(1, new String[]{}, "Iterable|NULL");
		assertLattePhpTypeClasses(1, new String[]{}, "Iterable[]|null");
		assertLattePhpTypeClasses(1, new String[]{}, "\\Foo\\Bar\\TestClass");
		assertLattePhpTypeClasses(2, new String[]{}, "Foo\\Bar\\TestClass|\\Bar\\TestClass|null");
		assertLattePhpTypeClasses(0, new String[]{"\\Foo\\Bar\\TestClass"}, "Foo\\Bar\\TestClass|String|null");
		assertLattePhpTypeClasses(2, new String[]{"\\Foo\\Bar\\TestClass"}, "Foo\\Bar\\TestClass[][]|String|null");
		assertLattePhpTypeClasses(1, new String[]{}, "Unknown|String|NULL");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testIsNullable() throws Exception {
		assertIsNullable(false, "string");
		assertIsNullable(false, "Int");
		assertIsNullable(false, "callable");
		assertIsNullable(true, "Iterable|NULL");
		assertIsNullable(true, "Iterable[]|null");
		assertIsNullable(false, "\\Foo\\Bar\\TestClass");
		assertIsNullable(true, "Foo\\Bar\\TestClass|\\Bar\\TestClass|null");
		assertIsNullable(true, "Foo\\Bar\\TestClass|Bar\\TestClass|null");
		assertIsNullable(true, "Foo\\Bar\\TestClass|String|null");
		assertIsNullable(true, "Foo\\Bar\\TestClass[][]|String|null");
		assertIsNullable(true, "Unknown|String|NULL");

		assertIsNullable(1, true, "String[]|null[]|null");
		assertIsNullable(2, true, "String[][]|null[][]");
		assertIsNullable(1, true, "null[]");
		assertIsNullable(5, false, "String[][]|null[][]");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testIsNative() throws Exception {
		assertIsNative(true, "string");
		assertIsNative(true, "Int");
		assertIsNative(true, "callable");
		assertIsNative(false, "Iterable[]|null");
		assertIsNative(false, "\\Foo\\Bar\\TestClass");
		assertIsNative(false, "Foo\\Bar\\TestClass|\\Bar\\TestClass");
		assertIsNative(false, "Foo\\Bar\\TestClass|Bar\\TestClass|null");
		assertIsNative(true, "Foo\\Bar\\TestClass|String|null");
		assertIsNative(true, "Foo\\Bar\\TestClass[][]|String|null");
		assertIsNative(true, "Unknown|String|NULL");

		assertIsNative(1, false, "Iterable[]");
		assertIsNative(2, true, "string[][]");
		assertIsNative(5, false, "string[][]");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testIsMixed() throws Exception {
		assertIsMixed(true, "mixed");
		assertIsMixed(false, "mixed[]");
		assertIsMixed(false, "Int");
		assertIsMixed(false, "callable");
		assertIsMixed(false, "Iterable[]|null");
		assertIsMixed(false, "\\Foo\\Bar\\TestClass");
		assertIsMixed(false, "Foo\\Bar\\TestClass|\\Bar\\TestClass");
		assertIsMixed(false, "Foo\\Bar\\TestClass[][]|String|null");

		assertIsMixed(1, true, "Iterable[]");
		assertIsMixed(2, true, "mixed[][]");
		assertIsMixed(5, false, "string[][]");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testHasClass() throws Exception {
		assertHasClass("\\Foo\\Bar", "string", false);
		assertHasClass("Foo\\Bar", "Int", false);
		assertHasClass("Int", "Int", false);
		assertHasClass("callable", "callable", false);
		assertHasClass("true", "Iterable|NULL", false);
		assertHasClass("Iterable[]", "Iterable[]|null", false);
		assertHasClass("Foo\\Bar\\TestClass", "\\Foo\\Bar\\TestClass", true);
		assertHasClass("\\Foo\\Bar\\TestClass", "Foo\\Bar\\TestClass|\\Bar\\TestClass|null", true);
		assertHasClass("Foo\\Bar", "Foo\\Bar\\TestClass|Bar\\TestClass|null", false);
		assertHasClass("\\Foo\\Bar", "Foo\\Bar\\TestClass|String|null", false);
		assertHasClass("Foo\\Bar\\TestClass", "Foo\\Bar\\TestClass[][]|String|null", false);
		assertHasClass("\\Unknown", "Unknown|String|NULL", true);
	}

	public static void assertLattePhpType(String expected, String type) {
		assertEquals(expected, LattePhpType.create(type).toString());
	}

	public static void assertLattePhpTypeClasses(String[] expected, String type) {
		assertArrayEquals(expected, LattePhpType.create(type).findClasses());
	}

	public static void assertLattePhpTypeClasses(int depth, String[] expected, String type) {
		assertArrayEquals(expected, LattePhpType.create(type).findClasses(depth));
	}

	public static void assertIsNullable(boolean nullable, String type) {
		assertIsNullable(0, nullable, type);
	}

	public static void assertIsNullable(int depth, boolean nullable, String type) {
		if (nullable) {
			assertTrue(LattePhpType.create(type).isNullable(depth));
		} else {
			assertFalse(LattePhpType.create(type).isNullable(depth));
		}
	}

	public static void assertIsNative(boolean isNative, String type) {
		assertIsNative(0, isNative, type);
	}

	public static void assertIsNative(int depth, boolean isNative, String type) {
		if (isNative) {
			assertTrue(LattePhpType.create(type).isNative(depth));
		} else {
			assertFalse(LattePhpType.create(type).isNative(depth));
		}
	}

	public static void assertIsMixed(boolean isMixed, String type) {
		assertIsMixed(0, isMixed, type);
	}

	public static void assertIsMixed(int depth, boolean isMixed, String type) {
		if (isMixed) {
			assertTrue(LattePhpType.create(type).isMixed(depth));
		} else {
			assertFalse(LattePhpType.create(type).isMixed(depth));
		}
	}

	public static void assertHasClass(String expected, String type, boolean has) {
		if (has) {
			assertTrue(LattePhpType.create(type).hasClass(expected));
		} else {
			assertFalse(LattePhpType.create(type).hasClass(expected));
		}
	}
}
