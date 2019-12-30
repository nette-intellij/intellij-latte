package com.jantvrdik.intellij.latte.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteDefaultVariable;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import com.jantvrdik.intellij.latte.utils.LattePhpUtil;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jantvrdik.intellij.latte.utils.PsiPositionedElement;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
			return LattePhpUtil.normalizePhpVariable(keyNode.getPsi().getText());
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

	public static String getClassName(@NotNull PsiElement element) {
		ASTNode keyNode = element.getNode().findChildByType(T_PHP_VAR_TYPE);
		if (keyNode != null) {
			return keyNode.getPsi().getText();
		} else {
			return null;
		}
	}

	private static LattePhpType detectVariableType(@NotNull PsiElement element, @NotNull String variableName)
	{
		LatteDefaultVariable defaultVariable = LatteConfiguration.INSTANCE.getVariable(element.getProject(), variableName);
		if (defaultVariable != null) {
			return defaultVariable.type;
		}

		List<PsiPositionedElement> all = LatteUtil.findVariablesInFileBeforeElement(element, element.getContainingFile().getOriginalFile().getVirtualFile(), variableName);
		List<PsiPositionedElement> definitions = all.stream().filter(
				psiPositionedElement -> psiPositionedElement.getElement() instanceof LattePhpVariable
						&& ((LattePhpVariable) psiPositionedElement.getElement()).isDefinition()
		).collect(Collectors.toList());

		for (PsiPositionedElement positionedElement : definitions) {
			if (!(positionedElement.getElement() instanceof LattePhpVariable)) {
				continue;
			}

			if (isVarTypeDefinition((LattePhpVariable) positionedElement.getElement())) {
				String prevPhpType = findPrevPhpType(positionedElement.getElement());
				boolean nullable = false;
				List<String> types = new ArrayList<String>();
				for (String part : prevPhpType.split(Pattern.quote("|"))) {
					if (part.equals("null")) {
						nullable = true;
						continue;
					}
					types.add(part);
				}
				return new LattePhpType(types.toArray(new String[types.size()]), nullable);
			}
		}
		return new LattePhpType("mixed", false);
	}

	private static String findPrevPhpType(PsiElement element)
	{
		return findPrevPhpType(element, "");
	}

	private static String findPrevPhpType(PsiElement element, String phpType)
	{
		PsiElement prevElement = PsiTreeUtil.prevLeaf(element, true);
		if (prevElement == null || prevElement.getNode().getElementType() == T_MACRO_NAME) {
			return phpType;
		}

		String text = prevElement.getText();
		if (text.trim().length() == 0) {
			return findPrevPhpType(prevElement, phpType);
		}

		return findPrevPhpType(prevElement, text + phpType);
	}

	public static @NotNull LattePhpType getPhpType(@NotNull PsiElement element) {
		PsiElement prev = PsiTreeUtil.skipWhitespacesBackward(element);
		if (prev == null || (prev.getNode().getElementType() != T_PHP_DOUBLE_COLON && prev.getNode().getElementType() != T_PHP_OBJECT_OPERATOR)) {
			if (element instanceof LattePhpVariable) {
				return detectVariableType(element, ((LattePhpVariable) element).getVariableName());
			}
			return new LattePhpType("mixed", false);
		}

		PsiElement prevElement;
		if (prev.getParent().getNode().getElementType() == PHP_FOREACH) {
			prevElement = PsiTreeUtil.skipWhitespacesBackward(prev.getParent());
		} else {
			prevElement = PsiTreeUtil.skipWhitespacesBackward(prev);
		}

		if (prevElement != null && prevElement.getText().equals(")")) {
			PsiElement beforeBraces = PsiTreeUtil.skipWhitespacesBackward(prevElement);
			if (beforeBraces instanceof LattePhpMethodArgs) {
				beforeBraces = PsiTreeUtil.skipWhitespacesBackward(beforeBraces);
			}

			if (beforeBraces != null && beforeBraces.getText().equals("(")) {
				prevElement = PsiTreeUtil.skipWhitespacesBackward(beforeBraces);
			}
		}

		LattePhpType type = null;
		if (prevElement instanceof LattePhpStaticVariable) {
			type = ((LattePhpStaticVariable) prevElement).getPropertyType();
		} else if (prevElement instanceof LattePhpClass) {
			type = ((LattePhpClass) prevElement).getPhpType();
		} else if (prevElement instanceof LattePhpMethod) {
			type = ((LattePhpMethod) prevElement).getReturnType();
		} else if (prevElement instanceof LattePhpProperty) {
			type = ((LattePhpProperty) prevElement).getPropertyType();
		} else if (prevElement instanceof LattePhpConstant) {
			type = ((LattePhpConstant) prevElement).getConstantType();
		} else if (prevElement instanceof LattePhpVariable) {
			type = ((LattePhpVariable) prevElement).getPhpType();
		}
		return type != null ? type : new LattePhpType("mixed", false);
	}

	public static boolean isStatic(@NotNull PsiElement element) {
		PsiElement prev = PsiTreeUtil.skipWhitespacesBackward(element);
		return prev != null && prev.getNode().getElementType() == T_PHP_DOUBLE_COLON;
	}

	public static boolean isFunction(@NotNull PsiElement element) {
		PsiElement prev = PsiTreeUtil.skipWhitespacesBackward(element);
		return prev != null && prev.getNode().getElementType() != T_PHP_DOUBLE_COLON && prev.getNode().getElementType() != T_PHP_OBJECT_OPERATOR;
	}

	public static LattePhpType getReturnType(@NotNull LattePhpMethod element) {
		return getMethodType(element.getProject(), element.getPhpType(), element.getMethodName());
	}

	public static LattePhpType getPropertyType(@NotNull LattePhpStaticVariable element) {
		return getPropertyType(element.getProject(), element.getPhpType(), element.getVariableName());
	}

	public static LattePhpType getConstantType(@NotNull LattePhpConstant element) {
		return getPropertyType(element.getProject(), element.getPhpType(), element.getConstantName());
	}

	public static LattePhpType getPropertyType(@NotNull LattePhpProperty element) {
		return getPropertyType(element.getProject(), element.getPhpType(), element.getPropertyName());
	}

	private static LattePhpType getPropertyType(@NotNull Project project, @NotNull LattePhpType type, @NotNull String elementName) {
		PhpClass first = type.getFirstPhpClass(project);
		if (first == null) {
			return null;
		}

		for (Field field : first.getFields()) {
			if (field.getName().equals(LattePhpUtil.normalizePhpVariable(elementName))) {
				return new LattePhpType(field.getType().toString(), field.getType().isNullable());
			}
		}
		return null;
	}

	private static LattePhpType getMethodType(@NotNull Project project, @NotNull LattePhpType type, @NotNull String elementName) {
		PhpClass first = type.getFirstPhpClass(project);
		if (first == null) {
			return null;
		}

		for (Method phpMethod : first.getMethods()) {
			if (phpMethod.getName().equals(elementName)) {
				return new LattePhpType(phpMethod.getType().toString(), phpMethod.getType().isNullable());
			}
		}
		return null;
	}

	public static LattePhpType getPhpType(@NotNull LattePhpClass element) {
		return new LattePhpType(element.getClassName(), false);
	}

	public static boolean isVarTypeDefinition(@NotNull LattePhpVariable element) {
		PsiElement parent = element.getParent();
		if (parent == null) {
			return false;
		}

		PsiElement prevParent = PsiTreeUtil.prevLeaf(parent.getParent(), true);
		return prevParent != null && prevParent.getText().equals("varType");
	}

	public static boolean isDefinition(@NotNull LattePhpVariable element) {
		if (isVarTypeDefinition(element)) {
			return true;
		}

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

		return false;
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

	@NotNull
	public static String getMacroName(LatteAutoClosedBlock element) {
		return "block";
	}

	public static PsiElement getNameIdentifier(LatteAutoClosedBlock element) {
		ASTNode keyNode = element.getNode().findChildByType(T_MACRO_NAME);
		if (keyNode != null) {
			return keyNode.getPsi();
		} else {
			return null;
		}
	}

	public static PsiElement getNameIdentifier(LatteMacroTag element) {
		ASTNode keyNode = element.getNode().findChildByType(T_MACRO_NAME);
		if (keyNode != null) {
			return keyNode.getPsi();
		} else {
			return null;
		}
	}

	public static String getName(LatteMacroTag element) {
		return element.getMacroName();
	}

	public static PsiElement getNameIdentifier(LattePhpClass element) {
		ASTNode keyNode = element.getNode().findChildByType(T_PHP_VAR_TYPE);
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
