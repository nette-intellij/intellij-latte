package com.jantvrdik.intellij.latte.editor;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import com.jantvrdik.intellij.latte.psi.LatteAutoClosedBlock;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LatteMacroClassic;
import com.jantvrdik.intellij.latte.psi.LatteNetteAttr;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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
		if (getElement() == null) {
			return elements;
		}
		for (PsiElement el : getElement().getChildren()) {
			if (el instanceof LatteMacroClassic || el instanceof LatteAutoClosedBlock || el instanceof LatteNetteAttr) {
				elements.add(new LatteStructureViewTreeElement(el));
			}
		}
		return elements;
	}

	@Override
	public Icon getIcon(boolean open) {
		PsiElement element = getElement();
		if (element instanceof LatteMacroClassic || element instanceof LatteAutoClosedBlock) {
			return LatteIcons.MACRO;
		} else if (element instanceof LatteNetteAttr) {
			return LatteIcons.N_TAG;
		} else if (element instanceof LatteFile) {
			return LatteIcons.FILE;
		}
		return super.getIcon(open);
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
		} else if (element instanceof LatteNetteAttr) {
			return ((LatteNetteAttr) element).getAttrName().getText();
		} else if (element instanceof LatteFile) {
			return ((LatteFile) element).getName();
		}
		return "";
	}
}
