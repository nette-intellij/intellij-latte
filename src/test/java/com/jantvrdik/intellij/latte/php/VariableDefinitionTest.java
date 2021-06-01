package com.jantvrdik.intellij.latte.php;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.HeavyPlatformTestCase;
import com.jantvrdik.intellij.latte.BasePsiParsingTestCase;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.utils.LattePhpVariableDefinition;
import org.junit.Assert;
import org.junit.Test;

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
        URL url = getClass().getClassLoader().getResource("data/php/definition");
        assert url != null;
        return url.getFile();
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

        List<LattePhpVariableDefinition> definition1 = LattePhpVariableUtil.getVariableDefinition(definition);
        Assert.assertSame(0, definition1.size());

        List<LattePhpVariableDefinition> definition2 = LattePhpVariableUtil.getVariableDefinition(usage1);
        Assert.assertSame(1, definition2.size());
        Assert.assertFalse(definition2.get(0).isProbablyUndefined());
        Assert.assertSame(definition, definition2.get(0).getElement());

        List<LattePhpVariableDefinition> definition3 = LattePhpVariableUtil.getVariableDefinition(usage2);
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

        List<LattePhpVariableDefinition> definitions1 = LattePhpVariableUtil.getVariableDefinition(definition);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpVariableDefinition> definitions2 = LattePhpVariableUtil.getVariableDefinition(usage);
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

        List<LattePhpVariableDefinition> definitions1 = LattePhpVariableUtil.getVariableDefinition(definition1);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpVariableDefinition> definitions2 = LattePhpVariableUtil.getVariableDefinition(usage1a);
        Assert.assertSame(1, definitions2.size());
        Assert.assertSame(definition1, definitions2.get(0).getElement());
        Assert.assertFalse(definitions2.get(0).isProbablyUndefined());

        List<LattePhpVariableDefinition> definitions3 = LattePhpVariableUtil.getVariableDefinition(usage1b);
        Assert.assertSame(1, definitions3.size());
        Assert.assertSame(definition1, definitions3.get(0).getElement());
        Assert.assertTrue(definitions3.get(0).isProbablyUndefined());

        List<LattePhpVariableDefinition> definitions4 = LattePhpVariableUtil.getVariableDefinition(definition2);
        Assert.assertSame(0, definitions4.size());

        List<LattePhpVariableDefinition> definitions5 = LattePhpVariableUtil.getVariableDefinition(usage2a);
        Assert.assertSame(1, definitions5.size());
        Assert.assertSame(definition2, definitions5.get(0).getElement());
        Assert.assertFalse(definitions5.get(0).isProbablyUndefined());

        List<LattePhpVariableDefinition> definitions6 = LattePhpVariableUtil.getVariableDefinition(usage2b);
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

        List<LattePhpVariableDefinition> definitions1 = LattePhpVariableUtil.getVariableDefinition(definition);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpVariableDefinition> definitions2 = LattePhpVariableUtil.getVariableDefinition(usage);
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

        List<LattePhpVariableDefinition> definitions1 = LattePhpVariableUtil.getVariableDefinition(definition1);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpVariableDefinition> otherDefinitions = LattePhpVariableUtil.getVariableOtherDefinitions(definition2);
        Assert.assertSame(1, otherDefinitions.size());
        Assert.assertSame(definition1, otherDefinitions.get(0).getElement());
        Assert.assertFalse(otherDefinitions.get(0).isProbablyUndefined());

        List<LattePhpVariableDefinition> definitions2 = LattePhpVariableUtil.getVariableDefinition(usage);
        Assert.assertSame(2, definitions2.size());
        Assert.assertSame(definition1, definitions2.get(0).getElement());
        Assert.assertSame(definition2, definitions2.get(1).getElement());
        Assert.assertFalse(definitions2.get(0).isProbablyUndefined());
        Assert.assertFalse(definitions2.get(1).isProbablyUndefined());
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

        List<LattePhpVariableDefinition> definitions = LattePhpVariableUtil.getVariableDefinition(definition);
        Assert.assertSame(0, definitions.size());

        List<LattePhpVariableDefinition> definitions1 = LattePhpVariableUtil.getVariableDefinition(usage1);
        Assert.assertSame(1, definitions1.size());
        Assert.assertSame(definition, definitions1.get(0).getElement());
        Assert.assertFalse(definitions1.get(0).isProbablyUndefined());

        List<LattePhpVariableDefinition> definitions2 = LattePhpVariableUtil.getVariableDefinition(usage2);
        Assert.assertSame(1, definitions2.size());
        Assert.assertFalse(definitions2.get(0).isProbablyUndefined());

        List<LattePhpVariableDefinition> definitions3 = LattePhpVariableUtil.getVariableDefinition(usage3);
        Assert.assertSame(1, definitions3.size());
        Assert.assertFalse(definitions3.get(0).isProbablyUndefined());
    }

}
