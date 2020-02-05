package com.jantvrdik.intellij.latte.reference;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatteBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] PAIRS = new BracePair[]{
            new BracePair(LatteTypes.T_PHP_LEFT_BRACKET, LatteTypes.T_PHP_RIGHT_BRACKET, false),
            new BracePair(LatteTypes.T_PHP_LEFT_CURLY_BRACE, LatteTypes.T_PHP_RIGHT_CURLY_BRACE, true),
            new BracePair(LatteTypes.T_PHP_LEFT_NORMAL_BRACE, LatteTypes.T_PHP_RIGHT_NORMAL_BRACE, false),
    };

    @NotNull
    @Override
    public BracePair[] getPairs() {
        return PAIRS;
    }

    @Override
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType contextType) {
        return true;
    }

    @Override
    public int getCodeConstructStart(PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }

}