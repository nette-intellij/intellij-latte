package com.jantvrdik.intellij.latte.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LatteFileViewProvider;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.Nullable;

public class LatteFormattingModelBuilder extends AbstractTemplateLanguageFormattingModelBuilder {


	@Override
	protected boolean isTemplateFile(PsiFile psiFile) {
		return psiFile instanceof LatteFile;
	}

	@Override
	public boolean isOuterLanguageElement(PsiElement psiElement) {
		return psiElement.getNode().getElementType() == LatteFileViewProvider.OUTER_LATTE;
	}

	@Override
	public boolean isMarkupLanguageElement(PsiElement psiElement) {
		return psiElement.getNode().getElementType() == LatteTypes.OUTER_HTML;
	}

	@Override
	protected Block createTemplateLanguageBlock(ASTNode astNode, CodeStyleSettings codeStyleSettings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent, @Nullable Alignment alignment, @Nullable Wrap wrap) {
		return new LatteBlock(this, astNode, wrap, alignment, codeStyleSettings, xmlFormattingPolicy, indent);
	}

}
