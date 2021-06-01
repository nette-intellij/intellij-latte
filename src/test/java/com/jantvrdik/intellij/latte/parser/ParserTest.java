package com.jantvrdik.intellij.latte.parser;

import com.intellij.testFramework.HeavyPlatformTestCase;
import com.jantvrdik.intellij.latte.BasePsiParsingTestCase;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import org.junit.Test;

import java.net.URL;

public class ParserTest extends BasePsiParsingTestCase {

    public ParserTest() {
        super();
        HeavyPlatformTestCase.doAutodetectPlatformPrefix();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // initialize configuration with test configuration
        LatteConfiguration.getInstance(getProject(), getXmlFileData());

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

}
