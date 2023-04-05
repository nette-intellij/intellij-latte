package com.jantvrdik.intellij.latte.parser;

import com.intellij.testFramework.HeavyPlatformTestCase;
import com.jantvrdik.intellij.latte.BasePsiParsingTestCase;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import org.junit.Test;

import java.net.URL;

public class ParserTest extends BasePsiParsingTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // initialize configuration with test configuration
        LatteConfiguration.getInstance(getProject());

        getProject().registerService(LatteSettings.class);
    }

    @Override
    protected String getTestDataPath() {
        URL url = getClass().getClassLoader().getResource("data/parser");
        assert url != null;
        return url.getFile();
    }

    @Test
    public void testVariable() {
        doTest(true, true);
    }

    @Test
    public void testNBlock() {
        doTest(true, true);
    }

    @Test
    public void testUnknownInIf() {
        doTest(true, true);
    }

    @Test
    public void testClassDefinition() {
        doTest(true, true);
    }

    @Test
    public void testTypedDefinition() {
        doTest(true, true);
    }

    @Test
    public void testForeachDefinition() {
        doTest(true, true);
    }

    @Test
    public void testForeachReferenceDefinition() {
        doTest(true, true);
    }

    @Test
    public void testBlockDefinition() {
        doTest(true, true);
    }

    @Test
    public void testForDefinition() {
        doTest(true, true);
    }

    @Test
    public void testParametersDefinition() {
        doTest(true, true);
    }

    @Test
    public void testMacroFilters() {
        doTest(true, true);
    }

}
