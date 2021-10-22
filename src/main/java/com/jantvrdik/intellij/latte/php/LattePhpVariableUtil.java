package com.jantvrdik.intellij.latte.php;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpStatementPartElement;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpTypedPartElement;
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;
import com.jantvrdik.intellij.latte.utils.*;

import static com.jantvrdik.intellij.latte.utils.LatteTagsUtil.*;

import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LattePhpVariableUtil {
    public static boolean isVariableDefinition(LattePhpVariable element) {
        if (isVarTypeDefinition(element) || isCaptureDefinition(element) || isBlockDefineVarDefinition(element)) {
            return true;
        }

        if (isVarDefinition(element) || isPhpArrayVarDefinition(element)) {
            if (isNextDefinitionOperator(element)) {
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

        if (isDefinitionInForeach(element)) {
            return true;
        }

        return isDefinitionInFor(element);
    }

    public static boolean isVarTypeDefinition(@NotNull LattePhpVariable element) {
        return LatteUtil.matchParentMacroName(element, Type.VAR_TYPE.getTagName());
    }

    public static boolean isCaptureDefinition(@NotNull LattePhpVariable element) {
        return LatteUtil.matchParentMacroName(element, Type.CAPTURE.getTagName());
    }

    public static boolean isBlockDefineVarDefinition(@NotNull LattePhpVariable element) {
        return LatteUtil.matchParentMacroName(element, Type.DEFINE.getTagName());
    }

    public static boolean isVarDefinition(@NotNull LattePhpVariable element) {
        return LatteUtil.matchParentMacroName(element, Type.VAR.getTagName()) || LatteUtil.matchParentMacroName(element, Type.DEFAULT.getTagName());
    }

    public static boolean isPhpArrayVarDefinition(@NotNull LattePhpVariable element) {
        return (LatteUtil.matchParentMacroName(element, Type.PHP.getTagName()) || LatteUtil.matchParentMacroName(element, Type.DO.getTagName()))
                && element.getParent() instanceof LattePhpArrayOfVariables;
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
        return LatteUtil.matchParentMacroName(element, Type.FOR.getTagName()) && isNextDefinitionOperator(element);
    }

    @NotNull
    public static List<LattePhpVariableDefinition> getVariableOtherDefinitions(@NotNull LattePhpVariable element) {
        PsiElement context = element.getVariableContext();
        if (context == null) {
            return new ArrayList<>();
        }
        int offset = LatteUtil.getStartOffsetInFile(element);
        return findVariables(element, context, offset, false);
    }

    @NotNull
    public static List<PsiCachedElement> getVariablesDefinitionsBeforeElement(PsiElement psiElement) {
        LatteFile file = PsiTreeUtil.getParentOfType(psiElement, LatteFile.class);
        if (file == null) {
            return Collections.emptyList();
        }
        List<PsiCachedElement> out = new ArrayList<>();
        int offset = LatteUtil.getStartOffsetInFile(psiElement);
        findLattePhpVariables(out, file, null, true, false, offset, null);
        return out;
    }

    @NotNull
    public static List<LattePhpVariableDefinition> getVariableDefinition(@NotNull LattePhpVariable element) {
        PsiElement context = element.getVariableContext();
        if (context == null || element.isDefinition()) {
            return new ArrayList<>();
        }
        int offset = LatteUtil.getStartOffsetInFile(element);
        return findVariables(element, context, offset, true);
    }

    @NotNull
    public static Map<PsiElement, PsiCachedElement> getAllVariablesInFile(PsiElement psiElement) {
        LatteFile file = psiElement instanceof LatteFile
                ? (LatteFile) psiElement
                : PsiTreeUtil.getParentOfType(psiElement, LatteFile.class);
        if (file == null) {
            return Collections.emptyMap();
        }
        Map<PsiElement, PsiCachedElement> out = new HashMap<>();
        psiElement.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof LattePhpVariable) {
                    out.put(element, new PsiCachedElement(LatteUtil.getStartOffsetInFile(element), (LattePhpVariable) element));
                } else {
                    super.visitElement(element);
                }
            }
        });
        return out;
    }

    @NotNull
    private static List<LattePhpVariableDefinition> getParentDefinition(
            @NotNull LattePhpVariable element,
            @NotNull PsiElement context,
            int offset
    ) {
        List<PsiCachedElement> contextDefinitions = new ArrayList<>();
        findLattePhpVariables(
                contextDefinitions,
                context,
                element.getVariableName(),
                true,
                false,
                offset,
                context
        );
        if (contextDefinitions.size() > 0) {
            List<LattePhpVariableDefinition> out = new ArrayList<>();
            for (PsiCachedElement contextDefinition : contextDefinitions) {
                if (contextDefinition.getElement() != element) {
                    out.add(new LattePhpVariableDefinition(false, contextDefinition.getElement()));
                }
            }
            return out;
        } else if (context instanceof LatteFile) {
            return new ArrayList<>();
        }

        PsiElement parentContext = getCurrentContext(context.getParent());
        if (parentContext == null) {
            return new ArrayList<>();
        }
        return getParentDefinition(element, parentContext, offset);
    }

    @Nullable
    public static PsiElement getCurrentContext(@NotNull PsiElement element) {
        if (element instanceof LattePhpVariable && (!((LattePhpVariable) element).isDefinition() && !LatteUtil.matchParentMacroName(element, Type.FOR.getTagName()))) {
            PsiElement mainOpenTag = PsiTreeUtil.getParentOfType(element, LatteMacroOpenTag.class, LatteHtmlOpenTag.class);
            if (mainOpenTag != null) {
                element = mainOpenTag.getParent();
            }
        }

        PsiElement context = PsiTreeUtil.getParentOfType(element, LattePairMacro.class, LatteHtmlPairTag.class, LatteFile.class);
        if (context instanceof LattePairMacro) {
            LatteMacroTag openTag = ((LattePairMacro) context).getMacroOpenTag();
            if (openTag == null || !LatteTagsUtil.isContextTag(openTag.getMacroName())) {
                return getCurrentContext(context);
            }
            return context;

        } else if (context instanceof LatteHtmlPairTag) {
            LatteHtmlOpenTag openTag = ((LatteHtmlPairTag) context).getHtmlOpenTag();
            if (openTag.getHtmlTagContent() == null) {
                return getCurrentContext(context);
            }
            for (LatteNetteAttr netteAttr : openTag.getHtmlTagContent().getNetteAttrList()) {
                if (LatteTagsUtil.isContextNetteAttribute(netteAttr.getAttrName().getText())) {
                    return context;
                }
            }
            return getCurrentContext(context);
        }
        return context instanceof LatteFile ? context : PsiTreeUtil.getParentOfType(element, LatteFile.class);
    }

    public static boolean isSameOrParentContext(@Nullable PsiElement check, @Nullable PsiElement probablySameOrParent) {
        if (check == probablySameOrParent) {
            return true;
        } else if (probablySameOrParent == null) {
            return false;
        }
        PsiElement parentContext = getCurrentContext(probablySameOrParent.getParent());
        return isSameOrParentContext(check, parentContext);
    }

    @NotNull
    public static NettePhpType detectVariableType(@NotNull LattePhpVariable element) {
        String variableName = element.getVariableName();
        List<LattePhpVariableDefinition> definitions = getVariableDefinition(element);

        LattePhpStatementPartElement mainStatementPart = element.getPhpStatementPart();

        for (LattePhpVariableDefinition positionedElement : definitions) {
            LattePhpVariable current = positionedElement.getElement();
            int startDepth = 0;
            if (!(current.getParent() instanceof LattePhpArrayOfVariables)) {
                NettePhpType prevPhpType = findPrevPhpType(current);
                if (prevPhpType != null) {
                    return prevPhpType;
                }
            } else {
                startDepth = 1;
            }

            if (current.isDefinitionInForeach()) {
                PsiElement nextElement = PsiTreeUtil.skipWhitespacesForward(current);
                IElementType type = nextElement != null ? nextElement.getNode().getElementType() : null;
                if (type != T_PHP_DOUBLE_ARROW) {
                    LattePhpForeach phpForeach = PsiTreeUtil.getParentOfType(current, LattePhpForeach.class);
                    if (phpForeach != null && phpForeach.getPhpExpression().getPhpStatementList().size() > 0) {
                        return phpForeach.getPhpExpression().getPhpType().withDepth(startDepth + 1);
                    }
                }
            }

            LattePhpStatementPartElement statementPart = current.getPhpStatementPart();
            LattePhpContent phpContent = PsiTreeUtil.getParentOfType(current, LattePhpContent.class);
            if (phpContent != null && statementPart != mainStatementPart) {
                return detectVariableType(phpContent, startDepth);
            }
            return NettePhpType.MIXED;
        }

        NettePhpType templateType = detectVariableTypeFromTemplateType(element, variableName);
        if (templateType != null) {
            return templateType.withDepth(element.getPhpArrayLevel());
        }

        LatteVariableSettings defaultVariable = LatteConfiguration.getInstance(element.getProject()).getVariable(variableName);
        if (defaultVariable != null) {
            return defaultVariable.toPhpType();
        }

        return NettePhpType.MIXED;
    }

    @NotNull
    public static List<Field> findPhpFiledListFromTemplateTypeTag(
        @NotNull PsiElement element,
        @NotNull String variableName
    ) {
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
    public static NettePhpType detectVariableTypeFromTemplateType(
        @NotNull final PsiElement element,
        @NotNull final String variableName
    ) {
        List<Field> fields = findPhpFiledListFromTemplateTypeTag(element, variableName);
        if (fields.size() == 0) {
            return null;
        }
        Field field = fields.get(0);
        return NettePhpType.create(field.getName(), field.getType().toString(), LattePhpUtil.isNullable(field.getType()));
    }

    @NotNull
    private static NettePhpType detectVariableType(@NotNull final LattePhpContent phpContent, final int startDepth) {
        final PsiElement[] varDefinition = {null};
        final boolean[] varDefinitionOperator = {false};
        List<PsiElement> otherParts = new ArrayList<>();
        phpContent.acceptChildren(new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (
                        varDefinition[0] == null && element instanceof LattePhpStatement && ((LattePhpStatement) element).isPhpVariableOnly()
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
            LattePhpStatementPartElement statementPart = ((LattePhpVariable) element).getPhpStatementPart();
            found = statementPart != null ? statementPart.getPhpStatement() : null;
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

    @NotNull
    private static List<LattePhpVariableDefinition> findVariables(
            @NotNull LattePhpVariable element,
            @NotNull PsiElement context,
            int offset,
            boolean onlyDefinitions
    ) {
        List<LattePhpVariableDefinition> definitions = getParentDefinition(element, context, offset);
        if (definitions.size() == 0) {
            List<PsiCachedElement> contextDefinitions = new ArrayList<>();
            findLattePhpVariables(contextDefinitions, context, element.getVariableName(), onlyDefinitions, !onlyDefinitions, offset, null);
            for (PsiCachedElement contextDefinition : contextDefinitions) {
                if (contextDefinition.getElement() != element) {
                    definitions.add(new LattePhpVariableDefinition(true, contextDefinition.getElement()));
                }
            }
        }
        if (definitions.size() == 0 && !(context instanceof LatteFile)) {
            return findVariables(element, context.getParent(), offset, onlyDefinitions);
        }
        return definitions;
    }

    private static void findLattePhpVariables(
            List<PsiCachedElement> properties,
            PsiElement psiElement,
            @Nullable String variableName,
            boolean onlyDefinitions,
            boolean onlyUsages,
            int offset,
            @Nullable PsiElement context
    ) {
        psiElement.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof LattePhpVariable) {
                    if (onlyDefinitions) {
                        if (!((LattePhpVariable) element).isDefinition() || (offset > 0 && LatteUtil.getStartOffsetInFile(element) >= offset)) {
                            return;
                        }
                    } else if (onlyUsages) {
                        if (((LattePhpVariable) element).isDefinition() || (offset > 0 && LatteUtil.getStartOffsetInFile(element) < offset)) {
                            return;
                        }
                    }
                    if (variableName != null && !variableName.equals(((LattePhpVariable) element).getVariableName())) {
                        return;
                    }
                    if (context != null && ((LattePhpVariable) element).getVariableContext() != context) {
                        return;
                    }
                    properties.add(new PsiCachedElement(LatteUtil.getStartOffsetInFile(element), (LattePhpVariable) element));
                } else {
                    super.visitElement(element);
                }
            }
        });
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

    public static String normalizePhpVariable(String name) {
        return name == null ? null : (name.startsWith("$") ? name.substring(1) : name);
    }

}