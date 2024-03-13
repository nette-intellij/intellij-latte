package com.jantvrdik.intellij.latte.php.variables;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.HeavyPlatformTestCase;
import com.jantvrdik.intellij.latte.BasePsiParsingTestCase;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.php.LattePhpTypeDetector;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PhpVariableTypeTest extends BasePsiParsingTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // initialize configuration with test configuration
        LatteConfiguration.getInstance(getProject());
        getProject().registerService(LatteSettings.class);
    }

    @Override
    protected String getTestDataPath() {
        URL url = getClass().getClassLoader().getResource("data/php/variables/variableType");
        assert url != null;
        return url.getFile();
    }

    @Test
    public void testArrayDefinition() throws IOException {
        String name = "ArrayDefinition.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(2, variables.size());

        LattePhpVariable varDefinition = variables.get(0);
        LattePhpVariable varUsage = variables.get(1);

        Assert.assertEquals("array", detectPhpType(varUsage));
        Assert.assertEquals("array", detectPhpType(varDefinition));
    }

    @Test
    public void testBlockDefinition() throws IOException {
        String name = "BlockDefinition.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(3, variables.size());

        LattePhpVariable nameDefinition = variables.get(0);
        LattePhpVariable valueDefinition = variables.get(1);
        LattePhpVariable typeDefinition = variables.get(2);

        Assert.assertEquals("float", detectPhpType(nameDefinition));
        Assert.assertEquals("int[]", detectPhpType(valueDefinition));
        Assert.assertEquals("string", detectPhpType(typeDefinition));
    }

    @Test
    public void testForDefinition() throws IOException {
        String name = "ForDefinition.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(3, variables.size());

        LattePhpVariable xDefinition = variables.get(0);
        LattePhpVariable xCondition = variables.get(1);
        LattePhpVariable xIncrease = variables.get(2);

        Assert.assertEquals("int", detectPhpType(xDefinition));
        Assert.assertEquals("int", detectPhpType(xCondition));
        Assert.assertEquals("int", detectPhpType(xIncrease));
    }

    @Test
    public void testTypedDefinition() throws IOException {
        String name = "TypedDefinition.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(3, variables.size());

        LattePhpVariable fooDefinition = variables.get(0);
        LattePhpVariable barDefinition = variables.get(1);
        LattePhpVariable barUsage = variables.get(2);

        Assert.assertEquals("int[]", detectPhpType(fooDefinition));
        Assert.assertEquals("int", detectPhpType(barDefinition));
        Assert.assertEquals("int", detectPhpType(barUsage));
    }

    @Test
    public void testVariableArrayDefinition() throws IOException {
        String name = "ClassDefinition.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(5, variables.size());

        LattePhpVariable fooDefinition = variables.get(0);
        LattePhpVariable barDefinition = variables.get(1);
        LattePhpVariable fooUsage = variables.get(2);
        LattePhpVariable pdoDefinition = variables.get(3);
        LattePhpVariable barUsage = variables.get(4);

        Assert.assertEquals("\\PDO[][]", detectPhpType(fooDefinition));
        Assert.assertEquals("\\PDO[]", detectPhpType(barDefinition));
        Assert.assertEquals("\\PDO[]", detectPhpType(fooUsage));
        Assert.assertEquals("\\PDO", detectPhpType(pdoDefinition));
        Assert.assertEquals("\\PDO", detectPhpType(barUsage));
    }

    @Test
    public void testForeachDefinition() throws IOException {
        assertForeachReferenceDefinition("ForeachDefinition.latte");
    }

    @Test
    public void testForeachReferenceDefinition() throws IOException {
        assertForeachReferenceDefinition("ForeachReferenceDefinition.latte");
    }

    @Test
    public void testForeachArrowDefinition() throws IOException {
        assertArrowForeachDefinition("ForeachArrowDefinition.latte");
    }

    @Test
    public void testNForeachArrowDefinition() throws IOException {
        assertArrowForeachDefinition("NForeachArrowDefinition.latte");
    }

    @Test
    public void testForeachNestedArrayDefinition() throws IOException {
        String name = "ForeachNestedArrayDefinition.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(4, variables.size());

        LattePhpVariable fooDefinition = variables.get(0);
        LattePhpVariable fooUsage = variables.get(1);
        LattePhpVariable barDefinition = variables.get(2);
        LattePhpVariable testDefinition = variables.get(3);

        Assert.assertEquals("\\PDO[][]", detectPhpType(fooDefinition));
        Assert.assertEquals("\\PDO[][]", detectPhpType(fooUsage));
        Assert.assertEquals("\\PDO", detectPhpType(barDefinition));
        Assert.assertEquals("\\PDO", detectPhpType(testDefinition));
    }

    @Test
    public void testForeachArrowNestedArrayDefinition() throws IOException {
        String name = "ForeachArrowNestedArrayDefinition.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(5, variables.size());

        LattePhpVariable fooDefinition = variables.get(0);
        LattePhpVariable fooUsage = variables.get(1);
        LattePhpVariable keyDefinition = variables.get(2);
        LattePhpVariable barDefinition = variables.get(3);
        LattePhpVariable testDefinition = variables.get(4);

        Assert.assertEquals("\\PDO[][]", detectPhpType(fooDefinition));
        Assert.assertEquals("\\PDO[][]", detectPhpType(fooUsage));
        Assert.assertEquals("mixed", detectPhpType(keyDefinition));
        Assert.assertEquals("\\PDO", detectPhpType(barDefinition));
        Assert.assertEquals("\\PDO", detectPhpType(testDefinition));
    }

    @Test
    public void testParametersDefinition() throws IOException {
        String name = "ParametersDefinition.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(4, variables.size());

        LattePhpVariable aDefinition = variables.get(0);
        LattePhpVariable bDefinition = variables.get(1);
        LattePhpVariable cDefinition = variables.get(2);
        LattePhpVariable dDefinition = variables.get(3);

        //Assert.assertEquals("mixed", detectPhpType(aDefinition)); todo: must be mixed (not int)
        Assert.assertEquals("null|int", detectPhpType(bDefinition));
        Assert.assertEquals("null|\\DateTime", detectPhpType(cDefinition));
        Assert.assertEquals("int|string", detectPhpType(dDefinition));
    }
