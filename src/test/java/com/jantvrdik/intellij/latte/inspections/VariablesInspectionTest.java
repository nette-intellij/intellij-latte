package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.HeavyPlatformTestCase;
import com.jantvrdik.intellij.latte.BasePsiParsingTestCase;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.inspections.utils.LatteInspectionInfo;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class VariablesInspectionTest extends BasePsiParsingTestCase {

    public VariablesInspectionTest() {
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
        URL url = getClass().getClassLoader().getResource("data/inspections/variables");
        assert url != null;
        return url.getFile();
    }

    @Test
    public void testUndefinedVariable() throws IOException {
        List<LatteInspectionInfo> problems = getProblems("UndefinedVariable.latte");

        Assert.assertNotNull(problems);
        Assert.assertSame(1, problems.size());

        Assert.assertEquals("Undefined variable 'foo'", problems.get(0).getDescription());
        Assert.assertEquals(ProblemHighlightType.GENERIC_ERROR_OR_WARNING, problems.get(0).getType());
    }

    @Test
    public void testProbablyUndefinedVariable() throws IOException {
        List<LatteInspectionInfo> problems = getProblems("ProbablyUndefinedVariable.latte");

        Assert.assertNotNull(problems);
        Assert.assertSame(1, problems.size());

        Assert.assertEquals("Variable 'bar' is probably undefined", problems.get(0).getDescription());
        Assert.assertEquals(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL, problems.get(0).getType());
    }

    @Test
    public void testMultipleDefinitions() throws IOException {
        List<LatteInspectionInfo> problems = getProblems("MultipleDefinitions.latte");

        Assert.assertNotNull(problems);
        Assert.assertSame(2, problems.size());

        Assert.assertEquals("Multiple definitions for variable 'foo'", problems.get(0).getDescription());
        Assert.assertEquals(ProblemHighlightType.WARNING, problems.get(0).getType());

        Assert.assertEquals("Multiple definitions for variable 'foo'", problems.get(1).getDescription());
        Assert.assertEquals(ProblemHighlightType.WARNING, problems.get(1).getType());
    }

    @Test
    public void testDefinitionsInAnotherContext() throws IOException {
        List<LatteInspectionInfo> problems = getProblems("DefinitionsInAnotherContext.latte");

        Assert.assertNotNull(problems);
        Assert.assertSame(1, problems.size());

        Assert.assertEquals("Unused variable 'foo'", problems.get(0).getDescription());
        Assert.assertEquals(ProblemHighlightType.LIKE_UNUSED_SYMBOL, problems.get(0).getType());
    }

    @Test
    public void testVariableInNFor() throws IOException {
        List<LatteInspectionInfo> problems = getProblems("VariableInNFor.latte");

        Assert.assertNotNull(problems);
        Assert.assertSame(0, problems.size());
    }

    @Test
    public void testVariableInBlock() throws IOException {
        List<LatteInspectionInfo> problems = getProblems("VariableInBlock.latte");

        Assert.assertNotNull(problems);
        Assert.assertSame(2, problems.size());
        Assert.assertNotEquals(problems.get(0).getElement(), problems.get(1).getElement());

        Assert.assertEquals("Unused variable 'foo'", problems.get(0).getDescription());
        Assert.assertEquals(ProblemHighlightType.LIKE_UNUSED_SYMBOL, problems.get(0).getType());

        Assert.assertEquals("Unused variable 'foo'", problems.get(1).getDescription());
        Assert.assertEquals(ProblemHighlightType.LIKE_UNUSED_SYMBOL, problems.get(1).getType());
    }

    private List<LatteInspectionInfo> getProblems(@NotNull String templateName) throws IOException {
        PsiFile file = parseFile(templateName);

        return (new VariablesInspection()).checkFile(file);
    }

}
