package com.jantvrdik.intellij.latte.utils;

import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.psi.elements.BaseLattePhpElement;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.LatteFileType;
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

        int offset = getStartOffsetInFile(element);
        return variables.stream()
                .filter(variableElement -> variableElement.getPosition() <= offset)
                .collect(Collectors.toList());
    }

    public static List<PsiPositionedElement> findVariablesInFileAfterElement(@NotNull PsiElement element, @NotNull VirtualFile virtualFile, @Nullable String key) {
        List<PsiPositionedElement> variables = findVariablesInFile(element.getProject(), virtualFile, key);

        int offset = getStartOffsetInFile(element);
        return variables.stream()
                .filter(variableElement -> variableElement.getPosition() >= offset)
                .collect(Collectors.toList());
    }

    public static List<PsiPositionedElement> findVariablesInFile(@NotNull Project project, @NotNull VirtualFile file, @Nullable String key) {
        List<PsiPositionedElement> result = null;
        LatteFile simpleFile = (LatteFile) PsiManager.getInstance(project).findFile(file);
        if (simpleFile != null) {
            List<PsiPositionedElement> properties = new ArrayList<PsiPositionedElement>();
            for (PsiElement element : simpleFile.getChildren()) {
                findLattePhpVariables(properties, element);
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

    public static Collection<BaseLattePhpElement> findMethods(Project project, String key, @Nullable PhpClass phpClass) {
        List<BaseLattePhpElement> result = new ArrayList<BaseLattePhpElement>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(LatteFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            LatteFile simpleFile = (LatteFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                List<PsiElement> elements = new ArrayList<PsiElement>();
                for (PsiElement element : simpleFile.getChildren()) {
                    findLattePhpMethods(elements, element);
                }

                attachResults(result, key, elements, phpClass);
            }
        }
        return result;
    }

    public static Collection<BaseLattePhpElement> findFunctions(Project project, String key) {
        List<BaseLattePhpElement> result = new ArrayList<BaseLattePhpElement>();
        findMethods(project, key, null)
            .forEach(method -> {
                if (((LattePhpMethod) method).isFunction()) {
                    result.add(method);
                }
            });
        return result;
    }

    public static Collection<BaseLattePhpElement> findProperties(Project project, String key, @NotNull PhpClass phpClass) {
        List<BaseLattePhpElement> result = new ArrayList<BaseLattePhpElement>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(LatteFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            LatteFile simpleFile = (LatteFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                List<PsiElement> elements = new ArrayList<PsiElement>();
                for (PsiElement element : simpleFile.getChildren()) {
                    findLattePhpProperties(elements, element);
                }

                attachResults(result, key, elements, phpClass);
            }
        }
        return result;
    }

    public static Collection<BaseLattePhpElement> findConstants(Project project, String key, @NotNull PhpClass phpClass) {
        List<BaseLattePhpElement> result = new ArrayList<BaseLattePhpElement>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(LatteFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            LatteFile simpleFile = (LatteFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                List<PsiElement> elements = new ArrayList<PsiElement>();
                for (PsiElement element : simpleFile.getChildren()) {
                    findLattePhpConstants(elements, element);
                }

                attachResults(result, key, elements, phpClass);
            }
        }
        return result;
    }

    public static Collection<BaseLattePhpElement> findClasses(Project project, String key) {
        List<BaseLattePhpElement> result = new ArrayList<BaseLattePhpElement>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(LatteFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            LatteFile simpleFile = (LatteFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                List<PsiElement> elements = new ArrayList<PsiElement>();
                for (PsiElement element : simpleFile.getChildren()) {
                    findLattePhpClasses(elements, element);
                }

                attachResults(result, LattePhpUtil.normalizeClassName(key), elements);
            }
        }
        return result;
    }

    public static Collection<BaseLattePhpElement> findStaticVariables(Project project, String key, @NotNull PhpClass phpClass) {
        List<BaseLattePhpElement> result = new ArrayList<BaseLattePhpElement>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(LatteFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            LatteFile simpleFile = (LatteFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                List<PsiElement> elements = new ArrayList<PsiElement>();
                for (PsiElement element : simpleFile.getChildren()) {
                    findLattePhpStaticVariables(elements, element);
                }

                attachResults(result, key, elements, phpClass);
            }
        }
        return result;
    }

    public static boolean matchParentMacroName(@NotNull PsiElement element, @NotNull String name) {
        LatteMacroClassic macroClassic = PsiTreeUtil.getParentOfType(element, LatteMacroClassic.class);
        if (macroClassic == null) {
            return false;
        }
        return macroClassic.getOpenTag().getMacroName().equals(name);
    }

    private static void attachResults(@NotNull List<BaseLattePhpElement> result, String key, List<PsiElement> elements, @Nullable PhpClass phpClass)
    {
        for (PsiElement element : elements) {
            if (!(element instanceof BaseLattePhpElement) || (phpClass != null && !((BaseLattePhpElement) element).getPhpType().hasClass(phpClass.getFQN()))) {
                continue;
            }

            String varName = ((BaseLattePhpElement) element).getPhpElementName();
            if (key.equals(varName)) {
                result.add((BaseLattePhpElement) element);
            }
        }
    }

    private static void attachResults(@NotNull List<BaseLattePhpElement> result, String key, List<PsiElement> elements)
    {
        for (PsiElement constant : elements) {
            if (!(constant instanceof BaseLattePhpElement)) {
                continue;
            }

            String varName = LattePhpUtil.normalizeClassName(((BaseLattePhpElement) constant).getPhpElementName());
            if (key.equals(varName)) {
                result.add((BaseLattePhpElement) constant);
            }
        }
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

    private static void findLattePhpVariables(List<PsiPositionedElement> properties, PsiElement psiElement) {
        for (PsiElement element : collectPsiElementsRecursive(psiElement)) {
            if (element instanceof LattePhpVariable) {
                properties.add(new PsiPositionedElement(getStartOffsetInFile(element), element));
            }
        }
    }

    private static void findLattePhpMethods(List<PsiElement> properties, PsiElement psiElement) {
        for (PsiElement element : collectPsiElementsRecursive(psiElement)) {
            if (element instanceof LattePhpMethod) {
                properties.add(element);
            }
        }
    }

    private static void findLattePhpProperties(List<PsiElement> properties, PsiElement psiElement) {
        for (PsiElement element : collectPsiElementsRecursive(psiElement)) {
            if (element instanceof LattePhpProperty) {
                properties.add(element);
            }
        }
    }

    private static void findLattePhpConstants(List<PsiElement> properties, PsiElement psiElement) {
        for (PsiElement element : collectPsiElementsRecursive(psiElement)) {
            if (element instanceof LattePhpConstant) {
                properties.add(element);
            }
        }
    }

    private static void findLattePhpClasses(List<PsiElement> properties, PsiElement psiElement) {
        for (PsiElement element : collectPsiElementsRecursive(psiElement)) {
            if (element instanceof LattePhpClass) {
                properties.add(element);
            }
        }
    }

    private static void findLattePhpStaticVariables(List<PsiElement> properties, PsiElement psiElement) {
        for (PsiElement element : collectPsiElementsRecursive(psiElement)) {
            if (element instanceof LattePhpStaticVariable) {
                properties.add(element);
            }
        }
    }

    @Nullable
    public static LattePhpType findFirstLatteTemplateType(PsiElement element) {
        List<LattePhpClass> out = new ArrayList<LattePhpClass>();
        findLatteTemplateType(out, element);
        return out.isEmpty() ? null : out.get(0).getPhpType();
    }

    public static void findLatteTemplateType(List<LattePhpClass> classes, PsiElement parent) {
        for (PsiElement element : collectPsiElementsRecursive(parent)) {
            if (element instanceof LattePhpClass && ((LattePhpClass) element).isTemplateType()) {
                classes.add((LattePhpClass) element);
            }
        }
    }

    public static void findLatteMacroTemplateType(List<LatteMacroTag> classes, LatteFile file) {
        for (PsiElement element : collectPsiElementsRecursive(file)) {
            if (element instanceof LatteMacroTag && ((LatteMacroTag) element).getMacroName().equals("templateType")) {
                classes.add((LatteMacroTag) element);
            }
        }
    }

    public static int getStartOffsetInFile(PsiElement psiElement) {
        return getStartOffsetInFile(psiElement, 0);
    }

    private static int getStartOffsetInFile(PsiElement psiElement, int offset) {
        PsiElement parent = psiElement.getParent();
        if (parent == null || parent instanceof LatteFile) {
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