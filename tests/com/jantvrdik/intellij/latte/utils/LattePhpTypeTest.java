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
		assertLattePhpType("array|null", "Iterable[]|null");
		assertLattePhpType("\\Foo\\Bar\\TestClass", "\\Foo\\Bar\\TestClass");
		assertLattePhpType("\\Foo\\Bar\\TestClass|\\Bar\\TestClass|null", "Foo\\Bar\\TestClass|\\Bar\\TestClass|null");
		assertLattePhpType("\\Foo\\Bar\\TestClass|\\Bar\\TestClass|null", "Foo\\Bar\\TestClass|Bar\\TestClass|null");
		assertLattePhpType("\\Foo\\Bar\\TestClass|string|null", "Foo\\Bar\\TestClass|String|null");
		assertLattePhpType("array|string|null", "Foo\\Bar\\TestClass[][]|String|null");
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
		assertEquals(expected, new LattePhpType(type).toReadableString());
	}

	public static void assertLattePhpTypeClasses(String[] expected, String type) {
		assertArrayEquals(expected, new LattePhpType(type).findClasses());
	}

	public static void assertIsNullable(boolean nullable, String type) {
		if (nullable) {
			assertTrue(new LattePhpType(type).isNullable());
		} else {
			assertFalse(new LattePhpType(type).isNullable());
		}
	}

	public static void assertHasClass(String expected, String type, boolean has) {
		if (has) {
			assertTrue(new LattePhpType(type).hasClass(expected));
		} else {
			assertFalse(new LattePhpType(type).hasClass(expected));
		}
	}
}
