package com.jantvrdik.intellij.latte.psi;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.HeavyPlatformTestCase;
import com.intellij.testFramework.TestDataFile;
import com.jantvrdik.intellij.latte.BasePsiParsingTestCase;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.utils.LattePhpVariableDefinition;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class VariableDefinitionTest extends BasePsiParsingTestCase {

    public VariableDefinitionTest() {
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
        URL url = getClass().getClassLoader().getResource("data/psi/definition");
        assert url != null;
        return url.getFile();
    }

    protected String loadFile(@NotNull @NonNls @TestDataFile String name) throws IOException {
        return FileUtil.loadFile(new File(myFullDataPath, name), CharsetToolkit.UTF8, true);
    }

    @Test
    public void testVariable() throws IOException {
        String name = "Variable.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(3, variables.size());

        LattePhpVariable definition = variables.get(0);
        LattePhpVariable usage1 = variables.get(1);
        LattePhpVariable usage2 = variables.get(2);

        List<LattePhpVariableDefinition> definition1 = LatteUtil.getVariableDefinition(definition);
        Assert.assertSame(0, definition1.size());

        List<LattePhpVariableDefinition> definition2 = LatteUtil.getVariableDefinition(usage1);
        Assert.assertSame(1, definition2.size());
        Assert.assertFalse(definition2.get(0).isProbablyUndefined());
        Assert.assertSame(definition, definition2.get(0).getElement());

        List<LattePhpVariableDefinition> definition3 = LatteUtil.getVariableDefinition(usage2);
        Assert.assertSame(1, definition3.size());
        Assert.assertFalse(definition3.get(0).isProbablyUndefined());
        Assert.assertSame(definition, definition3.get(0).getElement());
    }

    @Test
    public void testVariableInside() throws IOException {
        String name = "VariableInside.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(2, variables.size());

        LattePhpVariable definition = variables.get(0);
        LattePhpVariable usage = variables.get(1);

        List<LattePhpVariableDefinition> definitions1 = LatteUtil.getVariableDefinition(definition);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpVariableDefinition> definitions2 = LatteUtil.getVariableDefinition(usage);
        Assert.assertSame(1, definitions2.size());
        Assert.assertSame(definition, definitions2.get(0).getElement());
        Assert.assertFalse(definitions2.get(0).isProbablyUndefined());
    }

    @Test
    public void testBlockWithNAttr() throws IOException {
        String name = "BlockWithNAttr.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(6, variables.size());

        LattePhpVariable definition1 = variables.get(0);
        LattePhpVariable usage1a = variables.get(1);
        LattePhpVariable usage1b = variables.get(2);
        LattePhpVariable definition2 = variables.get(3);
        LattePhpVariable usage2a = variables.get(4);
        LattePhpVariable usage2b = variables.get(5);

        List<LattePhpVariableDefinition> definitions1 = LatteUtil.getVariableDefinition(definition1);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpVariableDefinition> definitions2 = LatteUtil.getVariableDefinition(usage1a);
        Assert.assertSame(1, definitions2.size());
        Assert.assertSame(definition1, definitions2.get(0).getElement());
        Assert.assertFalse(definitions2.get(0).isProbablyUndefined());

        List<LattePhpVariableDefinition> definitions3 = LatteUtil.getVariableDefinition(usage1b);
        Assert.assertSame(1, definitions3.size());
        Assert.assertSame(definition1, definitions3.get(0).getElement());
        Assert.assertTrue(definitions3.get(0).isProbablyUndefined());

        List<LattePhpVariableDefinition> definitions4 = LatteUtil.getVariableDefinition(definition2);
        Assert.assertSame(0, definitions4.size());

        List<LattePhpVariableDefinition> definitions5 = LatteUtil.getVariableDefinition(usage2a);
        Assert.assertSame(1, definitions5.size());
        Assert.assertSame(definition2, definitions5.get(0).getElement());
        Assert.assertFalse(definitions5.get(0).isProbablyUndefined());

        List<LattePhpVariableDefinition> definitions6 = LatteUtil.getVariableDefinition(usage2b);
        Assert.assertSame(1, definitions6.size());
        Assert.assertSame(definition2, definitions6.get(0).getElement());
        Assert.assertTrue(definitions6.get(0).isProbablyUndefined());
    }

    @Test
    public void testBlockInIf() throws IOException {
        String name = "BlockInIf.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(2, variables.size());

        LattePhpVariable definition = variables.get(0);
        LattePhpVariable usage = variables.get(1);

        List<LattePhpVariableDefinition> definitions1 = LatteUtil.getVariableDefinition(definition);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpVariableDefinition> definitions2 = LatteUtil.getVariableDefinition(usage);
        Assert.assertSame(1, definitions2.size());
        Assert.assertSame(definition, definitions2.get(0).getElement());
        Assert.assertFalse(definitions2.get(0).isProbablyUndefined());
    }

    @Test
    public void testOtherVariables() throws IOException {
        String name = "OtherVariables.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(3, variables.size());

        LattePhpVariable definition1 = variables.get(0);
        LattePhpVariable definition2 = variables.get(1);
        LattePhpVariable usage = variables.get(2);

        List<LattePhpVariableDefinition> definitions1 = LatteUtil.getVariableDefinition(definition1);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpVariableDefinition> otherDefinitions = LatteUtil.getVariableOtherDefinitions(definition2);
        Assert.assertSame(1, otherDefinitions.size());
        Assert.assertSame(definition1, otherDefinitions.get(0).getElement());
        Assert.assertFalse(otherDefinitions.get(0).isProbablyUndefined());

        List<LattePhpVariableDefinition> definitions2 = LatteUtil.getVariableDefinition(usage);
        Assert.assertSame(2, definitions2.size());
        Assert.assertSame(definition1, definitions2.get(0).getElement());
        Assert.assertSame(definition2, definitions2.get(1).getElement());
        Assert.assertFalse(definitions2.get(0).isProbablyUndefined());
        Assert.assertFalse(definitions2.get(1).isProbablyUndefined());
    }

}
