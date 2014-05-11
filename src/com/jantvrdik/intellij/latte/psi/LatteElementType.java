package com.jantvrdik.intellij.latte.psi;

import com.intellij.psi.tree.IElementType;
import com.jantvrdik.intellij.latte.LatteLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class LatteElementType extends IElementType {
	public LatteElementType(@NotNull @NonNls String debugName) {
		super(debugName, LatteLanguage.INSTANCE);
	}
}
