package com.jantvrdik.intellij.latte.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.php.LattePhpTypeDetector;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpExpressionElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpStatementPartElement;
import com.jantvrdik.intellij.latte.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LattePsiImplUtil {
	public static @NotNull String getMacroName(PsiElement element) {
		ASTNode nameNode = getMacroNameNode(element);
		if (nameNode != null) {
			return nameNode.getText();
		}
		return createMacroName(element);
	}

	public static boolean matchMacroName(PsiElement element, @NotNull String name) {
		ASTNode nameNode = getMacroNameNode(element);
		if (nameNode == null) {
			return createMacroName(element).equals(name);
		}
		return matchPsiElement(nameNode, name);
	}

	public static int getMacroNameLength(PsiElement element) {
		ASTNode nameNode = getMacroNameNode(element);
		if (nameNode != null) {
			return nameNode.getTextLength();
		}
		return createMacroName(element).length();
	}

	private static boolean matchPsiElement(ASTNode element, @NotNull String text) {
		return element.getTextLength() == text.length() && element.getText().equals(text);
	}

	private static @Nullable ASTNode getMacroNameNode(PsiElement element) {
		ASTNode elementNode = element.getNode();
		ASTNode nameNode = elementNode.findChildByType(T_MACRO_NAME);
		if (nameNode != null) {
			return nameNode;
		}
		return elementNode.findChildByType(T_MACRO_SHORTNAME);
	}

	private static @NotNull String createMacroName(PsiElement element) {
		LatteMacroContent content = element instanceof LatteMacroTag ? ((LatteMacroTag) element).getMacroContent() : null;
		if (content == null || element instanceof LatteMacroCloseTag) {
			return "";
		}
		return "=";
	}

	public static @Nullable LattePhpContent getFirstPhpContent(@NotNull LatteMacroContent macroContent) {
		List<LattePhpContent> phpContents = macroContent.getPhpContentList();
		return phpContents.stream().findFirst().isPresent() ? phpContents.stream().findFirst().get() : null;
	}

	public static @Nullable PsiElement getMacroNameElement(@NotNull LatteMacroContent macroContent) {
		return PsiTreeUtil.skipWhitespacesBackward(macroContent);
	}

	public static @Nullable LattePhpStatementPartElement getPrevPhpStatementPart(@NotNull LattePhpStatementPartElement element) {
		LattePhpStatement statement = element.getPhpStatement();
		if (element == statement.getPhpStatementFirstPart()) {
			return null;
		}
		LattePhpStatementPartElement previous = null;
		for (LattePhpStatementPart part : statement.getPhpStatementPartList()) {
			if (part == element) {
				return previous == null ? statement.getPhpStatementFirstPart() : previous;
			}
			previous = part;
		}
		return null;
	}

	public static @NotNull LattePhpStatement getPhpStatement(@NotNull LattePhpStatementPartElement element) {
		return (LattePhpStatement) element.getParent();
	}

	public static int getPhpArrayLevel(@NotNull LattePhpExpressionElement expression) {
		List<LattePhpStatement> statements = expression.getPhpStatementList();
		if (statements.size() > 0) {
			BaseLattePhpElement phpElement = statements.get(statements.size() - 1).getLastPhpElement();
			return phpElement != null ? phpElement.getPhpArrayLevel() : 0;
		}
		return 0;
	}

	public static @NotNull NettePhpType getReturnType(@NotNull PsiElement element) {
		return LattePhpTypeDetector.detectPhpType(element);
	}

	public static @Nullable BaseLattePhpElement getLastPhpElement(@NotNull LattePhpStatement statement) {
		int partsCount = statement.getPhpStatementPartList().size();
		if (partsCount > 0) {
			return statement.getPhpStatementPartList().get(partsCount - 1).getPhpElement();
		}
		return statement.getPhpStatementFirstPart().getPhpElement();
	}

	public static @Nullable BaseLattePhpElement getPhpElement(@NotNull LattePhpStatementFirstPart statement) {
		if (statement.getPhpClassReference() != null) {
			return statement.getPhpClassReference();
		} else if (statement.getPhpMethod() != null) {
			return statement.getPhpMethod();
		} else if (statement.getPhpVariable() != null) {
			return statement.getPhpVariable();
		}
		return null;
	}

	public static @Nullable BaseLattePhpElement getPhpElement(@NotNull LattePhpStatementPart statement) {
		if (statement.getPhpConstant() != null) {
			return statement.getPhpConstant();
		} else if (statement.getPhpMethod() != null) {
			return statement.getPhpMethod();
		} else if (statement.getPhpProperty() != null) {
			return statement.getPhpProperty();
		} else if (statement.getPhpStaticVariable() != null) {
			return statement.getPhpStaticVariable();
		}
		return null;
	}

	public static boolean isPhpVariableOnly(@NotNull LattePhpStatement statement) {
		return statement.getPhpStatementFirstPart().getPhpVariable() != null && statement.getPhpStatementPartList().size() == 0;
	}

	public static boolean isPhpClassReferenceOnly(@NotNull LattePhpStatement statement) {
		return statement.getPhpStatementFirstPart().getPhpClassReference() != null && statement.getPhpStatementPartList().size() == 0;
	}

	@Nullable
	public static LatteMacroTag getMacroOpenTag(@NotNull LattePairMacro element) {
		return element.getMacroTagList().stream().findFirst().orElse(null);
	}

	@Nullable
	public static LatteHtmlOpenTag getHtmlOpenTag(@NotNull LatteHtmlTagContainer element) {
		PsiElement prev = element.getPrevSibling();
		if (prev instanceof LatteHtmlOpenTag) {
			return (LatteHtmlOpenTag) prev;
		}
		return element.getHtmlOpenTag();
	}

	@NotNull
	public static String getHtmlTagName(@NotNull LatteHtmlOpenTag element) {
		PsiElement child = findFirstChildWithType(element, T_HTML_OPEN_TAG_OPEN);
		return child != null ? child.getText() : "?";
	}

	public static PsiElement findFirstChildWithType(PsiElement element, @NotNull IElementType type) {
		ASTNode keyNode = element.getNode().findChildByType(type);
		if (keyNode != null) {
			return keyNode.getPsi();
		} else {
			return null;
		}
	}
}
