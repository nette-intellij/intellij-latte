package com.jantvrdik.intellij.latte.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpStatementPartElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LatteBaseStubPhpElementImpl<T extends StubElement> extends LatteBaseStubElementImpl<T> implements BaseLattePhpElement, StubBasedPsiElement<T> {

	public LatteBaseStubPhpElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LatteBaseStubPhpElementImpl(final T stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public String getName() {
		return getPhpElementName();
	}

	@Override
	public @Nullable PsiElement getTextElement() {
		return getNameIdentifier();
	}

	public int getPhpArrayLevel() {
		return this.getPhpArrayUsageList().size();
	}

	@Override
	public @Nullable LattePhpStatementPartElement getPhpStatementPart() {
		PsiElement parent = this.getParent();
		return parent instanceof LattePhpStatementPartElement ? (LattePhpStatementPartElement) parent : null;
	}
}