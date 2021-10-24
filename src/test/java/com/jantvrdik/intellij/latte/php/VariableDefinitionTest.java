package com.jantvrdik.intellij.latte.php;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.HeavyPlatformTestCase;
import com.jantvrdik.intellij.latte.BasePsiParsingTestCase;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpVariableElement;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.utils.LattePhpCachedVariable;
import org.jetbrains.annotations.NotNull;
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

        List<LattePhpCachedVariable> definition1 = getVariableDefinition(definition);
        Assert.assertSame(0, definition1.size());

        List<LattePhpCachedVariable> definition2 = getVariableDefinition(usage1);
        Assert.assertSame(1, definition2.size());
        Assert.assertFalse(definition2.get(0).isProbablyUndefined());
        Assert.assertSame(definition, definition2.get(0).getElement());

        List<LattePhpCachedVariable> definition3 = getVariableDefinition(usage2);
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

        List<LattePhpCachedVariable> definitions1 = getVariableDefinition(definition);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpCachedVariable> definitions2 = getVariableDefinition(usage);
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

        List<LattePhpCachedVariable> definitions1 = getVariableDefinition(definition1);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpCachedVariable> definitions2 = getVariableDefinition(usage1a);
        Assert.assertSame(1, definitions2.size());
        Assert.assertSame(definition1, definitions2.get(0).getElement());
        Assert.assertFalse(definitions2.get(0).isProbablyUndefined());

        List<LattePhpCachedVariable> definitions3 = getVariableDefinition(usage1b);
        Assert.assertSame(1, definitions3.size());
        Assert.assertSame(definition1, definitions3.get(0).getElement());
        Assert.assertTrue(definitions3.get(0).isProbablyUndefined());

        List<LattePhpCachedVariable> definitions4 = getVariableDefinition(definition2);
        Assert.assertSame(0, definitions4.size());

        List<LattePhpCachedVariable> definitions5 = getVariableDefinition(usage2a);
        Assert.assertSame(1, definitions5.size());
        Assert.assertSame(definition2, definitions5.get(0).getElement());
        Assert.assertFalse(definitions5.get(0).isProbablyUndefined());

        List<LattePhpCachedVariable> definitions6 = getVariableDefinition(usage2b);
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

        List<LattePhpCachedVariable> definitions1 = getVariableDefinition(definition);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpCachedVariable> definitions2 = getVariableDefinition(usage);
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

        List<LattePhpCachedVariable> definitions1 = getVariableDefinition(definition1);
        Assert.assertSame(0, definitions1.size());

        List<LattePhpCachedVariable> definitions2 = getVariableDefinition(usage);
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

        List<LattePhpCachedVariable> definitions = getVariableDefinition(definition);
        Assert.assertSame(0, definitions.size());

        List<LattePhpCachedVariable> definitions1 = getVariableDefinition(usage1);
        Assert.assertSame(1, definitions1.size());
        Assert.assertSame(definition, definitions1.get(0).getElement());
        Assert.assertFalse(definitions1.get(0).isProbablyUndefined());

        List<LattePhpCachedVariable> definitions2 = getVariableDefinition(usage2);
        Assert.assertSame(1, definitions2.size());
        Assert.assertFalse(definitions2.get(0).isProbablyUndefined());

        List<LattePhpCachedVariable> definitions3 = getVariableDefinition(usage3);
        Assert.assertSame(1, definitions3.size());
        Assert.assertFalse(definitions3.get(0).isProbablyUndefined());
    }

    private List<LattePhpCachedVariable> getVariableDefinition(@NotNull LattePhpVariableElement element) {
        PsiFile file = element.getContainingFile();
        assert file instanceof LatteFile;
        return ((LatteFile) file).getCachedVariableDefinitions(element);
    }

}
