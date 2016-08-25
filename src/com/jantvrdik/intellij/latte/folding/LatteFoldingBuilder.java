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
			Collection<LatteMacroClassic> nodes = PsiTreeUtil.findChildrenOfAnyType(root, LatteMacroClassic.class);
			for (PsiElement node : nodes) {
				int start = node.getFirstChild().getTextRange().getEndOffset();
				int end = node.getLastChild().getTextRange().getEndOffset();
				if (end == start) {
					continue;
				}
				if (node instanceof LatteMacroClassic) {
					start--;
					end--;
				}

				descriptors.add(new FoldingDescriptor(node, TextRange.create(start, end)));

			}
		}

		return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
	}

	@Nullable
	@Override
	public String getPlaceholderText(@NotNull ASTNode node) {
		return null;
	}

	@Override
	public boolean isCollapsedByDefault(@NotNull ASTNode node) {
		return false;
	}
}
