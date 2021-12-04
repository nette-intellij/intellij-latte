package com.jantvrdik.intellij.latte.editor;

import com.intellij.codeInsight.editorActions.smartEnter.SmartEnterProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jetbrains.php.lang.PhpLanguage;
import org.jetbrains.annotations.NotNull;

public class LatteSmartEnterProcessor extends SmartEnterProcessor {

	@Override
	public boolean process(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
		if (!(psiFile instanceof LatteFile)) {
			return false;
		} else {
			int offset = editor.getCaretModel().getOffset() - 1;
			FileViewProvider viewProvider = psiFile.getViewProvider();
			PsiElement currElement = viewProvider.findElementAt(offset, PhpLanguage.INSTANCE);
			if (currElement instanceof PsiWhiteSpace) {
				currElement = PsiTreeUtil.prevLeaf(currElement);
			}

			if (true) {
				return false;
			} else {
				//boolean result = this.completeStatement(psiFile, editor, statement);
				//result |= completeParentStatement(psiFile, statement, editor);
				return false;
			}
		}
	}
}