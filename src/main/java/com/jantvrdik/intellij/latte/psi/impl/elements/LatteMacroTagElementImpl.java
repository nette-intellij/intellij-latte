package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import com.jantvrdik.intellij.latte.psi.LatteMacroContent;
import com.jantvrdik.intellij.latte.psi.elements.LatteMacroTagElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiElementImpl;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public abstract class LatteMacroTagElementImpl extends LattePsiElementImpl implements LatteMacroTagElement {

	private @Nullable String tagName = null;
	private @Nullable PsiElement identifier = null;
	private int macroNameLength = -1;

	public LatteMacroTagElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		tagName = null;
		identifier = null;
		macroNameLength = -1;
	}

	@Nullable
	public LatteMacroContent getMacroContent() {
		return findChildByClass(LatteMacroContent.class);
	}

	@Override
	public @Nullable Icon getIcon(int flags) {
		return LatteIcons.MACRO;
	}

	@Override
	public @NotNull String getMacroName() {
		if (tagName == null) {
			tagName = LattePsiImplUtil.getMacroName(this);
		}
		return tagName;
	}

	@Override
	public String getName() {
		return getMacroName();
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, T_MACRO_NAME);
		}
		return identifier;
	}

	public boolean matchMacroName(@NotNull String name) {
		return LattePsiImplUtil.matchMacroName(this, name);
	}

	@Override
	public int getMacroNameLength() {
		if (macroNameLength == -1) {
			macroNameLength = LattePsiImplUtil.getMacroNameLength(this);
		}
		return macroNameLength;
	}
}