package com.jantvrdik.intellij.latte.psi.impl;

import com.intellij.lang.ASTNode;
import com.jantvrdik.intellij.latte.psi.LatteMacroCloseTag;
import com.jantvrdik.intellij.latte.psi.LatteMacroContent;
import com.jantvrdik.intellij.latte.psi.LatteMacroTag;
import org.jetbrains.annotations.NotNull;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LattePsiImplUtil {
	@NotNull
	public static String getMacroName(LatteMacroTag element) {
		ASTNode elementNode = element.getNode();
		ASTNode nameNode = elementNode.findChildByType(T_MACRO_NAME);
		if (nameNode != null) {
			return nameNode.getText();
		}

		nameNode = elementNode.findChildByType(T_MACRO_SHORTNAME);
		if (nameNode != null) {
			return nameNode.getText();
		}
		LatteMacroContent content = element.getMacroContent();
		if (content == null) {
			return "";
		}
		if (element instanceof LatteMacroCloseTag) {
			return "";
		}
		return "=";
	}

}
