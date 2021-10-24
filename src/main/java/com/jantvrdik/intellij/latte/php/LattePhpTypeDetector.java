package com.jantvrdik.intellij.latte.php;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.psi.elements.*;
import com.jantvrdik.intellij.latte.settings.LatteFunctionSettings;
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;
import com.jantvrdik.intellij.latte.utils.LattePhpCachedVariable;
import com.jantvrdik.intellij.latte.utils.LatteTypesUtil;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LattePhpTypeDetector {
    public static @NotNull NettePhpType detectPhpType(@NotNull PsiElement element) {
        PsiFile file = element.getContainingFile();
        if (!(file instanceof LatteFile)) {
            return NettePhpType.MIXED;
        }
        return (new Detector((LatteFile) file, element)).detect();
    }

    public static @NotNull NettePhpType detectPrevPhpType(@NotNull BaseLattePhpElement element) {
        LattePhpStatementPartElement part = element.getPhpStatementPart();
        if (part == null) {
            return NettePhpType.MIXED;
        }
        part = part.getPrevPhpStatementPart();
        if (part == null || part.getPhpElement() == null) {
            return NettePhpType.MIXED;
        }
        NettePhpType type = detectPhpType(part);
        return type;
    }

    private static class Detector {
        @NotNull LatteFile file;
        @NotNull PsiElement element;
        @NotNull Project project;

        Detector(@NotNull LatteFile file, @NotNull PsiElement element) {
            this.file = file;
            this.element = element;
            this.project = file.getProject();
        }

        @NotNull NettePhpType detect() {
            return detect(element);
        }

        private @NotNull NettePhpType detect(@NotNull PsiElement current) {
            if (current instanceof LattePhpVariable) {
                return detect((LattePhpVariable) current).withDepth(((LattePhpVariable) current).getPhpArrayLevel());
            } else if (current instanceof LattePhpMethod) {
                return detect(((LattePhpMethod) current)).withDepth(((LattePhpMethod) current).getPhpArrayLevel());
            } else if (current instanceof LattePhpProperty) {
                return detect(((LattePhpProperty) current)).withDepth(((LattePhpProperty) current).getPhpArrayLevel());
            } else if (current instanceof LattePhpConstant) {
                return detect(((LattePhpConstant) current)).withDepth(((LattePhpConstant) current).getPhpArrayLevel());
            } else if (current instanceof LattePhpStaticVariable) {
                return detect(((LattePhpStaticVariable) current)).withDepth(((LattePhpStaticVariable) current).getPhpArrayLevel());
            } else if (current instanceof LattePhpType) {
                return ((LattePhpType) current).getReturnType(); // called from element, because in PhpType is type cached
            } else if (current instanceof LattePhpTypedPartElement) {
                LattePhpType typeElement = ((LattePhpTypedPartElement) current).getPhpType();
                return typeElement == null ? NettePhpType.MIXED : detect(typeElement);
            } else if (current instanceof LattePhpClassUsage) {
                return ((LattePhpClassUsage) current).getReturnType(); // called from element, because in PhpType is type cached
            } else if (current instanceof LattePhpClassReference) {
                return detect(((LattePhpClassReference) current).getPhpClassUsage()); // use detect from LattePhpClassUsage
            } else if (current instanceof LattePhpNamespaceReference) {
                return ((LattePhpNamespaceReference) current).getReturnType(); // called from element, because in PhpType is type cached
            } else if (current instanceof LattePhpStatement) {
                return detect((LattePhpStatement) current);
            } else if (current instanceof LattePhpStatementPartElement) {
                return detect((LattePhpStatementPartElement) current);
            } else if (current instanceof LattePhpExpression) {
                return detect((LattePhpExpression) current);
            } else if (current instanceof LattePhpExpressionElement) {
                return detect((LattePhpExpressionElement) current);
            } else if (current instanceof LattePhpArray) {
                return detect((LattePhpArray) current);
            }
            return NettePhpType.MIXED;
        }

        private @NotNull NettePhpType detect(@NotNull LattePhpVariableElement variable) {
            LattePhpCachedVariable cachedVariable = variable.getCachedVariable();
            if (cachedVariable == null) {
                return NettePhpType.MIXED;
            }

            if (cachedVariable.isCaptureDefinition()) {
                return NettePhpType.STRING;

            } else if (cachedVariable.isDefinitionInForeach()) {
                PsiElement nextElement = PsiTreeUtil.skipWhitespacesForward(variable);
                IElementType type = nextElement != null ? nextElement.getNode().getElementType() : null;
                if (type != T_PHP_DOUBLE_ARROW) {
                    LattePhpForeach phpForeach = PsiTreeUtil.getParentOfType(variable, LattePhpForeach.class);
                    return phpForeach != null && phpForeach.getPhpExpression().getPhpStatementList().size() > 0
                            ? detect(phpForeach.getPhpExpression()).withDepth(variable.getParent().getNode().getElementType() == PHP_ARRAY_OF_VARIABLES ? 2 : 1)
                            : NettePhpType.MIXED;
                }
            }

            List<LattePhpCachedVariable> definitions = file.getCachedVariableDefinitions(variable);
            if (definitions.size() > 0) {
                LattePhpCachedVariable lastDefinition = definitions.get(definitions.size() - 1);

                LattePhpTypedPartElement typedPart = PsiTreeUtil.getParentOfType(lastDefinition.getElement(), LattePhpTypedPartElement.class);
                if (typedPart != null && typedPart.getPhpType() != null) {
                    return detect(typedPart);
                }

                LattePhpStatement valueStatement = lastDefinition.getNextStatement();
                if (valueStatement != null && valueStatement != (variable.getPhpStatementPart() != null ? lastDefinition.getPhpStatement() : null)) {
                    return detect(valueStatement);
                }

                if (variable != lastDefinition.getElement()) {
                    return detect(lastDefinition.getElement());
                }
            }

            NettePhpType phpType = detectVariableTypeFromTemplateType(cachedVariable);
            if (phpType != null) {
                return phpType;
            }

            NettePhpType configurationType = detectVariableTypeFromConfiguration(cachedVariable);
            if (configurationType != null) {
                return configurationType;
            }
            return detectPrimitiveType(cachedVariable);
        }

        private @NotNull NettePhpType detect(@NotNull LattePhpMethod method) {
            NettePhpType type = method.getPrevReturnType();
            Collection<PhpClass> phpClasses = type.getPhpClasses(project);
            String name = method.getMethodName();
            if (phpClasses.size() == 0) {
                LatteFunctionSettings customFunction = LatteConfiguration.getInstance(project).getFunction(name);
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
            return types.size() > 0 ? NettePhpType.create(types) : NettePhpType.MIXED;
        }

        private @NotNull NettePhpType detect(@NotNull LattePhpProperty property) {
            NettePhpType type = property.getPrevReturnType();
            Collection<PhpClass> phpClasses = type.getPhpClasses(project);
            String name = property.getPropertyName();

            List<PhpType> types = new ArrayList<>();
            for (PhpClass phpClass : phpClasses) {
                for (Field field : phpClass.getFields()) {
                    if (!field.isConstant() && !field.getModifier().isStatic() && field.getModifier().isPublic() && field.getName().equals(name)) {
                        types.add(field.getType());
                    }
                }
            }
            return types.size() > 0 ? NettePhpType.create(types) : NettePhpType.MIXED;
        }

        private @NotNull NettePhpType detect(@NotNull LattePhpStaticVariable property) {
            NettePhpType type = property.getPrevReturnType();
            Collection<PhpClass> phpClasses = type.getPhpClasses(project);
            String name = property.getVariableName();

            List<PhpType> types = new ArrayList<>();
            for (PhpClass phpClass : phpClasses) {
                for (Field field : phpClass.getFields()) {
                    if (!field.isConstant() && field.getModifier().isStatic() && field.getModifier().isPublic() && field.getName().equals(name)) {
                        types.add(field.getType());
                    }
                }
            }
            return types.size() > 0 ? NettePhpType.create(types) : NettePhpType.MIXED;
        }

        private @NotNull NettePhpType detect(@NotNull LattePhpConstant constant) {
            NettePhpType type = constant.getPrevReturnType();
            Collection<PhpClass> phpClasses = type.getPhpClasses(project);
            String name = constant.getConstantName();

            List<PhpType> types = new ArrayList<>();
            for (PhpClass phpClass : phpClasses) {
                for (Field field : phpClass.getFields()) {
                    if (field.isConstant() && field.getModifier().isPublic() && field.getName().equals(name)) {
                        types.add(field.getType());
                    }
                }
            }
            return types.size() > 0 ? NettePhpType.create(types) : NettePhpType.MIXED;
        }

        private @NotNull NettePhpType detect(@NotNull LattePhpStatement statement) {
            BaseLattePhpElement last = statement.getLastPhpElement();
            return last != null ? detect(last) : NettePhpType.MIXED;
        }

        private @NotNull NettePhpType detect(@NotNull LattePhpStatementPartElement statementPart) {
            BaseLattePhpElement phpElement = statementPart.getPhpElement();
            return phpElement == null ? NettePhpType.MIXED : detect(phpElement);
        }

        private @NotNull NettePhpType detect(@NotNull LattePhpArray phpArray) {
            //phpArray.getPhpArrayDefinitionContent().getPhpArrayItemList()
            return NettePhpType.ARRAY; //todo: detect array content like int[], etc.
        }

        private @NotNull NettePhpType detect(@NotNull LattePhpExpression expression) {
            return detect((LattePhpExpressionElement) expression).withDepth(expression.getPhpArrayLevel());
        }

        private @NotNull NettePhpType detect(@NotNull LattePhpExpressionElement expressionElement) {
            List<LattePhpStatement> statements = expressionElement.getPhpStatementList();
            if (statements.size() > 0) {
                return detect(statements.get(0));
            }
            return NettePhpType.MIXED;
        }

        @Nullable
        public NettePhpType detectVariableTypeFromTemplateType(@NotNull LattePhpCachedVariable variable) {
            NettePhpType templateType = file.getFirstLatteTemplateType();
            if (templateType == null) {
                return null;
            }

            String variableName = variable.getVariableName();
            Collection<PhpClass> classes = templateType.getPhpClasses(project);
            for (PhpClass phpClass : classes) {
                for (Field field : phpClass.getFields()) {
                    if (!field.isConstant() && field.getModifier().isPublic() && variableName.equals(field.getName())) {
                        return NettePhpType.create(field.getName(), field.getType().toString(), LattePhpUtil.isNullable(field.getType()));
                    }
                }
            }
            return null;
        }

        @Nullable
        public NettePhpType detectVariableTypeFromConfiguration(@NotNull LattePhpCachedVariable variable) {
            LatteVariableSettings defaultVariable = LatteConfiguration.getInstance(project).getVariable(variable.getVariableName());
            if (defaultVariable != null) {
                return defaultVariable.toPhpType();
            }
            return null;
        }

        private @NotNull NettePhpType detectPrimitiveType(@NotNull LattePhpCachedVariable variable) {
            PsiElement statement = variable.getNextDefinedElement();
            if (statement == null) {
                return NettePhpType.MIXED;
            }

            List<PsiElement> otherParts = new ArrayList<>();
            if (statement instanceof LattePhpStatement) {
                statement.acceptChildren(new PsiElementVisitor() {
                    @Override
                    public void visitElement(@NotNull PsiElement element) {
                        if (!LatteTypesUtil.whitespaceTokens.contains(element.getNode().getElementType()) && !(element instanceof LatteMacroModifier)) {
                            otherParts.add(element);
                        }
                    }
                });
            } else {
                otherParts.add(statement);
            }

            if (otherParts.stream().anyMatch(element -> element instanceof LattePhpString || element.getNode().getElementType() == T_PHP_CONCATENATION)) {
                return NettePhpType.STRING;

            } else if (otherParts.stream().anyMatch(element -> element.getNode().getElementType() == T_MACRO_ARGS_NUMBER)) {
                return NettePhpType.INT;

            } else if (otherParts.stream().anyMatch(element -> element instanceof LattePhpArray || element instanceof LattePhpArrayOfVariables)) {
                if (otherParts.size() == 1 && otherParts.get(0) instanceof LattePhpArray) {
                    return detect((LattePhpArray) otherParts.get(0));
                }
                return NettePhpType.ARRAY;
            }
            return NettePhpType.MIXED;
        }
    }

}