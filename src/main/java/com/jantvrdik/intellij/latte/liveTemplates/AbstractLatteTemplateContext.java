package com.jantvrdik.intellij.latte.liveTemplates;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiUtilCore;
import com.jetbrains.php.lang.PhpLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;

abstract public class AbstractLatteTemplateContext extends TemplateContextType {
	protected AbstractLatteTemplateContext(@NotNull String id, @NotNull String presentableName) {
		super(id, presentableName);
	}

	@Override
	public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
		PsiFile file = templateActionContext.getFile();
		int offset = templateActionContext.getStartOffset();
		if (PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(PhpLanguage.INSTANCE)) {
			PsiElement element = file.findElementAt(offset);
			if (element instanceof PsiWhiteSpace && offset > 0) {
				element = file.findElementAt(offset - 1);
			}

			return element != null && this.isInContext(element);
		} else {
			return false;
		}
	}

	protected abstract boolean isInContext(@NotNull PsiElement element);
}