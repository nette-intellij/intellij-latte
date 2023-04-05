package com.jantvrdik.intellij.latte.psi.elements;

import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LattePairMacroElement extends LattePsiElement {

	@NotNull
	List<LatteMacroTag> getMacroTagList();

	@Nullable
	LatteMacroTag getMacroOpenTag();

	@NotNull
	LatteMacroTag getOpenTag();

	@Nullable
	LatteMacroTag getCloseTag();
}