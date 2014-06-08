package com.jantvrdik.intellij.latte.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LatteFoldingBuilder extends FoldingBuilderEx {
	@SuppressWarnings("unchecked")
	@NotNull
	@Override
	public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
		List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();

		if (!quick) {
			Collection<PsiElement> nodes = PsiTreeUtil.findChildrenOfAnyType(root, LatteMacroClassic.class, LattePairHtmlTag.class);
			for (PsiElement node : nodes) {
				descriptors.add(new FoldingDescriptor(node, TextRange.create(node.getTextRange())));
			}
		}

		return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
	}

	@Nullable
	@Override
	public String getPlaceholderText(@NotNull ASTNode node) {
		PsiElement psi = node.getPsi();
		if (psi instanceof LatteMacroClassic) {
			String macroName = ((LatteMacroClassic) psi).getOpenTag().getMacroName();
			return "{" + macroName + "}";

		} else if (psi instanceof LattePairHtmlTag) {
			LattePairHtmlTagOpen htmlTagOpen = ((LattePairHtmlTag) psi).getPairHtmlTagOpen();
			LatteAnyAttrs attrs = htmlTagOpen.getAnyAttrs();
			String placeholder = "<" + htmlTagOpen.getTagName();

			if (attrs != null) {
				for (LatteHtmlAttr htmlAttr : attrs.getHtmlAttrList()) {
					String htmlAttrName = htmlAttr.getAttrName().getText().toLowerCase();
					if (htmlAttrName.equals("class")) {
						placeholder += "." + getHtmlTagValue(htmlAttr).trim().replaceAll("\\s+", ".");
					} else if (htmlAttrName.equals("id")) {
						placeholder += "#" + getHtmlTagValue(htmlAttr).trim();
					}
				}
			}

			placeholder += ">";
			return placeholder;
		}

		return null;
	}

	@NotNull
	private String getHtmlTagValue(LatteHtmlAttr htmlAttr) {
		LatteHtmlAttrValue attrValue = htmlAttr.getAttrValue();
		if (attrValue != null) {
			TokenSet tokens = TokenSet.create(LatteTypes.T_HTML_TAG_ATTR_SQ_VALUE, LatteTypes.T_HTML_TAG_ATTR_DQ_VALUE, LatteTypes.T_HTML_TAG_ATTR_UQ_VALUE);
			ASTNode token = attrValue.getNode().findChildByType(tokens);
			if (token != null) {
				return token.getText();
			}
		}
		return "";
	}

	@Override
	public boolean isCollapsedByDefault(@NotNull ASTNode node) {
		return false;
	}
}
