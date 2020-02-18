package com.jantvrdik.intellij.latte.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.tree.IElementType;
import com.intellij.webcore.template.formatter.AbstractTemplateLanguageFormattingModelBuilder;
import com.intellij.webcore.template.formatter.TemplateLanguageBlock;
import com.jantvrdik.intellij.latte.psi.LatteMacroClassic;
import com.jantvrdik.intellij.latte.psi.LatteMacroOpenTag;
import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatteBlock extends TemplateLanguageBlock {

	private boolean isPair;

	public LatteBlock(AbstractTemplateLanguageFormattingModelBuilder abstractTemplateLanguageFormattingModelBuilder, @NotNull ASTNode astNode, @Nullable Wrap wrap, @Nullable Alignment alignment, CodeStyleSettings codeStyleSettings, XmlFormattingPolicy xmlFormattingPolicy, Indent indent) {
		super(abstractTemplateLanguageFormattingModelBuilder, astNode, wrap, alignment, codeStyleSettings, xmlFormattingPolicy, indent);
		if (getNode().getFirstChildNode() == null) {
			isPair = false;
		} else {
			IElementType lastType = getNode().getLastChildNode().getElementType();
			IElementType firstType = getNode().getFirstChildNode().getElementType();
			isPair = firstType == LatteTypes.MACRO_OPEN_TAG && lastType == LatteTypes.MACRO_CLOSE_TAG;
		}
	}

	@NotNull
	@Override
	protected Indent getChildIndent(@NotNull ASTNode astNode) {
		if (isBellowType(astNode, LatteTypes.MACRO_CONTENT)
			&& astNode.getTreePrev() != null
			&& astNode.getTreePrev().getElementType() == TokenType.WHITE_SPACE) {
			return Indent.getNormalIndent();
		}
		if (!isPair || isOpening(astNode) || isClosing(astNode)) {
			return Indent.getNoneIndent();
		}
		if (!(astNode.getPsi() instanceof LatteMacroClassic)) {
			return Indent.getNormalIndent();
		}
		PsiElement el = astNode.getPsi();
		LatteMacroTag openTag = ((LatteMacroClassic) el).getOpenTag();
		if (openTag.matchMacroName("else") || openTag.matchMacroName("elseif")) {
			return Indent.getNoneIndent();
		}
		return Indent.getNormalIndent();
	}

	@Override
	protected Spacing getSpacing(TemplateLanguageBlock templateLanguageBlock) {
		return null;
	}

	@Nullable
	@Override
	public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
		return null;
	}

	private boolean isOpening(ASTNode node) {
		return isBellowType(node, LatteTypes.MACRO_OPEN_TAG);
	}

	private boolean isClosing(ASTNode node) {
		return isBellowType(node, LatteTypes.MACRO_CLOSE_TAG);
	}

	private boolean isBellowType(ASTNode node, IElementType type) {
		do {
			if (node.getElementType() == type) {
				return true;
			}
			if (node == getNode()) {
				return false;
			}
			node = node.getTreeParent();
		} while (node != null);
		return false;
	}

}