/*
    todo: uncomment after resolve PHP dependencies (for native php classes)
    @Test
    public void testMethodDefinition() throws IOException {
        String name = "MethodDefinition.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(3, variables.size());

        LattePhpVariable fooDefinition = variables.get(0);
        LattePhpVariable barDefinition = variables.get(1);
        LattePhpVariable fooUsage = variables.get(2);

        //Assert.assertEquals("\\DateTime[]", detectPhpType(fooDefinition));
        Assert.assertEquals("string", detectPhpType(barDefinition));
        //Assert.assertEquals("\\DateTime", detectPhpType(fooUsage));
    }
*/
    public void assertForeachReferenceDefinition(@NotNull String name) throws IOException {
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(3, variables.size());

        LattePhpVariable fooDefinition = variables.get(0);
        LattePhpVariable fooUsage = variables.get(1);
        LattePhpVariable barDefinition = variables.get(2);

        Assert.assertEquals("\\PDO[]", detectPhpType(fooDefinition));
        Assert.assertEquals("\\PDO[]", detectPhpType(fooUsage));
        Assert.assertEquals("\\PDO", detectPhpType(barDefinition));
    }

    public void assertArrowForeachDefinition(@NotNull String name) throws IOException {
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(4, variables.size());

        LattePhpVariable fooDefinition = variables.get(0);
        LattePhpVariable fooUsage = variables.get(1);
        LattePhpVariable keyDefinition = variables.get(2);
        LattePhpVariable barDefinition = variables.get(3);

        Assert.assertEquals("\\PDO[][]", detectPhpType(fooDefinition));
        Assert.assertEquals("\\PDO[][]", detectPhpType(fooUsage));
        Assert.assertEquals("mixed", detectPhpType(keyDefinition));
        Assert.assertEquals("\\PDO[]", detectPhpType(barDefinition));
    }

    private static @NotNull String detectPhpType(@NotNull PsiElement element) {
        return LattePhpTypeDetector.detectPhpType(element).toString();
    }

}
