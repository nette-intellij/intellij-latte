package com.jantvrdik.intellij.latte.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.templateLanguages.OuterLanguageElementImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.ILeafElementType;
import com.jantvrdik.intellij.latte.LatteLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LatteOuterElementType extends IElementType implements ILeafElementType {
	public LatteOuterElementType(@NotNull @NonNls String debugName) {
		super(debugName, LatteLanguage.INSTANCE);
	}

	public @NotNull
	ASTNode createLeafNode(@NotNull CharSequence charSequence) {
		return new OuterLanguageElementImpl(this, charSequence);
	}
}
