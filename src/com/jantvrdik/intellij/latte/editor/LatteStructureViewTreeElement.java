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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		PsiElement element = getElement();
		if (element instanceof LatteMacroClassic) {
			LatteMacroClassic macroClassic = (LatteMacroClassic) element;
			String presentableText = macroClassic.getOpenTag().getMacroName();
			if (macroClassic.getOpenTag().getMacroContent() != null) {
				String macroText = macroClassic.getOpenTag().getMacroContent().getText().trim();
				Pattern pattern  = Pattern.compile("([\\S]+).*");
				Matcher matcher = pattern.matcher(macroText);
				if (matcher.matches()) {
					presentableText += " " + matcher.group(1);
				}

			}
			return presentableText;
		} else if (element instanceof LatteFile) {
			return ((LatteFile) element).getName();
		}
		return "";
	}
}
