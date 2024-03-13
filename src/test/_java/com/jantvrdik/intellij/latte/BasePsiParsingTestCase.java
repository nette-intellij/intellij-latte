package com.jantvrdik.intellij.latte;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.testFramework.ParsingTestCase;
import com.intellij.testFramework.TestDataFile;
import com.jantvrdik.intellij.latte.parser.LatteParserDefinition;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract public class BasePsiParsingTestCase extends ParsingTestCase {

    protected BasePsiParsingTestCase() {
        super("", "latte", new LatteParserDefinition());
    }

    protected String loadFile(@NotNull @NonNls @TestDataFile String name) throws IOException {
        return FileUtil.loadFile(new File(myFullDataPath, name), CharsetToolkit.UTF8, true);
    }

    protected PsiFile parseFile(@NotNull String fileName) throws IOException {
        return parseFile(fileName, loadFile(fileName));
    }

    protected List<LattePhpVariable> collectVariables(PsiElement parent) {
        List<LattePhpVariable> variables = new ArrayList<>();
        parent.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof LattePhpVariable) {
                    variables.add((LattePhpVariable) element);
                } else {
                    super.visitElement(element);
                }
            }
        });
        return variables;
    }

}
