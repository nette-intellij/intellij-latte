package com.jantvrdik.intellij.latte.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpVariableElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;
import static com.jantvrdik.intellij.latte.psi.LatteTypes.PHP_ARRAY_OF_VARIABLES;

public class LattePhpCachedVariable {

    private final int position;
    private final @NotNull LattePhpVariableElement element;
    private @Nullable PsiElement context = null;
    private boolean contextInitialized = false;
    private boolean definition = false;
    private @Nullable List<LattePhpVariableElement> definitions = null;
    private boolean definitionInitialized = false;
    private boolean definitionInForeachInitialized = false;
    private boolean definitionInForeach = false;
    private @Nullable String parentMacroName = null;
    private LattePhpStatement statement = null;
    private boolean statementInitialized = false;
    private boolean probablyUndefined = false;
    private boolean nextElementInitialized = false;
    private @Nullable PsiElement nextElement = null;

    public LattePhpCachedVariable(int position, @NotNull LattePhpVariableElement element) {
        this.position = position;
        this.element = element;
    }

    public static @NotNull LattePhpCachedVariable fromElement(@NotNull LattePhpVariable element) {
        return new LattePhpCachedVariable(0, element);
    }

    public boolean isProbablyUndefined() {
        return probablyUndefined; //todo: implement content
    }

    public int getPosition() {
        return position;
    }

    public @NotNull LattePhpVariableElement getElement() {
        return element;
    }

    public boolean matchElement(LattePhpCachedVariable cachedElement) {
        return element == cachedElement.element;
    }

    public String getVariableName() {
        // name must be cached in variable element
        return element.getVariableName();
    }

    public @NotNull Project getProject() {
        // project must be cached in variable element
        return element.getProject();
    }

    public @Nullable PsiElement getVariableContext() {
        if (!contextInitialized) {
            context = findVariableContext(element);
            contextInitialized = true;
        }
        return context;
    }

    public @NotNull List<LattePhpVariableElement> getVariableDefinitions() {
        if (isDefinition()) {
            return Collections.emptyList();
        }

        if (definitions == null) {
            definitions = new ArrayList<>();
            for (LattePhpCachedVariable variable: element.getVariableDefinition()) {
                definitions.add(variable.getElement());
            }
        }
        return definitions;
    }

    public boolean isDefinition() {
        if (!definitionInitialized) {
            definition = isVariableDefinition();
            definitionInitialized = true;
        }
        return definition;
    }

    public String getParentMacroName() {
        if (parentMacroName == null) {
            parentMacroName = LatteUtil.getParentMacroName(element);
        }
        return parentMacroName;
    }

    public boolean isVarTypeDefinition() {
        return LatteTagsUtil.Type.VAR_TYPE.getTagName().equals(getParentMacroName());
    }

    public boolean isCaptureDefinition() {
        return LatteTagsUtil.Type.CAPTURE.getTagName().equals(getParentMacroName());
    }

    public boolean isBlockDefineVarDefinition() {
        return LatteTagsUtil.Type.DEFINE.getTagName().equals(getParentMacroName());
    }

    public boolean isVarDefinition() {
        return LatteTagsUtil.Type.VAR.getTagName().equals(getParentMacroName())
                || LatteTagsUtil.Type.DEFAULT.getTagName().equals(getParentMacroName());
    }

    public boolean isParametersDefinition() {
        return LatteTagsUtil.Type.PARAMETERS.getTagName().equals(getParentMacroName());
    }

    public boolean isPhpArrayVarDefinition() {
        return element.getParent() instanceof LattePhpArrayOfVariables
                && (LatteTagsUtil.Type.PHP.getTagName().equals(getParentMacroName())
                || LatteTagsUtil.Type.DO.getTagName().equals(getParentMacroName()));
    }

    public boolean isDefinitionInForeach() {
        if (!definitionInForeachInitialized) {
            definitionInForeach = isDefinitionInForeach(this.getElement());
            definitionInForeachInitialized = true;
        }
        return definitionInForeach;
    }

    public boolean isNextDefinitionOperator() {
        return isNextDefinitionOperator(element);
    }

    public @Nullable LattePhpStatement getNextStatement() {
        return getNextDefinedElement() instanceof LattePhpStatement ? (LattePhpStatement) getNextDefinedElement() : null;
    }

    public @Nullable PsiElement getNextDefinedElement() {
        if (!nextElementInitialized) {
            PsiElement element = getNextElement(this.getElement());
            if (element == null || element.getNode().getElementType() != T_PHP_DEFINITION_OPERATOR) {
                nextElement = null;
            } else {
                nextElement = PsiTreeUtil.skipWhitespacesAndCommentsForward(element);
            }
            nextElementInitialized = true;
        }
        return nextElement;
    }

