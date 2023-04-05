package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.psi.LatteMacroContent;
import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import com.jantvrdik.intellij.latte.psi.LattePhpContent;
import com.jantvrdik.intellij.latte.psi.elements.LatteMacroContentElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePairMacroElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiElementImpl;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LatteMacroContentImpl extends LattePsiElementImpl implements LatteMacroContentElement {

	public LatteMacroContentImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public @Nullable LattePhpContent getFirstPhpContent() {
		return LattePsiImplUtil.getFirstPhpContent(this);
	}

	@Override
	public @Nullable PsiElement getMacroNameElement() {
		return LattePsiImplUtil.getMacroNameElement(this);
	}
}