package com.jantvrdik.intellij.latte.parser;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.HeavyPlatformTestCase;
import com.intellij.testFramework.ParsingTestCase;
import com.intellij.testFramework.TestDataFile;
import com.intellij.testFramework.fixtures.JavaTestFixtureFactory;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.settings.LatteTagSettings;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileData;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

public class ParserTest extends ParsingTestCase {

    public ParserTest() {
        super("", "latte", new LatteParserDefinition());
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

    protected String loadFile(@NonNls @TestDataFile String name) throws IOException {
        return FileUtil.loadFile(new File(myFullDataPath, name), CharsetToolkit.UTF8, true);
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

    private Collection<LatteXmlFileData> getXmlFileData() {
        Collection<LatteXmlFileData> out = new HashSet<>();
        LatteXmlFileData xmlFileData = new LatteXmlFileData(new LatteXmlFileData.VendorResult(LatteConfiguration.Vendor.LATTE, "Latte"));
        out.add(xmlFileData);
        xmlFileData.addTag(new LatteTagSettings("block", LatteTagSettings.Type.PAIR));
        xmlFileData.addTag(new LatteTagSettings("if", LatteTagSettings.Type.PAIR));
        return out;
    }

}