    public @Nullable LattePhpStatement getPhpStatement() {
        if (!statementInitialized) {
            statement = PsiTreeUtil.getParentOfType(element, LattePhpStatement.class);
            statementInitialized = true;
        }
        return statement;

    }

    private boolean isVariableDefinition() {
        if (isVarTypeDefinition() || isCaptureDefinition() || isBlockDefineVarDefinition() || isParametersDefinition()) {
            return true;
        }

        if (isVarDefinition() || isPhpArrayVarDefinition()) {
            if (isNextDefinitionOperator()) {
                return true;
            }
        }

        PsiElement parent = element.getParent();
        if (parent == null) {
            return false;
        }

        if (parent.getNode().getElementType() == PHP_ARRAY_OF_VARIABLES) {
            if (isNextDefinitionOperator(parent)) {
                return true;
            }
        }

        if (isDefinitionInForeach()) {
            return true;
        }
        return isDefinitionInFor();
    }

    private boolean isDefinitionInFor() {
        return LatteUtil.matchParentMacroName(element, LatteTagsUtil.Type.FOR.getTagName()) && isNextDefinitionOperator();
    }

    public static boolean isSameOrParentContext(@Nullable PsiElement check, @Nullable PsiElement probablySameOrParent) {
        if (check == probablySameOrParent) {
            return true;
        } else if (probablySameOrParent == null) {
            return false;
        }
        PsiElement parentContext = findVariableContext(probablySameOrParent.getParent());
        return isSameOrParentContext(check, parentContext);
    }

    private static @Nullable PsiElement findVariableContext(@NotNull PsiElement element) {
        if (element instanceof LattePhpVariable && (!((LattePhpVariable) element).isDefinition() && !LatteUtil.matchParentMacroName(element, LatteTagsUtil.Type.FOR.getTagName()))) {
            PsiElement mainOpenTag = PsiTreeUtil.getParentOfType(element, LatteMacroOpenTag.class, LatteHtmlOpenTag.class);
            if (mainOpenTag != null) {
                element = mainOpenTag.getParent();
            }
        }

        PsiElement context = PsiTreeUtil.getParentOfType(element, LattePairMacro.class, LatteHtmlPairTag.class, LatteFile.class);
        if (context instanceof LattePairMacro) {
            LatteMacroTag openTag = ((LattePairMacro) context).getMacroOpenTag();
            if (openTag == null || !LatteTagsUtil.isContextTag(openTag.getMacroName())) {
                return findVariableContext(context);
            }
            return context;

        } else if (context instanceof LatteHtmlPairTag) {
            LatteHtmlOpenTag openTag = ((LatteHtmlPairTag) context).getHtmlOpenTag();
            if (openTag.getHtmlTagContent() == null) {
                return findVariableContext(context);
            }
            for (LatteNetteAttr netteAttr : openTag.getHtmlTagContent().getNetteAttrList()) {
                if (LatteTagsUtil.isContextNetteAttribute(netteAttr.getAttrName().getText())) {
                    return context;
                }
            }
            return findVariableContext(context);
        }
        return context instanceof LatteFile ? context : PsiTreeUtil.getParentOfType(element, LatteFile.class);
    }

    public static @Nullable PsiElement getNextElement(@NotNull PsiElement element) {
        LattePhpStatement statement = PsiTreeUtil.getParentOfType(element, LattePhpStatement.class);
        if (statement == null) {
            LattePhpTypedArguments typedArguments = PsiTreeUtil.getParentOfType(element, LattePhpTypedArguments.class);
            return typedArguments != null ? PsiTreeUtil.skipWhitespacesAndCommentsForward(typedArguments) : null;
        }
        return PsiTreeUtil.skipWhitespacesAndCommentsForward(statement);
    }

    public static boolean isNextDefinitionOperator(@NotNull PsiElement element) {
        PsiElement nextElement = getNextElement(element);
        return nextElement != null && nextElement.getNode().getElementType() == LatteTypes.T_PHP_DEFINITION_OPERATOR;
    }

    private static boolean isDefinitionInForeach(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent != null && parent.getNode().getElementType() == PHP_FOREACH) {
            PsiElement prevElement = PsiTreeUtil.skipWhitespacesAndCommentsBackward(element);
            if (prevElement != null && prevElement.getNode().getElementType() == T_PHP_REFERENCE_OPERATOR) {
                prevElement = PsiTreeUtil.skipWhitespacesAndCommentsBackward(prevElement);
            }
            IElementType type = prevElement != null ? prevElement.getNode().getElementType() : null;
            return type == T_PHP_AS || type == T_PHP_DOUBLE_ARROW;

        } else if (parent != null && parent.getNode().getElementType() == PHP_ARRAY_OF_VARIABLES) {
            return isDefinitionInForeach(parent);
        }
        return false;
    }
}