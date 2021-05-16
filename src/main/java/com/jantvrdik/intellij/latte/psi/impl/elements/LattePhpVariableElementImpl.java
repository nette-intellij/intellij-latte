package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.stubs.IStubElementType;
import com.jantvrdik.intellij.latte.indexes.LatteStubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpVariableStub;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpVariableElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LattePhpVariableElementImpl extends LatteStubBasedPsiElement<LattePhpVariableStub> implements LattePhpVariableElement {

	public LattePhpVariableElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpVariableElementImpl(final LattePhpVariableStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public String getPhpElementName()
	{
		return getVariableName();
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