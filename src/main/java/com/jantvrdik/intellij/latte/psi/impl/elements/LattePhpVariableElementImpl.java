package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.stubs.IStubElementType;
import com.jantvrdik.intellij.latte.indexes.LatteStubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpVariableStub;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpVariableElement;
import com.jantvrdik.intellij.latte.utils.LattePhpCachedVariable;
import com.jantvrdik.intellij.latte.utils.LattePhpVariableDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

	@Override
	public @Nullable LattePhpCachedVariable getCachedVariable() {
		LatteFile file = getLatteFile();
		return file != null ? getLatteFile().getCachedVariable(this) : null;
	}

	@Override
	public boolean isDefinition() {
		LattePhpCachedVariable variable = getCachedVariable();
		return variable != null && variable.isDefinition();
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