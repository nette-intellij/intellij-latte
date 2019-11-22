package com.jantvrdik.intellij.latte.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

		ASTNode argsNode = content.getNode().findChildByType(
			TokenSet.create(T_MACRO_ARGS, T_MACRO_ARGS_VAR, T_MACRO_ARGS_STRING, T_MACRO_ARGS_NUMBER)
		);
		return (argsNode != null ? "=" : "");
	}

	@Nullable
	public static LattePhpContent getFirstPhpContent(@NotNull LatteMacroContent macroContent) {
		List<LattePhpContent> phpContents = macroContent.getPhpContentList();
		return phpContents.stream().findFirst().isPresent() ? phpContents.stream().findFirst().get() : null;
	}

	public static String getVariableName(@NotNull PsiElement element) {
		ASTNode keyNode = element.getNode().findChildByType(T_MACRO_ARGS_VAR);
		if (keyNode != null) {
			return keyNode.getPsi().getText();
		} else {
			return null;
		}
	}

	public static String getConstantName(@NotNull PsiElement element) {
		return getMethodName(element);
	}

	public static String getMethodName(@NotNull PsiElement element) {
		ASTNode keyNode = element.getNode().findChildByType(T_PHP_METHOD);
		if (keyNode != null) {
			return keyNode.getPsi().getText();
		} else {
			return null;
		}
	}

	public static String getPropertyName(@NotNull PsiElement element) {
		return getMethodName(element);
	}

	public static boolean isDefinition(@NotNull LattePhpVariable element) {
		PsiElement parent = element.getParent();
		if (parent == null) {
			return false;
		}

		if (parent.getNode().getElementType() == PHP_ARRAY_OF_VARIABLES) {
			PsiElement parentPrevElement = PsiTreeUtil.skipWhitespacesBackward(parent);
			IElementType type = parentPrevElement != null ? parentPrevElement.getNode().getElementType() : null;
			return type == T_PHP_AS || type == T_PHP_DOUBLE_ARROW;
		}

		if (parent.getNode().getElementType() == PHP_FOREACH) {
			PsiElement prevElement = PsiTreeUtil.skipWhitespacesBackward(element);
			IElementType type = prevElement != null ? prevElement.getNode().getElementType() : null;
			return type == T_PHP_AS || type == T_PHP_DOUBLE_ARROW;
		}

		LatteNetteAttrValue parentAttr = PsiTreeUtil.getParentOfType(element, LatteNetteAttrValue.class);
		if (parentAttr != null) {
			PsiElement nextElement = PsiTreeUtil.skipWhitespacesForward(element);
			if (nextElement == null || !nextElement.getText().equals("=")) {
				return false;
			}
			PsiElement prevElement = PsiTreeUtil.skipWhitespacesBackward(parentAttr);
			if (prevElement == null || !prevElement.getText().equals("=")) {
				return false;
			}

			prevElement = PsiTreeUtil.skipWhitespacesBackward(prevElement);
			return prevElement != null && prevElement.getText().equals("n:for");
		}

		PsiElement prev = PsiTreeUtil.prevLeaf(parent, true);
		if (prev == null) {
			return false;
		}

		if (prev.getText().equals("for") || prev.getText().equals("var")) {
			PsiElement nextElement = PsiTreeUtil.skipWhitespacesForward(element);
			if (nextElement != null && nextElement.getText().equals("=")) {
				return true;
			}
		}

		return  false;
	}

	public static String getName(LattePhpVariable element) {
		String text = element.getFirstChild().getText();
		return text.startsWith("$") ? text.substring(1) : text;
	}

	public static PsiElement setName(LattePhpVariable element, String newName) {
		ASTNode keyNode = element.getNode();
		if (keyNode != null) {
			LattePhpVariable variable = LatteElementFactory.createVariable(element.getProject(), newName);
			if (variable != null) {
				ASTNode newKeyNode = variable.getFirstChild().getNode();
				element.getNode().replaceChild(keyNode, newKeyNode);
			}
		}
		return element;
	}

	public static PsiElement getNameIdentifier(LattePhpVariable element) {
		ASTNode keyNode = element.getNode().findChildByType(T_MACRO_ARGS_VAR);
		if (keyNode != null) {
			return keyNode.getPsi();
		} else {
			return null;
		}
	}

	public static String getName(LattePhpStaticVariable element) {
		ASTNode keyNode = element.getNode().findChildByType(T_MACRO_ARGS_VAR);
		if (keyNode != null) {
			return LattePhpUtil.normalizePhpVariable(keyNode.getText());
		} else {
			return null;
		}
	}

	public static PsiElement getNameIdentifier(LattePhpStaticVariable element) {
		ASTNode keyNode = element.getNode().findChildByType(T_MACRO_ARGS_VAR);
		if (keyNode != null) {
			return keyNode.getPsi();
		} else {
			return null;
		}
	}

	public static PsiElement getNameIdentifier(PsiElement element) {
		ASTNode keyNode = element.getNode().findChildByType(T_PHP_METHOD);
		if (keyNode != null) {
			return keyNode.getPsi();
		} else {
			return null;
		}
	}

	public static String getName(PsiElement element) {
		ASTNode keyNode = element.getNode().findChildByType(T_PHP_METHOD);
		if (keyNode != null) {
			return LattePhpUtil.normalizePhpVariable(keyNode.getText());
		} else {
			return null;
		}
	}

	public static PsiElement setName(PsiElement element, String newName) {
		return element;
	}
}
