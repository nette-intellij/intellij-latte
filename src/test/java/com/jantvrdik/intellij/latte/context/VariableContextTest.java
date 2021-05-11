package com.jantvrdik.intellij.latte.context;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.HeavyPlatformTestCase;
import com.intellij.testFramework.TestDataFile;
import com.jantvrdik.intellij.latte.BasePsiParsingTestCase;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.LatteHtmlTagContainer;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class VariableContextTest extends BasePsiParsingTestCase {

    public VariableContextTest() {
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
        URL url = getClass().getClassLoader().getResource("data/context");
        assert url != null;
        return url.getFile();
    }

    @Test
    public void testVariable() throws IOException {
        String name = "Variable.latte";
        PsiFile file = parseFile(name, loadFile(name));
        LattePhpVariable definition = PsiTreeUtil.findChildOfAnyType(file, LattePhpVariable.class);
        Assert.assertNotNull(definition);

        Assert.assertSame(file, LatteUtil.getCurrentContext(definition));
    }

    @Test
    public void testInBlock() throws IOException {
        String name = "InBlock.latte";
        PsiFile file = parseFile(name, loadFile(name));
        LattePhpVariable definition = PsiTreeUtil.findChildOfAnyType(file, LattePhpVariable.class);
        Assert.assertNotNull(definition);

        Assert.assertSame(file.getFirstChild(), LatteUtil.getCurrentContext(definition));
    }

    @Test
    public void testNetteAttribute() throws IOException {
        String name = "NetteAttribute.latte";
        PsiFile file = parseFile(name, loadFile(name));
        LattePhpVariable definition = PsiTreeUtil.findChildOfAnyType(file, LattePhpVariable.class);
        Assert.assertNotNull(definition);

        Assert.assertSame(file, LatteUtil.getCurrentContext(definition));
    }

    @Test
    public void testHtmlTag() throws IOException {
        String name = "HtmlTag.latte";
        PsiFile file = parseFile(name, loadFile(name));
        LattePhpVariable usage = PsiTreeUtil.findChildOfAnyType(file, LattePhpVariable.class);
        Assert.assertNotNull(usage);

        Assert.assertSame(file, LatteUtil.getCurrentContext(usage));
    }

    @Test
    public void testNetteAttributeForeach() throws IOException {
        String name = "NetteAttributeForeach.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(2, variables.size());

        LattePhpVariable usage = variables.get(0);
        LattePhpVariable definition = variables.get(1);

        PsiElement context1 = LatteUtil.getCurrentContext(usage);
        Assert.assertSame(file, context1);

        PsiElement context2 = LatteUtil.getCurrentContext(definition);
        LatteHtmlTagContainer block = PsiTreeUtil.findChildOfAnyType(file, LatteHtmlTagContainer.class);
        Assert.assertNotNull(block);
        Assert.assertSame(block, context2);
    }

    @Test
    public void testNetteAttrComplexForeach() throws IOException {
        String name = "NetteAttrComplexForeach.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(3, variables.size());

        LattePhpVariable usage = variables.get(0);
        LattePhpVariable definition = variables.get(1);
        LattePhpVariable usage1 = variables.get(2);

        PsiElement context1 = LatteUtil.getCurrentContext(usage);
        Assert.assertSame(file, context1);

        PsiElement context2 = LatteUtil.getCurrentContext(definition);
        PsiElement context3 = LatteUtil.getCurrentContext(usage1);

        LatteHtmlTagContainer block = PsiTreeUtil.findChildOfAnyType(file, LatteHtmlTagContainer.class);
        Assert.assertNotNull(block);
        Assert.assertSame(block, context2);
        Assert.assertSame(block, context3);
    }

}
