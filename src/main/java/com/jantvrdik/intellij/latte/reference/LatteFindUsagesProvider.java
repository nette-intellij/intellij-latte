package com.jantvrdik.intellij.latte.reference;

import com.intellij.lang.cacheBuilder.*;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.*;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.lexer.LatteMacroLexerAdapter;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.*;

public class LatteFindUsagesProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(
                new LatteMacroLexerAdapter(),
                TokenSet.create(LatteTypes.PHP_VARIABLE),
                TokenSet.create(LatteTypes.MACRO_COMMENT),
                TokenSet.EMPTY
        );
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        boolean out = psiElement instanceof LattePhpVariable && ((LattePhpVariable) psiElement).isDefinition();
        return psiElement instanceof LattePhpVariable && ((LattePhpVariable) psiElement).isDefinition();
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof LattePhpVariable) {
            return "latte variable";
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof LattePhpVariable) {
            return ((LattePhpVariable) element).getVariableName();
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof LattePhpVariable) {
            return ((LattePhpVariable) element).getVariableName();
        } else {
            return "";
        }
    }
}
