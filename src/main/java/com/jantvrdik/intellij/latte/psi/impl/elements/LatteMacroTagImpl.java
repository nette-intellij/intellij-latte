package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import com.jantvrdik.intellij.latte.psi.elements.LattePairMacroElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LatteMacroTagImpl extends LattePsiElementImpl implements LattePairMacroElement {

	public LatteMacroTagImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Nullable
	public LatteMacroTag getMacroOpenTag() {
		return getMacroTagList().stream().findFirst().orElse(null);
	}
}