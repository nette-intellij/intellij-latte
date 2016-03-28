package com.jantvrdik.intellij.latte.editor;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.LatteAutoClosedBlock;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LatteMacroClassic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by matej21 on 28.3.16.
 */
public class LatteStructureViewTreeElement extends PsiTreeElementBase<PsiElement> {

	public LatteStructureViewTreeElement(PsiElement psiElement) {
		super(psiElement);
	}

	@NotNull
	@Override
	public Collection<StructureViewTreeElement> getChildrenBase() {
		Collection<StructureViewTreeElement> elements = new ArrayList<StructureViewTreeElement>();
		for (PsiElement el : getElement().getChildren()) {
			if (el instanceof LatteMacroClassic || el instanceof LatteAutoClosedBlock) {
				elements.add(new LatteStructureViewTreeElement(el));
			}
		}
		return elements;
	}

	@Nullable
	@Override
	public String getPresentableText() {
		if (getElement() instanceof LatteMacroClassic) {
			return ((LatteMacroClassic) getElement()).getOpenTag().getMacroName();
		} else if (getElement() instanceof LatteAutoClosedBlock) {
			return ((LatteAutoClosedBlock) getElement()).getMacroOpenTag().getMacroName();
		} else if (getElement() instanceof LatteFile) {
			return ((LatteFile) getElement()).getName();
		}
		return "";
	}
}
