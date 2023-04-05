package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import com.jantvrdik.intellij.latte.psi.elements.LattePairMacroElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class LattePairMacroImpl extends LattePsiElementImpl implements LattePairMacroElement {

	public LattePairMacroImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public @NotNull List<LatteMacroTag> getMacroTagList() {
		return PsiTreeUtil.getChildrenOfTypeAsList(this, LatteMacroTag.class);
	}

	@Nullable
	public LatteMacroTag getMacroOpenTag() {
		return getMacroTagList().stream().findFirst().orElse(null);
	}

	@Override
	public @Nullable LatteMacroTag getCloseTag() {
		return getMacroTagList().get(1);
	}

	public @NotNull LatteMacroTag getOpenTag() {
		return getMacroTagList().get(0);
	}
}