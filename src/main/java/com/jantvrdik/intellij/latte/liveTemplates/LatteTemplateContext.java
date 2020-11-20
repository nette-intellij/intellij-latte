package com.jantvrdik.intellij.latte.liveTemplates;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatteTemplateContext extends TemplateContextType {
	protected LatteTemplateContext() {
		super("LATTE", "Latte");
	}

	@Override
	public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
		return templateActionContext.getFile().getName().endsWith(".latte");
	}

	@Override
	public @Nullable TemplateContextType getBaseContextType() {
		return super.getBaseContextType();
	}
}