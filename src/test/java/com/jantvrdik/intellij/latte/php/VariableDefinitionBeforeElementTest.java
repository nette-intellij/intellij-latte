package com.jantvrdik.intellij.latte.php;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.HeavyPlatformTestCase;
import com.jantvrdik.intellij.latte.BasePsiParsingTestCase;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.utils.PsiCachedElement;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class VariableDefinitionBeforeElementTest extends BasePsiParsingTestCase {

    public VariableDefinitionBeforeElementTest() {
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
        URL url = getClass().getClassLoader().getResource("data/php/definitionBefore");
        assert url != null;
        return url.getFile();
    }

    @Test
    public void testSimpleDefinition() throws IOException {
        String name = "SimpleDefinition.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(2, variables.size());

        LattePhpVariable definition = variables.get(0);
        LattePhpVariable usage = variables.get(1);

        List<PsiCachedElement> definitions = LattePhpVariableUtil.getVariablesDefinitionsBeforeElement(usage);
        Assert.assertSame(1, definitions.size());
        Assert.assertSame(definition, definitions.get(0).getElement());
    }

    @Test
    public void testDefinitionInBlock() throws IOException {
        String name = "DefinitionInBlock.latte";
        PsiFile file = parseFile(name, loadFile(name));
        List<LattePhpVariable> variables = collectVariables(file);
        Assert.assertSame(3, variables.size());

        LattePhpVariable definition1 = variables.get(0);
        LattePhpVariable definition2 = variables.get(1);
        LattePhpVariable usage = variables.get(2);

        List<PsiCachedElement> definitions = LattePhpVariableUtil.getVariablesDefinitionsBeforeElement(usage);
        Assert.assertSame(2, definitions.size());
        Assert.assertSame(definition1, definitions.get(0).getElement());
        Assert.assertSame(definition2, definitions.get(1).getElement());
    }

}
