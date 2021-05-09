package com.jantvrdik.intellij.latte.php;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpStatementPartElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpTypedPartElement;
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;
import com.jantvrdik.intellij.latte.utils.LatteTypesUtil;
import com.jantvrdik.intellij.latte.utils.LatteUtil;
import com.jantvrdik.intellij.latte.utils.PsiPositionedElement;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LattePhpVariableUtil {
    public static NettePhpType detectVariableType(@NotNull LattePhpVariable element) {
        String variableName = element.getVariableName();
        List<PsiPositionedElement> all = LatteUtil.findVariablesInFileBeforeElement(
                element,
                element.getContainingFile().getOriginalFile().getVirtualFile(),
                element.getVariableName()
        );
        List<PsiPositionedElement> definitions = all.stream().filter(
                psiPositionedElement -> psiPositionedElement.getElement() instanceof LattePhpVariable
                        && ((LattePhpVariable) psiPositionedElement.getElement()).isDefinition()
        ).collect(Collectors.toList());

        LattePhpStatementPartElement mainStatementPart = element.getPhpStatementPart();

        for (PsiPositionedElement positionedElement : definitions) {
            if (!(positionedElement.getElement() instanceof LattePhpVariable)) {
                continue;
            }

            PsiElement current = positionedElement.getElement();
            if (
                    ((LattePhpVariable) current).isVarTypeDefinition()
                            || ((LattePhpVariable) current).isVarDefinition()
                            || ((LattePhpVariable) current).isPhpArrayVarDefinition()
                            || ((LattePhpVariable) current).isCaptureDefinition()
                            || ((LattePhpVariable) current).isBlockDefineVarDefinition()
                            || ((LattePhpVariable) current).isDefinitionInForeach()
            ) {
                int startDepth = 0;
                if (!(current.getParent() instanceof LattePhpArrayOfVariables)) {
                    NettePhpType prevPhpType = findPrevPhpType((LattePhpVariable) current);
                    if (prevPhpType != null) {
                        return prevPhpType;
                    }
                } else {
                    startDepth = 1;
                }

                if (((LattePhpVariable) current).isDefinitionInForeach()) {
                    PsiElement nextElement = PsiTreeUtil.skipWhitespacesForward(current);
                    IElementType type = nextElement != null ? nextElement.getNode().getElementType() : null;
                    if (type != T_PHP_DOUBLE_ARROW) {
                        LattePhpForeach phpForeach = PsiTreeUtil.getParentOfType(current, LattePhpForeach.class);
                        if (phpForeach != null && phpForeach.getPhpExpression().getPhpStatementList().size() > 0) {
                            return phpForeach.getPhpExpression().getPhpType().withDepth(startDepth + 1);
                        }
                    }
                }

                LattePhpStatementPartElement statementPart = ((LattePhpVariable) current).getPhpStatementPart();
                LattePhpContent phpContent = PsiTreeUtil.getParentOfType(current, LattePhpContent.class);
                if (phpContent != null && statementPart != mainStatementPart) {
                    return detectVariableType(phpContent, startDepth);
                }
                return NettePhpType.MIXED;
            }
        }

        NettePhpType templateType = detectVariableTypeFromTemplateType(element, variableName);
        if (templateType != null) {
            return templateType;
        }

        LatteVariableSettings defaultVariable = LatteConfiguration.getInstance(element.getProject()).getVariable(variableName);
        if (defaultVariable != null) {
            return defaultVariable.toPhpType();
        }

        return NettePhpType.MIXED;
    }

    public static List<Field> findPhpFiledListFromTemplateTypeTag(@NotNull PsiElement element, @NotNull String variableName) {
        if (!(element.getContainingFile() instanceof LatteFile)) {
            return Collections.emptyList();
        }

        NettePhpType templateType = LatteUtil.findFirstLatteTemplateType(element.getContainingFile());
        if (templateType == null) {
            return Collections.emptyList();
        }

        Collection<PhpClass> classes = templateType.getPhpClasses(element.getProject());
        List<Field> out = new ArrayList<>();
        for (PhpClass phpClass : classes) {
            for (Field field : phpClass.getFields()) {
                if (!field.isConstant() && field.getModifier().isPublic() && variableName.equals(field.getName())) {
                    out.add(field);
                }
            }
        }
        return out;
    }

    @Nullable
    public static NettePhpType detectVariableTypeFromTemplateType(@NotNull PsiElement element, @NotNull String variableName) {
        List<Field> fields = findPhpFiledListFromTemplateTypeTag(element, variableName);
        if (fields.size() == 0) {
            return null;
        }
        Field field = fields.get(0);
        return NettePhpType.create(field.getName(), field.getType().toString(), LattePhpUtil.isNullable(field.getType()));
    }

    private static NettePhpType detectVariableType(@NotNull LattePhpContent phpContent, int startDepth) {
        final PsiElement[] varDefinition = {null};
        final boolean[] varDefinitionOperator = {false};
        List<PsiElement> otherParts = new ArrayList<>();
        phpContent.acceptChildren(new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (
                        element instanceof LattePhpStatement && ((LattePhpStatement) element).isPhpVariableOnly()
                                || element instanceof LattePhpArrayOfVariables
                ) {
                    varDefinition[0] = element;

                } else if (varDefinition[0] != null && element.getNode().getElementType() == LatteTypes.T_PHP_DEFINITION_OPERATOR) {
                    varDefinitionOperator[0] = true;

                } else if (
                        !(element instanceof LatteMacroModifier)
                                && !LatteTypesUtil.whitespaceTokens.contains(element.getNode().getElementType())
                                && varDefinitionOperator[0]
                ) {
                    otherParts.add(element);
                }
            }
        });

        if (otherParts.size() == 1 && otherParts.get(0) instanceof LattePhpStatement) {
            return ((LattePhpStatement) otherParts.get(0)).getPhpType().withDepth(startDepth);
        }

        if (startDepth > 0) {
            return NettePhpType.MIXED;

        } else if (
                otherParts.stream().anyMatch(element -> element instanceof LattePhpString
                        || element.getNode().getElementType() == T_PHP_CONCATENATION)
        ) {
            return NettePhpType.STRING;

        } else if (otherParts.stream().anyMatch(element -> element.getNode().getElementType() == T_MACRO_ARGS_NUMBER)) {
            return NettePhpType.INT;

        } else if (otherParts.stream().anyMatch(element -> element instanceof LattePhpArray || element instanceof LattePhpArrayOfVariables)) {
            return NettePhpType.ARRAY;
        }

        return NettePhpType.MIXED;
    }

    public static boolean isNextDefinitionOperator(@NotNull PsiElement element) {
        PsiElement found = null;
        if (element instanceof LattePhpVariable && ((LattePhpVariable) element).getPhpStatementPart() != null) {
            found = ((LattePhpVariable) element).getPhpStatementPart().getPhpStatement();
        } else if (element.getParent() instanceof LattePhpArrayOfVariables) {
            found = element.getParent();
        }

        if (found == null) {
            LattePhpTypedArguments typedArgs = PsiTreeUtil.getParentOfType(element, LattePhpTypedArguments.class);
            found = typedArgs != null ? typedArgs : element;
        }

        PsiElement nextElement = PsiTreeUtil.skipWhitespacesForward(found);
        return nextElement != null && nextElement.getNode().getElementType() == LatteTypes.T_PHP_DEFINITION_OPERATOR;
    }

    @Nullable
    private static NettePhpType findPrevPhpType(LattePhpVariable element) {
        LattePhpTypedPartElement typedElement = PsiTreeUtil.getParentOfType(element, LattePhpTypedPartElement.class);
        if (typedElement != null) {
            return typedElement.getPhpType();
        }
        LattePhpStatementPartElement statementPart = element.getPhpStatementPart();
        if (statementPart == null) {
            return null;
        }
        PsiElement phpTypeElement = PsiTreeUtil.skipWhitespacesBackward(statementPart.getPhpStatement());
        return phpTypeElement instanceof LattePhpTypeElement
                ? ((LattePhpTypeElement) phpTypeElement).getPhpType()
                : (phpTypeElement instanceof LattePhpStatement ? ((LattePhpStatement) phpTypeElement).getPhpType() : null);
    }

}