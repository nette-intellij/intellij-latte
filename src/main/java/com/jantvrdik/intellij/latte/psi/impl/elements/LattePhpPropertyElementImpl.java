package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.stubs.IStubElementType;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpPropertyStub;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpPropertyElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LattePhpPropertyElementImpl extends StubBasedPsiElementBase<LattePhpPropertyStub> implements LattePhpPropertyElement {

	public LattePhpPropertyElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpPropertyElementImpl(final LattePhpPropertyStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public String getPhpElementName()
	{
		return getPropertyName();
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