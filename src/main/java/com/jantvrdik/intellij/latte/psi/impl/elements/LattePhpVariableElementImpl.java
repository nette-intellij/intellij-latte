package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jantvrdik.intellij.latte.php.LattePhpVariableUtil;
import com.jantvrdik.intellij.latte.psi.LatteElementFactory;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpVariableElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePhpElementImpl;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiImplUtil;
import com.jantvrdik.intellij.latte.utils.LattePhpCachedVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.T_MACRO_ARGS_VAR;

public abstract class LattePhpVariableElementImpl extends LattePhpElementImpl implements LattePhpVariableElement {

	private @Nullable String name = null;
	private @Nullable PsiElement identifier = null;

	public LattePhpVariableElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public String getPhpElementName()
	{
		return getVariableName();
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		this.name = null;
		this.identifier = null;
	}

	@Override
	public String getVariableName() {
		if (name == null) {
			PsiElement found = getTextElement();
			name = found != null ? LattePhpVariableUtil.normalizePhpVariable(found.getText()) : null;
		}
		return name;
	}

	@Override
	public @NotNull List<LattePhpCachedVariable> getVariableDefinition() {
		LatteFile file = getLatteFile();
		if (file == null) {
			return Collections.emptyList();
		}
		return file.getCachedVariableDefinitions(this);
	}

	@Override
	public String getName() {
		return getVariableName();
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		ASTNode keyNode = getFirstChild().getNode();
		PsiElement variable = LatteElementFactory.createVariable(getProject(), name);
		if (variable == null) {
			return this;
		}
		return LatteElementFactory.replaceChildNode(this, variable, keyNode);
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, T_MACRO_ARGS_VAR);
		}
		return identifier;
	}

	@Override
	public @Nullable PsiElement getVariableContext() {
		return LattePhpVariableUtil.getCurrentContext(this);
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
}