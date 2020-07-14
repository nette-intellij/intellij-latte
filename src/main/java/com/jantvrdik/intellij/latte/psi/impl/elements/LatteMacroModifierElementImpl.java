package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.stubs.IStubElementType;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import com.jantvrdik.intellij.latte.indexes.stubs.LatteFilterStub;
import com.jantvrdik.intellij.latte.psi.LatteMacroContent;
import com.jantvrdik.intellij.latte.psi.elements.LatteMacroModifierElement;
import com.jantvrdik.intellij.latte.settings.LatteFilterSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LatteMacroModifierElementImpl extends StubBasedPsiElementBase<LatteFilterStub> implements LatteMacroModifierElement {

	public LatteMacroModifierElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LatteMacroModifierElementImpl(final LatteFilterStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Nullable
	public LatteMacroContent getMacroContent() {
		return findChildByClass(LatteMacroContent.class);
	}

	@Nullable
	public LatteFilterSettings getMacroModifier() {
		return LatteConfiguration.getInstance(getProject()).getFilter(getModifierName());
	}

	@Override
	public @Nullable Icon getIcon(int flags) {
		return LatteIcons.MODIFIER;
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