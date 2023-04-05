package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.php.LattePhpTypeDetector;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.LatteHtmlOpenTag;
import com.jantvrdik.intellij.latte.psi.LatteHtmlTagContainer;
import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import com.jantvrdik.intellij.latte.psi.LattePairMacro;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.psi.elements.LatteHtmlTagContainerElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpStatementElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiElementImpl;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LatteHtmlTagContainerImpl extends LattePsiElementImpl implements LatteHtmlTagContainerElement {

	public LatteHtmlTagContainerImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public LatteHtmlOpenTag getHtmlOpenTag() {
		return LattePsiImplUtil.getHtmlOpenTag(this);
	}
}