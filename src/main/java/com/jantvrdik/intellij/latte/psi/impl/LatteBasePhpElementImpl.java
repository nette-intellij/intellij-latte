package com.jantvrdik.intellij.latte.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpStatementPartElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LatteBasePhpElementImpl extends ASTWrapperPsiElement implements BaseLattePhpElement {

	public LatteBasePhpElementImpl(@NotNull ASTNode node) {
		super(node);
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

	@Nullable
	public PsiReference getReference() {
		PsiReference[] references = getReferences();
		return references.length == 0 ? null : references[0];
	}

	@NotNull
	public PsiReference[] getReferences() {
		return ReferenceProvidersRegistry.getReferencesFromProviders(this);
	}
}