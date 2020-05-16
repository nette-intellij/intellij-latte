package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteModifier;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import com.jantvrdik.intellij.latte.psi.LatteMacroContent;
import com.jantvrdik.intellij.latte.psi.elements.LatteMacroModifierElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LatteMacroModifierElementImpl extends ASTWrapperPsiElement implements LatteMacroModifierElement {

	public LatteMacroModifierElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Nullable
	public LatteMacroContent getMacroContent() {
		return findChildByClass(LatteMacroContent.class);
	}

	@Nullable
	public LatteModifier getMacroModifier() {
		return LatteConfiguration.INSTANCE.getModifier(getProject(), getModifierName());
	}

	@Override
	public @Nullable Icon getIcon(int flags) {
		return LatteIcons.MACRO;
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