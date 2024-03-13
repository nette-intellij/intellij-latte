package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpClassReferenceElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePhpElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LattePhpClassReferenceElementImpl extends LattePhpElementImpl implements LattePhpClassReferenceElement {

	private @Nullable String name = null;
	private @Nullable String className = null;

	public LattePhpClassReferenceElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		name = null;
		className = null;
		getPhpClassUsage().reset();
	}

	@Override
	public String getPhpElementName()
	{
		return getClassName();
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		return getPhpClassUsage().getNameIdentifier();
	}

	@Override
	public String getClassName() {
		if (className == null) {
			className = getPhpClassUsage().getClassName();
		}
		return className;
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		return this;
	}

	@Override
	public String getName() {
		if (name == null) {
			PsiElement found = getNameIdentifier();
			name = found != null ? found.getText() : null;
		}
		return name;
	}
}