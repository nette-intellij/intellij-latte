package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpStaticVariableStub;
import com.jantvrdik.intellij.latte.php.LattePhpVariableUtil;
import com.jantvrdik.intellij.latte.psi.LatteElementFactory;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpStaticVariableElement;
import com.jantvrdik.intellij.latte.psi.impl.LatteStubPhpElementImpl;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.T_MACRO_ARGS_VAR;

public abstract class LattePhpStaticVariableElementImpl extends LatteStubPhpElementImpl<LattePhpStaticVariableStub> implements LattePhpStaticVariableElement {

	private @Nullable String name = null;
	private @Nullable String variableName = null;
	private @Nullable PsiElement identifier = null;

	public LattePhpStaticVariableElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpStaticVariableElementImpl(final LattePhpStaticVariableStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		this.name = null;
		this.variableName = null;
		this.identifier = null;
	}

	@Override
	public String getPhpElementName()
	{
		return getVariableName();
	}

	@Override
	public String getVariableName() {
		if (variableName == null) {
			final LattePhpStaticVariableStub stub = getStub();
			if (stub != null) {
				variableName = stub.getVariableName();
				return variableName;
			}

			PsiElement found = getTextElement();
			variableName = found != null ? LattePhpVariableUtil.normalizePhpVariable(found.getText()) : null;
		}
		return variableName;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, T_MACRO_ARGS_VAR);
		}
		return identifier;
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		ASTNode keyNode = getFirstChild().getNode();
		PsiElement property = LatteElementFactory.createStaticVariable(getProject(), name);
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