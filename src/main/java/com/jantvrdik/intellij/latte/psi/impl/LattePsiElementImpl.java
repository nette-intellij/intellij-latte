package com.jantvrdik.intellij.latte.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LattePsiElementImpl extends ASTWrapperPsiElement {

	private Project project = null;
	private LatteFile file = null;

	public LattePsiElementImpl(@NotNull ASTNode node) {
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
}