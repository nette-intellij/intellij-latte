package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jantvrdik.intellij.latte.psi.LatteElementFactory;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpConstantElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePhpElementImpl;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.T_PHP_IDENTIFIER;

public abstract class LattePhpConstantElementImpl extends LattePhpElementImpl implements LattePhpConstantElement {

	private @Nullable String name = null;
	private @Nullable String constantName = null;
	private @Nullable PsiElement identifier = null;

	public LattePhpConstantElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		this.name = null;
		this.constantName = null;
		this.identifier = null;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, T_PHP_IDENTIFIER);
		}
		return identifier;
	}

	@Override
	public String getConstantName() {
		if (constantName == null) {
			PsiElement found = getTextElement();
			constantName = found != null ? found.getText() : null;
		}
		return constantName;
	}

	@Override
	public String getPhpElementName()
	{
		return getConstantName();
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		ASTNode keyNode = getFirstChild().getNode();
		PsiElement property = LatteElementFactory.createConstant(getProject(), name);
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