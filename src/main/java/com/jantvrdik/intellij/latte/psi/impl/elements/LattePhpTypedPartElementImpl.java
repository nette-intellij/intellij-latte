package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpTypedPartElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiElementImpl;
import org.jetbrains.annotations.NotNull;

public abstract class LattePhpTypedPartElementImpl extends LattePsiElementImpl implements LattePhpTypedPartElement {

	public LattePhpTypedPartElementImpl(@NotNull ASTNode node) {
		super(node);
	}
}