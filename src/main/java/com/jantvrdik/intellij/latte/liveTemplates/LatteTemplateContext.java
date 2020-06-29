package com.jantvrdik.intellij.latte.liveTemplates;

import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class LatteTemplateContext extends TemplateContextType {
	protected LatteTemplateContext() {
		super("LATTE", "Latte");
	}

	@Override
	public boolean isInContext(@NotNull PsiFile file, int offset) {
		return file.getName().endsWith(".latte");
	}
}