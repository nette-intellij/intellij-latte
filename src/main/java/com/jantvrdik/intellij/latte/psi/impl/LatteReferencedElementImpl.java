package com.jantvrdik.intellij.latte.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LatteReferencedElementImpl extends ASTWrapperPsiElement {

	private Project project = null;
	private LatteFile file = null;

	public LatteReferencedElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public @Nullable LatteFile getLatteFile() {
		if (file == null) {
			PsiFile containingFile = getContainingFile();
			file = containingFile instanceof LatteFile ? (LatteFile) containingFile : null;
		}
		return file;
	}

	@Override
	public @NotNull Project getProject() {
		if (project == null) {
			project = super.getProject();
		}
		return project;
	}

	public @Nullable PsiReference getReference() {
		PsiReference[] references = getReferences();
		return references.length == 0 ? null : references[0];
	}

	public @NotNull PsiReference[] getReferences() {
		return ReferenceProvidersRegistry.getReferencesFromProviders(this);
	}
}