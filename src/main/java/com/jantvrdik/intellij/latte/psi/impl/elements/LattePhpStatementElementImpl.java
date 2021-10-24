package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.jantvrdik.intellij.latte.php.LattePhpTypeDetector;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpStatementElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LattePhpStatementElementImpl extends LattePsiElementImpl implements LattePhpStatementElement {

	private @Nullable BaseLattePhpElement lastPhpElement;

	public LattePhpStatementElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		lastPhpElement = null;
	}

	@Override
	public NettePhpType getReturnType() {
		return LattePhpTypeDetector.detectPhpType(this);
	}

	@Override
	public @Nullable BaseLattePhpElement getLastPhpElement() {
		if (lastPhpElement == null) {
			int partsCount = getPhpStatementPartList().size();
			if (partsCount > 0) {
				lastPhpElement = getPhpStatementPartList().get(partsCount - 1).getPhpElement();
				return lastPhpElement;
			}
			lastPhpElement = getPhpStatementFirstPart().getPhpElement();
		}
		return lastPhpElement;
	}

	@Override
	public boolean isPhpVariableOnly() {
		return getPhpStatementFirstPart().getPhpVariable() != null && getPhpStatementPartList().size() == 0;
	}

	@Override
	public boolean isPhpClassReferenceOnly() {
		return getPhpStatementFirstPart().getPhpClassReference() != null && getPhpStatementPartList().size() == 0;
	}
}