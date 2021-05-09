package com.jantvrdik.intellij.latte.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.indexes.stubs.*;
import com.jantvrdik.intellij.latte.php.LattePhpUtil;
import com.jantvrdik.intellij.latte.php.LattePhpVariableUtil;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpExpressionElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpStatementPartElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpTypedPartElement;
import com.jantvrdik.intellij.latte.settings.LatteFunctionSettings;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.utils.*;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LattePsiImplUtil {
	public static @NotNull String getMacroName(LatteMacroTag element) {
		ASTNode nameNode = getMacroNameNode(element);
		if (nameNode != null) {
			return nameNode.getText();
		}
		return createMacroName(element);
	}

	public static boolean matchMacroName(LatteMacroTag element, @NotNull String name) {
		ASTNode nameNode = getMacroNameNode(element);
		if (nameNode == null) {
			return createMacroName(element).equals(name);
		}
		return matchPsiElement(nameNode, name);
	}

	public static int getMacroNameLength(LatteMacroTag element) {
		ASTNode nameNode = getMacroNameNode(element);
		if (nameNode != null) {
			return nameNode.getTextLength();
		}
		return createMacroName(element).length();
	}

	private static boolean matchPsiElement(ASTNode element, @NotNull String text) {
		return element.getTextLength() == text.length() && element.getText().equals(text);
	}

	private static @Nullable ASTNode getMacroNameNode(LatteMacroTag element) {
		ASTNode elementNode = element.getNode();
		ASTNode nameNode = elementNode.findChildByType(T_MACRO_NAME);
		if (nameNode != null) {
			return nameNode;
		}
		return elementNode.findChildByType(T_MACRO_SHORTNAME);
	}

	private static @NotNull String createMacroName(LatteMacroTag element) {
		LatteMacroContent content = element.getMacroContent();
		if (content == null || element instanceof LatteMacroCloseTag) {
			return "";
		}
		return "=";
	}

	public static @Nullable LattePhpContent getFirstPhpContent(@NotNull LatteMacroContent macroContent) {
		List<LattePhpContent> phpContents = macroContent.getPhpContentList();
		return phpContents.stream().findFirst().isPresent() ? phpContents.stream().findFirst().get() : null;
	}

	public static String getVariableName(@NotNull LattePhpVariable element) {
		PsiElement found = getTextElement(element);
		return found != null ? LattePhpUtil.normalizePhpVariable(found.getText()) : null;
	}

	public static String getVariableName(@NotNull LattePhpStaticVariable element) {
		final LattePhpStaticVariableStub stub = element.getStub();
		if (stub != null) {
			return stub.getVariableName();
		}

		PsiElement found = getTextElement(element);
		return found != null ? LattePhpUtil.normalizePhpVariable(found.getText()) : null;
	}

	public static @Nullable PsiElement getTextElement(@NotNull LattePhpStaticVariable element) {
		return findFirstChildWithType(element, T_MACRO_ARGS_VAR);
	}

	public static @Nullable PsiElement getTextElement(@NotNull LattePhpVariable element) {
		return findFirstChildWithType(element, T_MACRO_ARGS_VAR);
	}

	public static String getConstantName(@NotNull LattePhpConstant element) {
		final LattePhpConstantStub stub = element.getStub();
		if (stub != null) {
			return stub.getConstantName();
		}

		PsiElement found = getTextElement(element);
		return found != null ? found.getText() : null;
	}

	public static String getMethodName(@NotNull LattePhpMethod element) {
		final LattePhpMethodStub stub = element.getStub();
		if (stub != null) {
			return stub.getMethodName();
		}

		PsiElement found = findFirstChildWithType(element, T_PHP_IDENTIFIER);
		if (found == null) {
			found = findFirstChildWithType(element, T_PHP_NAMESPACE_REFERENCE);
		}
		return found != null ? found.getText() : null;
	}

	public static String getPropertyName(@NotNull LattePhpProperty element) {
		final LattePhpPropertyStub stub = element.getStub();
		if (stub != null) {
			return stub.getPropertyName();
		}

		PsiElement found = getTextElement(element);
		return found != null ? found.getText() : null;
	}

	public static String getClassName(LattePhpClassReference classReference) {
		final LattePhpClassStub stub = classReference.getStub();
		if (stub != null) {
			return stub.getClassName();
		}
		return classReference.getPhpClassUsage().getClassName();
	}

	public static @Nullable PsiElement getTextElement(@NotNull LattePhpMethod element) {
		return findFirstChildWithType(element, T_PHP_IDENTIFIER);
	}

	public static @Nullable PsiElement getTextElement(@NotNull LatteMacroModifier element) {
		return findFirstChildWithType(element, T_MACRO_FILTERS);
	}

	public static @Nullable PsiElement getTextElement(@NotNull PsiElement element) {
		return findFirstChildWithType(element, T_PHP_IDENTIFIER);
	}

	public static @Nullable PsiElement getTextElement(@NotNull LattePhpClassUsage element) {
		return element.getNameIdentifier();
	}

	public static @Nullable PsiElement getTextElement(@NotNull LattePhpClassReference element) {
		return element.getPhpClassUsage().getTextElement();
	}

	public static @NotNull PsiElement getTextElement(@NotNull LattePhpNamespaceReference element) {
		return element;
	}

	public static String getClassName(@NotNull LattePhpClassUsage classUsage) {
		StringBuilder out = new StringBuilder();
		classUsage.getParent().acceptChildren(new PsiRecursiveElementVisitor() {
			@Override
			public void visitElement(PsiElement element) {
				IElementType type = element.getNode().getElementType();
				if (TokenSet.create(T_PHP_NAMESPACE_REFERENCE, T_PHP_NAMESPACE_RESOLUTION, T_PHP_IDENTIFIER).contains(type)) {
					out.append(element.getText());
				} else {
					super.visitElement(element);
				}
			}
		});
		return LattePhpUtil.normalizeClassName(out.toString());
	}

	public static String getModifierName(@NotNull LatteMacroModifier element) {
		final LatteFilterStub stub = element.getStub();
		if (stub != null) {
			return stub.getModifierName();
		}

		PsiElement found = getTextElement(element);
		return found != null ? LatteUtil.normalizeMacroModifier(found.getText()) : null;
	}

	public static boolean isVariableModifier(@NotNull LatteMacroModifier element) {
		LattePhpInBrackets variableModifier = PsiTreeUtil.getParentOfType(element, LattePhpInBrackets.class);
		return variableModifier != null;
	}

	public static @NotNull NettePhpType getPhpType(@NotNull LattePhpVariable element) {
		return LattePhpVariableUtil.detectVariableType(element);
	}

	public static @NotNull NettePhpType getPhpType(@NotNull LattePhpTypeElement element) {
		List<String> out = new ArrayList<>();
		for (LattePhpTypePart part : element.getPhpTypePartList()) {
			String item;
			if (part.getPhpClassReference() != null) {
				item = part.getPhpClassReference().getClassName();
			} else {
				item = part.getFirstChild().getText();
			}
			int size = part.getPhpTypeIterableList().size();
			if (size > 0) {
				item += String.join("", Collections.nCopies(part.getPhpTypeIterableList().size(), "[]"));
			}
			out.add(item);
		}
		return NettePhpType.create(String.join("|", out));
	}

	public static @NotNull NettePhpType getPhpType(@NotNull BaseLattePhpElement element) {
		LattePhpStatementPartElement part = element.getPhpStatementPart();
		if (part == null) {
			return NettePhpType.MIXED;
		}
		part = part.getPrevPhpStatementPart();
		if (part == null || part.getPhpElement() == null) {
			return NettePhpType.MIXED;
		}
		return part.getPhpType().withDepth(part.getPhpElement().getPhpArrayLevel());
	}

	public static int getPhpArrayLevel(@NotNull BaseLattePhpElement element) {
		return element.getPhpArrayUsageList().size();
	}

	public static @NotNull List<LattePhpArrayUsage> getPhpArrayUsageList(@NotNull PsiElement element) {
		return Collections.emptyList();
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

	public static @Nullable LattePhpStatementPartElement getPhpStatementPart(@NotNull PsiElement element) {
		PsiElement parent = element.getParent();
		return parent instanceof LattePhpStatementPartElement ? (LattePhpStatementPartElement) parent : null;
	}

	public static @NotNull LattePhpStatement getPhpStatement(@NotNull LattePhpStatementPartElement element) {
		return (LattePhpStatement) element.getParent();
	}

	public static @NotNull NettePhpType getPhpType(@NotNull LattePhpStatement statement) {
		BaseLattePhpElement last = statement.getLastPhpElement();
		return last != null ? last.getReturnType() : NettePhpType.MIXED;
	}

	public static @NotNull NettePhpType getPhpType(@NotNull LattePhpTypedPartElement statement) {
		LattePhpTypeElement typeElement = statement.getPhpTypeElement();
		return typeElement == null ? NettePhpType.MIXED : typeElement.getPhpType();
	}

	public static int getPhpArrayLevel(@NotNull LattePhpExpressionElement expression) {
		List<LattePhpStatement> statements = expression.getPhpStatementList();
		if (statements.size() > 0) {
			BaseLattePhpElement phpElement = statements.get(statements.size() - 1).getLastPhpElement();
			return phpElement != null ? phpElement.getPhpArrayLevel() : 0;
		}
		return 0;
	}

	public static @NotNull NettePhpType getPhpType(@NotNull LattePhpExpression expression) {
		return getPhpType((LattePhpExpressionElement) expression).withDepth(expression.getPhpArrayLevel());
	}

	public static @NotNull NettePhpType getPhpType(@NotNull LattePhpExpressionElement expression) {
		List<LattePhpStatement> statements = expression.getPhpStatementList();
		if (statements.size() > 0) {
			return statements.get(0).getPhpType();
		}
		return NettePhpType.MIXED;
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

	public static @NotNull NettePhpType getPhpType(@NotNull LattePhpStatementPartElement statement) {
		BaseLattePhpElement phpElement = statement.getPhpElement();
		return phpElement == null ? NettePhpType.MIXED : phpElement.getReturnType();
	}

	public static boolean isStatic(@NotNull PsiElement element) {
		PsiElement prev = PsiTreeUtil.skipWhitespacesBackward(element);
		return prev != null && prev.getNode().getElementType() == T_PHP_DOUBLE_COLON;
	}

	public static boolean isFunction(@NotNull PsiElement element) {
		PsiElement prev = PsiTreeUtil.skipWhitespacesBackward(element);
		return prev == null || (prev.getNode().getElementType() != T_PHP_DOUBLE_COLON && prev.getNode().getElementType() != T_PHP_OBJECT_OPERATOR);
	}

	public static @NotNull NettePhpType getReturnType(@NotNull LattePhpVariable element) {
		return element.getPhpType();
	}

	@NotNull
	public static NettePhpType getReturnType(@NotNull LattePhpClassReference element) {
		return element.getPhpClassUsage().getPhpType();
	}

	public static @NotNull NettePhpType getReturnType(@NotNull LattePhpClassUsage element) {
		return element.getPhpType();
	}

	public static @NotNull NettePhpType getReturnType(@NotNull LattePhpNamespaceReference element) {
		return element.getPhpType();
	}

	public static @NotNull NettePhpType getReturnType(@NotNull LattePhpMethod element) {
		NettePhpType type = element.getPhpType();
		Collection<PhpClass> phpClasses = type.getPhpClasses(element.getProject());
		String name = element.getMethodName();
		if (phpClasses.size() == 0) {
			LatteFunctionSettings customFunction = LatteConfiguration.getInstance(element.getProject()).getFunction(name);
			return customFunction == null ? NettePhpType.MIXED : NettePhpType.create(customFunction.getFunctionReturnType());
		}

		List<PhpType> types = new ArrayList<>();
		for (PhpClass phpClass : phpClasses) {
			for (Method phpMethod : phpClass.getMethods()) {
				if (phpMethod.getName().equals(name)) {
					types.add(phpMethod.getType());
				}
			}
		}
		return types.size() > 0 ? NettePhpType.create(types).withDepth(element.getPhpArrayLevel()) : NettePhpType.MIXED;
	}

	public static @NotNull NettePhpType getReturnType(@NotNull BaseLattePhpElement element) {
		Collection<PhpClass> phpClasses = element.getPhpType().getPhpClasses(element.getProject());
		if (phpClasses.size() == 0) {
			return NettePhpType.MIXED;
		}

		List<PhpType> types = new ArrayList<>();
		for (PhpClass phpClass : phpClasses) {
			for (Field field : phpClass.getFields()) {
				if (field.getName().equals(LattePhpUtil.normalizePhpVariable(element.getPhpElementName()))) {
					types.add(field.getType());
				}
			}
		}
		return types.size() > 0 ? NettePhpType.create(types).withDepth(element.getPhpArrayLevel()) : NettePhpType.MIXED;
	}

	public static @NotNull String getNamespaceName(LattePhpNamespaceReference namespaceReference) {
		final LattePhpNamespaceStub stub = namespaceReference.getStub();
		if (stub != null) {
			return stub.getNamespaceName();
		}

		StringBuilder out = new StringBuilder();
		for (PsiElement element : namespaceReference.getParent().getChildren()) {
			if (element instanceof LattePhpNamespaceReference) {
				out.append("\\").append(element.getText());
			}

			if (element == namespaceReference) {
				break;
			}
		}
		return LattePhpUtil.normalizeClassName(out.toString());
	}

	public static @NotNull NettePhpType getPhpType(@NotNull LattePhpClassUsage element) {
		return NettePhpType.create(element.getClassName(), false);
	}

	public static @NotNull NettePhpType getPhpType(@NotNull LattePhpClassReference element) {
		return element.getPhpClassUsage().getPhpType();
	}

	public static @NotNull NettePhpType getPhpType(@NotNull LattePhpNamespaceReference element) {
		return NettePhpType.create(element.getNamespaceName(), false);
	}

	public static boolean isTemplateType(@NotNull LattePhpClassUsage element) {
		return LatteUtil.matchParentMacroName(element, "templateType");
	}

	public static boolean isVarTypeDefinition(@NotNull LattePhpVariable element) {
		return LatteUtil.matchParentMacroName(element, "varType");
	}

	public static boolean isCaptureDefinition(@NotNull LattePhpVariable element) {
		return LatteUtil.matchParentMacroName(element, "capture");
	}

	public static boolean isBlockDefineVarDefinition(@NotNull LattePhpVariable element) {
		return LatteUtil.matchParentMacroName(element, "define");
	}

	public static boolean isVarDefinition(@NotNull LattePhpVariable element) {
		return LatteUtil.matchParentMacroName(element, "var") || LatteUtil.matchParentMacroName(element, "default");
	}

	public static boolean isPhpArrayVarDefinition(@NotNull LattePhpVariable element) {
		return LatteUtil.matchParentMacroName(element, "php") && element.getParent() instanceof LattePhpArrayOfVariables;
	}

	public static boolean isDefinitionInForeach(@NotNull PsiElement element) {
		PsiElement parent = element.getParent();
		if (parent != null && parent.getNode().getElementType() == PHP_FOREACH) {
			PsiElement prevElement = PsiTreeUtil.skipWhitespacesBackward(element);
			IElementType type = prevElement != null ? prevElement.getNode().getElementType() : null;
			return type == T_PHP_AS || type == T_PHP_DOUBLE_ARROW;

		} else if (parent != null && parent.getNode().getElementType() == PHP_ARRAY_OF_VARIABLES) {
			return isDefinitionInForeach(parent);
		}
		return false;
	}

	public static boolean isDefinitionInFor(@NotNull LattePhpVariable element) {
		return LatteUtil.matchParentMacroName(element, "for") && LattePhpVariableUtil.isNextDefinitionOperator(element);
	}

	public static boolean isDefinition(@NotNull LattePhpVariable element) {
		if (
				isVarTypeDefinition(element)
						|| LatteUtil.matchParentMacroName(element, "capture")
						|| isBlockDefineVarDefinition(element)
		) {
			return true;
		}

		if (isVarDefinition(element) || isPhpArrayVarDefinition(element)) {
			if (LattePhpVariableUtil.isNextDefinitionOperator(element)) {
				return true;
			}
		}

		PsiElement parent = element.getParent();
		if (parent == null) {
			return false;
		}

		if (parent.getNode().getElementType() == PHP_ARRAY_OF_VARIABLES) {
			if (LattePhpVariableUtil.isNextDefinitionOperator(parent)) {
				return true;
			}
		}

		if (isDefinitionInForeach(element)) {
			return true;
		}

		return isDefinitionInFor(element);
	}

	public static String getName(LattePhpVariable element) {
		return element.getVariableName();
	}

	public static PsiElement setName(LattePhpMethod element, String newName) {
		ASTNode keyNode = element.getFirstChild().getNode();
		PsiElement method = LatteElementFactory.createMethod(element.getProject(), newName);
		if (method == null) {
			return element;
		}
		return replaceChildNode(element, method, keyNode);
	}

	public static PsiElement setName(LattePhpProperty element, String newName) {
		ASTNode keyNode = element.getFirstChild().getNode();
		PsiElement property = LatteElementFactory.createProperty(element.getProject(), newName);
		if (property == null) {
			return element;
		}
		return replaceChildNode(element, property, keyNode);
	}

	public static PsiElement setName(LattePhpConstant element, String newName) {
		ASTNode keyNode = element.getFirstChild().getNode();
		PsiElement property = LatteElementFactory.createConstant(element.getProject(), newName);
		if (property == null) {
			return element;
		}
		return replaceChildNode(element, property, keyNode);
	}

	public static PsiElement setName(LattePhpStaticVariable element, String newName) {
		ASTNode keyNode = element.getFirstChild().getNode();
		PsiElement property = LatteElementFactory.createStaticVariable(element.getProject(), newName);
		if (property == null) {
			return element;
		}
		return replaceChildNode(element, property, keyNode);
	}

	public static PsiElement setName(LattePhpVariable element, String newName) {
		ASTNode keyNode = element.getFirstChild().getNode();
		PsiElement variable = LatteElementFactory.createVariable(element.getProject(), newName);
		if (variable == null) {
			return element;
		}
		return replaceChildNode(element, variable, keyNode);
	}

	public static PsiElement setName(LattePhpClassUsage element, String newName) {
		ASTNode keyNode = element.getLastChild().getNode();
		PsiElement classUsage;
		if (keyNode.getElementType() == T_PHP_NAMESPACE_REFERENCE) {
			classUsage = LatteElementFactory.createClassRootUsage(element.getProject(), newName);
		} else {
			classUsage = LatteElementFactory.createClassUsage(element.getProject(), newName);
		}

		if (classUsage == null) {
			return element;
		}
		return replaceLastNode(element, classUsage, keyNode);
	}

	@NotNull
	private static PsiElement replaceChildNode(@NotNull PsiElement psiElement, @NotNull PsiElement newElement, @Nullable ASTNode keyNode) {
		ASTNode newKeyNode = newElement.getFirstChild().getNode();
		if (newKeyNode == null) {
			return psiElement;
		}

		if (keyNode == null) {
			psiElement.getNode().addChild(newKeyNode);

		} else {
			psiElement.getNode().replaceChild(keyNode, newKeyNode);
		}
		return psiElement;
	}

	@NotNull
	private static PsiElement replaceLastNode(@NotNull PsiElement psiElement, @NotNull PsiElement newElement, @Nullable ASTNode keyNode) {
		ASTNode newKeyNode = newElement.getLastChild().getNode();
		if (newKeyNode == null) {
			return psiElement;
		}

		if (keyNode == null) {
			psiElement.getNode().addChild(newKeyNode);

		} else {
			psiElement.getNode().replaceChild(keyNode, newKeyNode);
		}
		return psiElement;
	}

	public static PsiElement getNameIdentifier(LatteMacroModifier element) {
		return findFirstChildWithType(element, T_MACRO_FILTERS);
	}

	public static PsiElement getNameIdentifier(LattePhpVariable element) {
		return findFirstChildWithType(element, T_MACRO_ARGS_VAR);
	}

	public static String getName(LattePhpStaticVariable element) {
		PsiElement found = findFirstChildWithType(element, T_MACRO_ARGS_VAR);
		return found != null ? LattePhpUtil.normalizePhpVariable(found.getText()) : null;
	}

	public static PsiElement getNameIdentifier(LattePhpStaticVariable element) {
		return findFirstChildWithType(element, T_MACRO_ARGS_VAR);
	}

	public static PsiElement getNameIdentifier(PsiElement element) {
		return findFirstChildWithType(element, T_PHP_IDENTIFIER);
	}

	public static PsiElement getNameIdentifier(LattePhpNamespaceReference element) {
		return element;
	}

	public static PsiElement getNameIdentifier(LattePhpConstant element) {
		return findFirstChildWithType(element, T_PHP_IDENTIFIER);
	}

	public static PsiElement getNameIdentifier(LattePhpProperty element) {
		return findFirstChildWithType(element, T_PHP_IDENTIFIER);
	}

	public static PsiElement getNameIdentifier(LatteMacroTag element) {
		return findFirstChildWithType(element, T_MACRO_NAME);
	}

	public static String getName(LatteMacroTag element) {
		return element.getMacroName();
	}

	public static PsiElement getNameIdentifier(LattePhpClassUsage element) {
		return element.getFirstChild() != null ? element.getFirstChild() : element;
	}

	public static PsiElement getNameIdentifier(LattePhpClassReference element) {
		return element.getPhpClassUsage().getNameIdentifier();
	}

	public static String getName(LattePhpNamespaceReference element) {
		return element.getNamespaceName();
	}

	public static String getName(PsiElement element) {
		PsiElement found = findFirstChildWithType(element, T_PHP_IDENTIFIER);
		return found != null ? found.getText() : null;
	}

	public static String getName(LatteMacroModifier element) {
		return element.getModifierName();
	}

	public static String getName(LattePhpConstant element) {
		PsiElement found = findFirstChildWithType(element, T_PHP_IDENTIFIER);
		return found != null ? found.getText() : null;
	}

	public static String getName(LattePhpClassUsage element) {
		return element.getClassName();
	}

	public static String getName(LattePhpProperty element) {
		PsiElement found = findFirstChildWithType(element, T_PHP_IDENTIFIER);
		return found != null ? found.getText() : null;
	}

	public static PsiElement setName(PsiElement element, String newName) {
		return element;
	}

	private static PsiElement findFirstChildWithType(PsiElement element, @NotNull IElementType type) {
		ASTNode keyNode = element.getNode().findChildByType(type);
		if (keyNode != null) {
			return keyNode.getPsi();
		} else {
			return null;
		}
	}
}
