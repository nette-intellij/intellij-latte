package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.stubs.IStubElementType;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpClassStub;
import com.jantvrdik.intellij.latte.php.LattePhpTypeDetector;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpClassReferenceElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LattePhpClassReferenceElementImpl extends StubBasedPsiElementBase<LattePhpClassStub> implements LattePhpClassReferenceElement {

	public LattePhpClassReferenceElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpClassReferenceElementImpl(final LattePhpClassStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public String getPhpElementName()
	{
		return getClassName();
	}

	@Override
	public NettePhpType getReturnType() {
		return LattePhpTypeDetector.detectPhpType(this);
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