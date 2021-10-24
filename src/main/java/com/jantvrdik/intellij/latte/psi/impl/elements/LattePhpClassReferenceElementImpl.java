package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpClassStub;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpClassReferenceElement;
import com.jantvrdik.intellij.latte.psi.impl.LatteBaseStubPhpElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LattePhpClassReferenceElementImpl extends LatteBaseStubPhpElementImpl<LattePhpClassStub> implements LattePhpClassReferenceElement {

	private @Nullable String name = null;
	private @Nullable String className = null;

	public LattePhpClassReferenceElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpClassReferenceElementImpl(final LattePhpClassStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
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
			final LattePhpClassStub stub = getStub();
			if (stub != null) {
				className = stub.getClassName();
				return className;
			}
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