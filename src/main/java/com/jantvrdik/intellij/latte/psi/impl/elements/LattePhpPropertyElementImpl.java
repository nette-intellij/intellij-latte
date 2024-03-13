package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jantvrdik.intellij.latte.psi.LatteElementFactory;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpPropertyElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePhpElementImpl;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.T_PHP_IDENTIFIER;

public abstract class LattePhpPropertyElementImpl extends LattePhpElementImpl implements LattePhpPropertyElement {

	private @Nullable String name = null;
	private @Nullable String propertyName = null;
	private @Nullable PsiElement identifier = null;

	public LattePhpPropertyElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		this.name = null;
		this.propertyName = null;
		this.identifier = null;
	}

	@Override
	public String getPhpElementName()
	{
		return getPropertyName();
	}

	@Override
	public String getPropertyName() {
		if (propertyName == null) {
			PsiElement found = getTextElement();
			propertyName = found != null ? found.getText() : null;
		}
		return propertyName;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, T_PHP_IDENTIFIER);
		}
		return identifier;
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		ASTNode keyNode = getFirstChild().getNode();
		PsiElement property = LatteElementFactory.createProperty(getProject(), name);
		if (property == null) {
			return this;
		}
		return LatteElementFactory.replaceChildNode(this, property, keyNode);
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