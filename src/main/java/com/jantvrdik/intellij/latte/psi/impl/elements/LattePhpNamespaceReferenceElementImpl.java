package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.stubs.IStubElementType;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpNamespaceStub;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpNamespaceReferenceElement;
import com.jetbrains.php.PhpIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LattePhpNamespaceReferenceElementImpl extends StubBasedPsiElementBase<LattePhpNamespaceStub> implements LattePhpNamespaceReferenceElement {

	public LattePhpNamespaceReferenceElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpNamespaceReferenceElementImpl(final LattePhpNamespaceStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public String getPhpElementName()
	{
		return getNamespaceName();
	}

	@Override
	public NettePhpType getReturnType() {
		return NettePhpType.create(getNamespaceName());
	}

	@Override
	public @Nullable Icon getIcon(int flags) {
		return PhpIcons.NAMESPACE;
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