package com.jantvrdik.intellij.latte.php;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.HeavyPlatformTestCase;
import com.jantvrdik.intellij.latte.BasePsiParsingTestCase;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.LattePairMacro;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.psi.impl.LatteHtmlPairTagImpl;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import org.junit.Assert;
import org.junit.Test;

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
        URL url = getClass().getClassLoader().getResource("data/php/context");
        assert url != null;
        return url.getFile();
    }

    @Test
    public void testVariable() throws IOException {
        String name = "Variable.latte";
        PsiFile file = parseFile(name, loadFile(name));
        LattePhpVariable definition = PsiTreeUtil.findChildOfAnyType(file, LattePhpVariable.class);
        Assert.assertNotNull(definition);

        Assert.assertSame(file, LattePhpVariableUtil.getCurrentContext(definition));
    }

    @Test
    public void testInBlock() throws IOException {
        String name = "InBlock.latte";
        PsiFile file = parseFile(name, loadFile(name));
        LattePhpVariable definition = PsiTreeUtil.findChildOfAnyType(file, LattePhpVariable.class);
        Assert.assertNotNull(definition);

        Assert.assertSame(file.getFirstChild(), LattePhpVariableUtil.getCurrentContext(definition));
    }

    @Test
    public void testNetteAttribute() throws IOException {
        String name = "NetteAttribute.latte";
        PsiFile file = parseFile(name, loadFile(name));
        LattePhpVariable definition = PsiTreeUtil.findChildOfAnyType(file, LattePhpVariable.class);
        Assert.assertNotNull(definition);

        Assert.assertSame(file, LattePhpVariableUtil.getCurrentContext(definition));
    }

    @Test
    public void testHtmlTag() throws IOException {
        String name = "HtmlTag.latte";
        PsiFile file = parseFile(name, loadFile(name));
        LattePhpVariable usage = PsiTreeUtil.findChildOfAnyType(file, LattePhpVariable.class);
        Assert.assertNotNull(usage);

        Assert.assertSame(file, LattePhpVariableUtil.getCurrentContext(usage));
    }

    @Test
    public void testNetteAttributeForeach() throws IOException {
        String name = "NetteAttributeForeach.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(2, variables.size());

        LattePhpVariable usage = variables.get(0);
        LattePhpVariable definition = variables.get(1);

        PsiElement context1 = LattePhpVariableUtil.getCurrentContext(usage);
        Assert.assertSame(file, context1);

        PsiElement context2 = LattePhpVariableUtil.getCurrentContext(definition);
        LatteHtmlPairTagImpl block = PsiTreeUtil.findChildOfAnyType(file, LatteHtmlPairTagImpl.class);
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

        PsiElement context1 = LattePhpVariableUtil.getCurrentContext(usage);
        Assert.assertSame(file, context1);

        PsiElement context2 = LattePhpVariableUtil.getCurrentContext(definition);
        PsiElement context3 = LattePhpVariableUtil.getCurrentContext(usage1);

        LatteHtmlPairTagImpl block = PsiTreeUtil.findChildOfAnyType(file, LatteHtmlPairTagImpl.class);
        Assert.assertNotNull(block);
        Assert.assertSame(block, context2);
        Assert.assertSame(block, context3);
    }

    @Test
    public void testVariableInNFor() throws IOException {
        String name = "VariableInNFor.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(4, variables.size());

        LattePhpVariable definition = variables.get(0);
        LattePhpVariable usage1 = variables.get(1);
        LattePhpVariable usage2 = variables.get(2);
        LattePhpVariable usage3 = variables.get(3);

        PsiElement context1 = LattePhpVariableUtil.getCurrentContext(definition);
        PsiElement context2 = LattePhpVariableUtil.getCurrentContext(usage1);
        PsiElement context3 = LattePhpVariableUtil.getCurrentContext(usage2);
        PsiElement context4 = LattePhpVariableUtil.getCurrentContext(usage3);
        Assert.assertSame(context1, context2);
        Assert.assertSame(context3, context4);
        Assert.assertNotSame(file, context1);
    }

    @Test
    public void testVariablesInDefine() throws IOException {
        String name = "VariablesInDefine.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(6, variables.size());

        LattePhpVariable definition1 = variables.get(0);
        LattePhpVariable definition2 = variables.get(1);
        LattePhpVariable definition3 = variables.get(2);
        LattePhpVariable usage1 = variables.get(3);
        LattePhpVariable usage2 = variables.get(4);
        LattePhpVariable usage3 = variables.get(5);

        LattePairMacro parentMacro = PsiTreeUtil.getParentOfType(definition1, LattePairMacro.class);
        Assert.assertNotNull(parentMacro);

        PsiElement context1 = LattePhpVariableUtil.getCurrentContext(definition1);
        PsiElement context2 = LattePhpVariableUtil.getCurrentContext(definition2);
        PsiElement context3 = LattePhpVariableUtil.getCurrentContext(definition3);
        PsiElement context4 = LattePhpVariableUtil.getCurrentContext(usage1);
        PsiElement context5 = LattePhpVariableUtil.getCurrentContext(usage2);
        PsiElement context6 = LattePhpVariableUtil.getCurrentContext(usage3);

        Assert.assertSame(parentMacro, context1);
        Assert.assertSame(parentMacro, context2);
        Assert.assertSame(parentMacro, context3);
        Assert.assertSame(parentMacro, context4);
        Assert.assertSame(parentMacro, context5);
        Assert.assertSame(parentMacro, context6);
    }

}
