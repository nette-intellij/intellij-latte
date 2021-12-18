package com.jantvrdik.intellij.latte.php;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class NettePhpTypeTest {

	@Test
	public void testReadableString() {
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
	public void testClassNames() {
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
	public void testContainsObjectsOnly() {
		assertContainsObjectsOnly("string", false);
		assertContainsObjectsOnly("Int", false);
		assertContainsObjectsOnly("callable", false);
		assertContainsObjectsOnly("Iterable|NULL", false);
		assertContainsObjectsOnly("Iterable[]|null", false);
		assertContainsObjectsOnly("\\Foo\\Bar\\TestClass", true);
		assertContainsObjectsOnly("\\Foo\\Bar\\TestClass|object", true);
		assertContainsObjectsOnly("object", true);
		assertContainsObjectsOnly("object|null", false);
		assertContainsObjectsOnly("Foo\\Bar\\TestClass|\\Bar\\TestClass|null", false);
		assertContainsObjectsOnly("Foo\\Bar\\TestClass|Bar\\TestClass|null", false);
		assertContainsObjectsOnly("Foo\\Bar\\TestClass|String|null", false);
		assertContainsObjectsOnly("Foo\\Bar\\TestClass[][]|String|null", false);
		assertContainsObjectsOnly("Unknown|String|NULL", false);
	}

	@Test
	public void testContainsObjectsOnlyForDepth() {
		assertContainsObjectsOnly(0, "string", false);
		assertContainsObjectsOnly(1, "Foo\\Bar\\TestClass[]", true);
		assertContainsObjectsOnly(1, "\\Foo\\Bar\\TestClass", false);
		assertContainsObjectsOnly(1, "\\Foo\\Bar\\TestClass[]|object[]", true);
		assertContainsObjectsOnly(2, "\\Foo\\Bar\\TestClass[][]|object[][]", true);
		assertContainsObjectsOnly(2, "\\Foo\\Bar\\TestClass[]|object[][]", false);
	}

	@Test
	public void testContainsNativeObject() {
		assertContainsNativeObject("string", false);
		assertContainsNativeObject("object", true);
		assertContainsNativeObject("object|string|null", true);
		assertContainsNativeObject("Foo\\Bar\\TestClass", false);
		assertContainsNativeObject("Foo\\Bar\\TestClass[]|object[]", false);
		assertContainsNativeObject("\\Bar\\TestClass|object", true);
	}

	@Test
	public void testContainsNativeObjectForDepth() {
		assertContainsNativeObject(1, "Foo\\Bar\\TestClass", false);
		assertContainsNativeObject(1, "Foo\\Bar\\TestClass|object", false);
		assertContainsNativeObject(1, "Foo\\Bar\\TestClass[]", false);
		assertContainsNativeObject(1, "\\Foo\\Bar\\TestClass|object", false);
		assertContainsNativeObject(1, "\\Foo\\Bar\\TestClass[]|object[]", true);
		assertContainsNativeObject(1, "\\Foo\\Bar\\TestClass[]|object[][]", false);
		assertContainsNativeObject(2, "\\Foo\\Bar\\TestClass[]|object[][]", true);
		assertContainsNativeObject(2, "string|object[][]", true);
		assertContainsNativeObject(2, "object|string[][]", false);
	}

	@Test
	public void testClassNamesForDepth() {
		assertLattePhpTypeClasses(1, new String[]{}, "Iterable|NULL");
		assertLattePhpTypeClasses(1, new String[]{}, "Iterable[]|null");
		assertLattePhpTypeClasses(1, new String[]{}, "\\Foo\\Bar\\TestClass");
		assertLattePhpTypeClasses(2, new String[]{}, "Foo\\Bar\\TestClass|\\Bar\\TestClass|null");
		assertLattePhpTypeClasses(0, new String[]{"\\Foo\\Bar\\TestClass"}, "Foo\\Bar\\TestClass|String|null");
		assertLattePhpTypeClasses(2, new String[]{"\\Foo\\Bar\\TestClass"}, "Foo\\Bar\\TestClass[][]|String|null");
		assertLattePhpTypeClasses(1, new String[]{}, "Unknown|String|NULL");
	}

	@Test
	public void testGetTypes() {
		assertGetTypes("string|null", new String[]{"string", "null"});
		assertGetTypes("int|null", new String[]{"int", "null"});
		assertGetTypes("int", new String[]{"int"});
		assertGetTypes("\\Foo\\Bar\\TestClass", new String[]{"\\Foo\\Bar\\TestClass"});
	}

	@Test
	public void testContains() {
		assertContains("string|null", "null", true);
		assertContains("string|null", "string", true);
		assertContains("string[]|null", "string[]", true);
		assertContains("mixed", "string[]", true);
		assertContains("mixed", "\\Foo\\Bar", true);
		assertContains("mixed", "object|null", true);
		assertContains("Iterable|null", "iterable", true);
		assertContains("Int", "int", true);
		assertContains("string|null", "int", false);
		assertContains("string|int|null", "int", true);
		assertContains("string|int|null", "\\Foo\\Bar", false);
		assertContains("\\Foo\\Bar|null", "\\Foo\\Bar", true);
		assertContains("object", "\\Foo\\Bar", true);
		assertContains("object|null", "\\Foo\\Bar|null", true);
		assertContains("object[]", "\\Foo\\Bar[]", true);
		assertContains("object[]|string", "\\Foo\\Bar[]|\\Bar[]", true);
		assertContains("object[]|string", "\\Foo\\Bar[]|\\Bar[]|string[]", false);
		assertContains("object", "\\Foo\\Bar|string", false);
	}

	@Test
	public void testIsNullable() {
		assertIsNullable(true, "null");
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
	public void testIsNative() {
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
	public void testIsMixed() {
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
	public void testIsIterable() {
		assertIsIterable(false, "mixed");
		assertIsIterable(true, "mixed[]");
		assertIsIterable(false, "Int");
		assertIsIterable(false, "callable");
		assertIsIterable(true, "Iterable[]|null");
		assertIsIterable(false, "\\Foo\\Bar\\TestClass");
		assertIsIterable(false, "Foo\\Bar\\TestClass|\\Bar\\TestClass");
		assertIsIterable(true, "Foo\\Bar\\TestClass[][]|String|null");

		assertIsIterable(1, false, "Iterable[]");
		assertIsIterable(2, true, "mixed[][][]");
		assertIsIterable(5, false, "string[][]");
	}

	@Test
	public void testHasClass() {
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

	@Test
	public void testWithDepth() {
		NettePhpType phpType = NettePhpType.create("Foo\\TestClass[][]|String|null");
		assertEquals("\\Foo\\TestClass[][]|string|null", phpType.toString());
		assertTrue(phpType.isIterable());
		assertTrue("Must be nullable", phpType.isNullable());

		NettePhpType firstDepth = phpType.withDepth(1);
		assertEquals("\\Foo\\TestClass[]", firstDepth.toString());
		assertTrue(firstDepth.isIterable());
		assertFalse(firstDepth.isNullable());
		assertFalse(firstDepth.containsClasses());

		NettePhpType secondDepth = firstDepth.withDepth(1);
		assertEquals(secondDepth.toString(), phpType.withDepth(2).toString());
		assertEquals("\\Foo\\TestClass", secondDepth.toString());
		assertFalse(secondDepth.isIterable());
		assertFalse(secondDepth.isNullable());
		assertTrue(secondDepth.containsClasses());
	}

	public static void assertLattePhpType(String expected, String type) {
		assertEquals(expected, NettePhpType.create(type).toString());
	}

	public static void assertLattePhpTypeClasses(String[] expected, String type) {
		assertArrayEquals(expected, NettePhpType.create(type).findClasses());
	}

	public static void assertLattePhpTypeClasses(int depth, String[] expected, String type) {
		assertArrayEquals(expected, NettePhpType.create(type).findClasses(depth));
	}

	public static void assertIsNullable(boolean nullable, String type) {
		assertIsNullable(0, nullable, type);
	}

	public static void assertIsNullable(int depth, boolean nullable, String type) {
		if (nullable) {
			assertTrue(NettePhpType.create(type).isNullable(depth));
		} else {
			assertFalse(NettePhpType.create(type).isNullable(depth));
		}
	}

	public static void assertContainsObjectsOnly(String type, boolean objectOnly) {
		assertContainsObjectsOnly(0, type, objectOnly);
	}

	public static void assertContainsObjectsOnly(int depth, String type, boolean objectOnly) {
		if (objectOnly) {
			assertTrue(NettePhpType.create(type).containsObjectsOnly(depth));
		} else {
			assertFalse(NettePhpType.create(type).containsObjectsOnly(depth));
		}
	}

	public static void assertContainsNativeObject(String type, boolean nativeObject) {
		assertContainsNativeObject(0, type, nativeObject);
	}

	public static void assertContainsNativeObject(int depth, String type, boolean nativeObject) {
		if (nativeObject) {
			assertTrue(NettePhpType.create(type).containsNativeObject(depth));
		} else {
			assertFalse(NettePhpType.create(type).containsNativeObject(depth));
		}
	}

	public static void assertIsNative(boolean isNative, String type) {
		assertIsNative(0, isNative, type);
	}

	public static void assertIsNative(int depth, boolean isNative, String type) {
		if (isNative) {
			assertTrue(NettePhpType.create(type).isNative(depth));
		} else {
			assertFalse(NettePhpType.create(type).isNative(depth));
		}
	}

	public static void assertGetTypes(String type, String[] actual) {
		Assert.assertEquals(Arrays.asList(actual), NettePhpType.create(type).getTypes());
	}

	public static void assertContains(String type, String actual, boolean contains) {
		if (contains) {
			assertTrue(NettePhpType.create(type).contains(NettePhpType.create(actual)));
		} else {
			assertFalse(NettePhpType.create(type).contains(NettePhpType.create(actual)));
		}
	}

	public static void assertIsMixed(boolean isMixed, String type) {
		assertIsMixed(0, isMixed, type);
	}

	public static void assertIsMixed(int depth, boolean isMixed, String type) {
		if (isMixed) {
			assertTrue(NettePhpType.create(type).isMixed(depth));
		} else {
			assertFalse(NettePhpType.create(type).isMixed(depth));
		}
	}

	public static void assertIsIterable(boolean isIterable, String type) {
		assertIsIterable(0, isIterable, type);
	}

	public static void assertIsIterable(int depth, boolean isIterable, String type) {
		if (isIterable) {
			assertTrue(NettePhpType.create(type).isIterable(depth));
		} else {
			assertFalse(NettePhpType.create(type).isIterable(depth));
		}
	}

	public static void assertHasClass(String expected, String type, boolean has) {
		if (has) {
			assertTrue(NettePhpType.create(type).hasClass(expected));
		} else {
			assertFalse(NettePhpType.create(type).hasClass(expected));
		}
	}
}
