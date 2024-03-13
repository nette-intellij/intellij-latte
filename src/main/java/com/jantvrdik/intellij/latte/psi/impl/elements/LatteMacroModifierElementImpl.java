package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import com.jantvrdik.intellij.latte.psi.LatteMacroContent;
import com.jantvrdik.intellij.latte.psi.LattePhpInBrackets;
import com.jantvrdik.intellij.latte.psi.elements.LatteMacroModifierElement;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiElementImpl;
import com.jantvrdik.intellij.latte.psi.impl.LattePsiImplUtil;
import com.jantvrdik.intellij.latte.settings.LatteFilterSettings;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.T_MACRO_FILTERS;

public abstract class LatteMacroModifierElementImpl extends LattePsiElementImpl implements LatteMacroModifierElement {

	private @Nullable String modifierName = null;
	private @Nullable PsiElement identifier = null;

	public LatteMacroModifierElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		modifierName = null;
		identifier = null;
	}

	@Nullable
	public LatteMacroContent getMacroContent() {
		return findChildByClass(LatteMacroContent.class);
	}

	@Override
	public String getModifierName() {
		if (modifierName == null) {
			PsiElement found = getTextElement();
			modifierName = found != null ? LatteUtil.normalizeMacroModifier(found.getText()) : null;
		}
		return modifierName;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, T_MACRO_FILTERS);
		}
		return identifier;
	}

	@Override
	public boolean isVariableModifier() {
		LattePhpInBrackets variableModifier = PsiTreeUtil.getParentOfType(this, LattePhpInBrackets.class);
		return variableModifier != null;
	}

	@Override
	public @Nullable PsiElement getTextElement() {
		return getNameIdentifier();
	}

	@Nullable
	public LatteFilterSettings getMacroModifier() {
		return LatteConfiguration.getInstance(getProject()).getFilter(getModifierName());
	}

	@Override
	public @Nullable Icon getIcon(int flags) {
		return LatteIcons.MODIFIER;
	}
}