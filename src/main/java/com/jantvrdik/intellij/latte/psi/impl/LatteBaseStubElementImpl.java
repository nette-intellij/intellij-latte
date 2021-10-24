package com.jantvrdik.intellij.latte.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiReference;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.jantvrdik.intellij.latte.indexes.LatteStubBasedPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LatteBaseStubElementImpl<T extends StubElement> extends LatteStubBasedPsiElement<T> implements StubBasedPsiElement<T> {

	private Project project = null;

	public LatteBaseStubElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LatteBaseStubElementImpl(final T stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public @NotNull Project getProject() {
		if (project == null) {
			project = super.getProject();
		}
		return project;
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