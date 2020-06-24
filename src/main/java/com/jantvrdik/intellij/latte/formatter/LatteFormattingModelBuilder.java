package com.jantvrdik.intellij.latte.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.tree.TokenSet;
import com.intellij.xml.template.formatter.AbstractXmlTemplateFormattingModelBuilder;
import com.jantvrdik.intellij.latte.LatteLanguage;
import com.jantvrdik.intellij.latte.codeStyle.LatteCodeStyleSettings;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LatteFileViewProvider;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.Nullable;

public class LatteFormattingModelBuilder extends AbstractXmlTemplateFormattingModelBuilder {

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
		return new LatteBlock(this, astNode, wrap, alignment, codeStyleSettings, xmlFormattingPolicy, indent, createSpaceBuilder(codeStyleSettings));
	}

	private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
		return new SpacingBuilder(settings, LatteLanguage.INSTANCE)
				.around(TokenSet.create(LatteTypes.T_PHP_ASSIGNMENT_OPERATOR, LatteTypes.T_PHP_DEFINITION_OPERATOR))
				.spaceIf(settings.getCommonSettings(LatteLanguage.INSTANCE.getID()).SPACE_AROUND_ASSIGNMENT_OPERATORS)

				.around(LatteTypes.T_PHP_OPERATOR)
				.spaceIf(settings.getCommonSettings(LatteLanguage.INSTANCE.getID()).SPACE_AROUND_EQUALITY_OPERATORS)

				.around(LatteTypes.T_PHP_LOGIC_OPERATOR)
				.spaceIf(settings.getCommonSettings(LatteLanguage.INSTANCE.getID()).SPACE_AROUND_LOGICAL_OPERATORS)

				.around(LatteTypes.T_PHP_RELATIONAL_OPERATOR)
				.spaceIf(settings.getCommonSettings(LatteLanguage.INSTANCE.getID()).SPACE_AROUND_RELATIONAL_OPERATORS)

				.around(LatteTypes.T_PHP_BITWISE_OPERATOR)
				.spaceIf(settings.getCommonSettings(LatteLanguage.INSTANCE.getID()).SPACE_AROUND_BITWISE_OPERATORS)

				.around(LatteTypes.T_PHP_SHIFT_OPERATOR)
				.spaceIf(settings.getCommonSettings(LatteLanguage.INSTANCE.getID()).SPACE_AROUND_SHIFT_OPERATORS)

				.around(LatteTypes.T_PHP_UNARY_OPERATOR)
				.spaceIf(settings.getCommonSettings(LatteLanguage.INSTANCE.getID()).SPACE_AROUND_UNARY_OPERATOR)

				.around(LatteTypes.T_PHP_MULTIPLICATIVE_OPERATORS)
				.spaceIf(settings.getCommonSettings(LatteLanguage.INSTANCE.getID()).SPACE_AROUND_MULTIPLICATIVE_OPERATORS)

				.around(LatteTypes.T_PHP_CONCATENATION)
				.spaceIf(LatteCodeStyleSettings.SPACE_AROUND_CONCATENATION)

				.before(LatteTypes.T_MACRO_TAG_CLOSE)
				.none()

				.before(LatteTypes.T_PHP_MACRO_SEPARATOR)
				.none()

				.after(LatteTypes.T_MACRO_NAME)
				.spaces(1)
				//.spaceIf(LatteCodeStyleSettings.SPACE_BEFORE_CLOSING_TAG)
				//.before(SimpleTypes.PROPERTY)
				//.none()
				;
	}

}
