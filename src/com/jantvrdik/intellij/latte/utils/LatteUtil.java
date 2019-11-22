package com.jantvrdik.intellij.latte.utils;

import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.LatteFileType;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class LatteUtil {

    public static List<PsiPositionedElement> findVariablesDefinitionsInFileBeforeElement(@NotNull PsiElement element, @NotNull VirtualFile virtualFile) {
        return findVariablesDefinitionsInFileBeforeElement(element, virtualFile, null);
    }

    public static List<PsiPositionedElement> findVariablesDefinitionsInFileBeforeElement(@NotNull PsiElement element, @NotNull VirtualFile virtualFile, @Nullable String key) {
        List<PsiPositionedElement> variables = findVariablesInFileBeforeElement(element, virtualFile, key);

        return variables.stream()
                .filter(variableElement -> variableElement.getElement() instanceof LattePhpVariable && ((LattePhpVariable) variableElement.getElement()).isDefinition())
                .collect(Collectors.toList());
    }

    public static List<PsiPositionedElement> findVariablesInFileBeforeElement(@NotNull PsiElement element, @NotNull VirtualFile virtualFile, @Nullable String key) {
        List<PsiPositionedElement> variables = findVariablesInFile(element.getProject(), virtualFile, key);

        //PsiElement mainParent = PsiTreeUtil.getParentOfType(element, LatteMacroClassic.class, LatteNetteAttr.class);
        //int offset = mainParent != null ? mainParent.getNode().getStartOffsetInParent() : 0;
        int offset = getStartOffsetInFile(element);
        return variables.stream()
                .filter(variableElement -> variableElement.getPosition() <= offset)
                .collect(Collectors.toList());
    }

    public static List<PsiPositionedElement> findVariablesInFile(@NotNull Project project, @NotNull VirtualFile file, @Nullable String key) {
        List<PsiPositionedElement> result = null;
        LatteFile simpleFile = (LatteFile) PsiManager.getInstance(project).findFile(file);
        if (simpleFile != null) {
            List<PsiPositionedElement> properties = new ArrayList<PsiPositionedElement>();
            for (PsiElement element : simpleFile.getChildren()) {
                findElementsByType(properties, element);
            }

            for (PsiPositionedElement variable : properties) {
                if (!(variable.getElement() instanceof LattePhpVariable)) {
                    continue;
                }

                String varName = ((LattePhpVariable) variable.getElement()).getVariableName();
                if (key == null || key.equals(varName)) {
                    if (result == null) {
                        result = new ArrayList<PsiPositionedElement>();
                    }
                    result.add(variable);
                }
            }
        }
        return result != null ? result : Collections.<PsiPositionedElement>emptyList();
    }

    @NotNull
    private static List<PsiElement> collectPsiElementsRecursive(@NotNull PsiElement psiElement) {
        final List<PsiElement> elements = new ArrayList<PsiElement>();
        elements.add(psiElement.getContainingFile());

        psiElement.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                elements.add(element);
                super.visitElement(element);
            }
        });
        return elements;
    }

    public static void findElementsByType(List<PsiPositionedElement> properties, PsiElement psiElement) {
        for (PsiElement element : collectPsiElementsRecursive(psiElement)) {
            if (element instanceof LattePhpVariable) {
                properties.add(new PsiPositionedElement(getStartOffsetInFile(psiElement), element));
            }
        }
    }

    public static int getStartOffsetInFile(PsiElement psiElement) {
        return getStartOffsetInFile(psiElement, 0);
    }

    private static int getStartOffsetInFile(PsiElement psiElement, int offset) {
        PsiElement parent = psiElement.getParent();
        if (parent instanceof LatteFile) {
            return psiElement.getStartOffsetInParent() + offset;
        }
        return getStartOffsetInFile(parent, psiElement.getStartOffsetInParent() + offset);
    }

    public static List<LattePhpVariable> findVariables(Project project) {
        List<LattePhpVariable> result = new ArrayList<LattePhpVariable>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(LatteFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            LatteFile simpleFile = (LatteFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                LattePhpVariable[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, LattePhpVariable.class);
                if (properties != null) {
                    Collections.addAll(result, properties);
                }
            }
        }
        return result;
    }
}