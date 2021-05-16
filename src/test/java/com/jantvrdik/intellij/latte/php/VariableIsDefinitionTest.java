package com.jantvrdik.intellij.latte.php;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.HeavyPlatformTestCase;
import com.jantvrdik.intellij.latte.BasePsiParsingTestCase;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class VariableIsDefinitionTest extends BasePsiParsingTestCase {

    public VariableIsDefinitionTest() {
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
        URL url = getClass().getClassLoader().getResource("data/php/isDefinition");
        assert url != null;
        return url.getFile();
    }

    @Test
    public void testVarTypeDefinition() throws IOException {
        String name = "VarTypeDefinition.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(3, variables.size());

        LattePhpVariable varTypeDefinition = variables.get(0);
        LattePhpVariable varDefinition = variables.get(1);
        LattePhpVariable usage = variables.get(2);

        Assert.assertTrue(LattePhpVariableUtil.isVariableDefinition(varTypeDefinition));
        Assert.assertTrue(LattePhpVariableUtil.isVarTypeDefinition(varTypeDefinition));
        Assert.assertTrue(LattePhpVariableUtil.isVariableDefinition(varDefinition));
        Assert.assertFalse(LattePhpVariableUtil.isVariableDefinition(usage));
    }

    @Test
    public void testDefinitionInNForeach() throws IOException {
        String name = "DefinitionInNForeach.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(3, variables.size());

        LattePhpVariable definition1 = variables.get(0);
        LattePhpVariable usage = variables.get(1);
        LattePhpVariable definition2 = variables.get(2);

        Assert.assertTrue(LattePhpVariableUtil.isVariableDefinition(definition1));
        Assert.assertFalse(LattePhpVariableUtil.isVariableDefinition(usage));
        Assert.assertTrue(LattePhpVariableUtil.isVariableDefinition(definition2));
    }

    @Test
    public void testDefinitionInNForeachWithBefore() throws IOException {
        String name = "DefinitionInNForeachWithBefore.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(6, variables.size());

        LattePhpVariable definition1 = variables.get(0);
        LattePhpVariable usage1 = variables.get(1);
        LattePhpVariable definition2 = variables.get(2);

        LattePhpVariable definition3 = variables.get(3);
        LattePhpVariable usage2 = variables.get(4);
        LattePhpVariable definition4 = variables.get(5);

        Assert.assertTrue(LattePhpVariableUtil.isVariableDefinition(definition1));
        Assert.assertFalse(LattePhpVariableUtil.isVariableDefinition(usage1));
        Assert.assertTrue(LattePhpVariableUtil.isVariableDefinition(definition2));

        Assert.assertTrue(LattePhpVariableUtil.isVariableDefinition(definition3));
        Assert.assertFalse(LattePhpVariableUtil.isVariableDefinition(usage2));
        Assert.assertTrue(LattePhpVariableUtil.isVariableDefinition(definition4));
    }

}
