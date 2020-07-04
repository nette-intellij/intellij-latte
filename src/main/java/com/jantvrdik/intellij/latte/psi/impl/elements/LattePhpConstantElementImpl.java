package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.stubs.IStubElementType;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpConstantStub;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpConstantElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LattePhpConstantElementImpl extends StubBasedPsiElementBase<LattePhpConstantStub> implements LattePhpConstantElement {

	public LattePhpConstantElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpConstantElementImpl(final LattePhpConstantStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public String getPhpElementName()
	{
		return getConstantName();
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