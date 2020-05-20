package com.jantvrdik.intellij.latte.file;

import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.file.UpdateAddedFileProcessor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatteUpdateAddedFileProcessor extends UpdateAddedFileProcessor {


    @Override
    public boolean canProcessElement(@NotNull PsiFile element) {
        return false;
    }

    @Override
    public void update(PsiFile element, @Nullable PsiFile originalElement) throws IncorrectOperationException {

    }
}
