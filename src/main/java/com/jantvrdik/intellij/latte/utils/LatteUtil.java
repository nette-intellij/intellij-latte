package com.jantvrdik.intellij.latte.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.php.NettePhpType;
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
                .filter(variableElement -> variableElement.getElement() != null && variableElement.getElement().isDefinition())
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
        PsiFile simpleFile = PsiManager.getInstance(project).findFile(file);
        if (simpleFile != null) {
            List<PsiPositionedElement> properties = new ArrayList<>();
            for (PsiElement element : simpleFile.getChildren()) {
                findLattePhpVariables(properties, element);
            }

            for (PsiPositionedElement variable : properties) {
                if (variable.getElement() == null) {
                    continue;
                }

                String varName = variable.getElement().getVariableName();
                if (key == null || key.equals(varName)) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(variable);
                }
            }
        }
        return result != null ? result : Collections.emptyList();
    }

    public static boolean matchParentMacroName(@NotNull PsiElement element, @NotNull String name) {
        LatteMacroClassic macroClassic = PsiTreeUtil.getParentOfType(element, LatteMacroClassic.class);
        if (macroClassic != null) {
            return macroClassic.getOpenTag().getMacroName().equals(name);
        }
        LatteNetteAttr netteAttr = PsiTreeUtil.getParentOfType(element, LatteNetteAttr.class);
        if (netteAttr == null) {
            return false;
        }
        String attributeName = netteAttr.getAttrName().getText();
        return attributeName.equals("n:" + name) || attributeName.equals("n:tag-" + name) || attributeName.equals("n:inner-" + name);
    }

    public static String getSpacesBeforeCaret(@NotNull Editor editor) {
        int startOffset = editor.getCaretModel().getOffset();
        String fileText = editor.getDocument().getText();
        if (fileText.length() < startOffset) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        int position = 1;
        char letter = fileText.charAt(startOffset - position);
        while (letter != '\n' && startOffset > 1) {
            if (letter == '\t' || letter == ' ') {
                out.append(letter);
            }
            position = position + 1;
            int current = startOffset - position;
            if (current < 0) {
                break;
            }
            letter = fileText.charAt(current);
        }
        return out.reverse().toString();
    }

    public static boolean isStringAtCaret(@NotNull Editor editor, @NotNull String string) {
        int startOffset = editor.getCaretModel().getOffset();
        String fileText = editor.getDocument().getText();
        return fileText.length() >= startOffset + string.length() && fileText.substring(startOffset, startOffset + string.length()).equals(string);
    }

    private static <T extends BaseLattePhpElement> Collection<T> findElementsInAllFiles(Project project, String key, Class<T> className, @Nullable Collection<PhpClass> phpClass) {
        List<T> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(LatteFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            LatteFile simpleFile = (LatteFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                List<PsiElement> elements = new ArrayList<>();
                for (PsiElement element : simpleFile.getChildren()) {
                    findFileItem(elements, element, className);
                }

                attachResults(result, key, elements, phpClass);
            }
        }
        return result;
    }

    private static <T extends BaseLattePhpElement> void attachResults(@NotNull List<T> result, String key, List<PsiElement> elements, @Nullable Collection<PhpClass> phpClasses)
    {
        for (PsiElement element : elements) {
            if (!(element instanceof BaseLattePhpElement)) {
                continue;
            }

            if ((phpClasses != null && phpClasses.size() > 0 && !((BaseLattePhpElement) element).getPhpType().hasClass(phpClasses))) {
                continue;
            }

            String varName = ((BaseLattePhpElement) element).getPhpElementName();
            if (key.equals(varName)) {
                result.add((T) element);
            }
        }
    }

    private static void findLattePhpVariables(List<PsiPositionedElement> properties, PsiElement psiElement) {
        findLattePhpVariables(properties, psiElement, null, false, -1, null);
    }

    private static void findLattePhpVariables(
            List<PsiPositionedElement> properties,
            PsiElement psiElement,
            @Nullable String variableName,
            boolean onlyDefinitions,
            int offset,
            @Nullable PsiElement context
    ) {
        /*psiElement.acceptChildren(new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof LattePhpVariable) {
                    if (onlyDefinitions) {
                        if (!((LattePhpVariable) element).isDefinition() || (offset > 0 && getStartOffsetInFile(element) >= offset)) {
                            return;
                        }
                    }
                    if (variableName != null && !variableName.equals(((LattePhpVariable) element).getVariableName())) {
                        return;
                    }
                    if (context != null && ((LattePhpVariable) element).getVariableContext() != context) {
                        return;
                    }
                    properties.add(new PsiPositionedElement(getStartOffsetInFile(element), (LattePhpVariable) element));

                } else {
                    PsiElement nextSibling = element.getNextSibling();
                    while (nextSibling != null) {
                        visitElement(nextSibling);
                        nextSibling = element.getNextSibling();
                    }
                    super.visitElement(element);
                }
            }
        });*/
        psiElement.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof LattePhpVariable) {
                    if (onlyDefinitions) {
                        if (!((LattePhpVariable) element).isDefinition() || (offset > 0 && getStartOffsetInFile(element) >= offset)) {
                            return;
                        }
                    }
                    if (variableName != null && !variableName.equals(((LattePhpVariable) element).getVariableName())) {
                        return;
                    }
                    if (context != null && ((LattePhpVariable) element).getVariableContext() != context) {
                        return;
                    }
                    properties.add(new PsiPositionedElement(getStartOffsetInFile(element), (LattePhpVariable) element));
                } else {
                    super.visitElement(element);
                }
            }
        });
    }

    private static <T> void findFileItem(List<PsiElement> properties, PsiElement psiElement, Class<T> className) {
        psiElement.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (className.isInstance(element)) {
                    properties.add(element);
                } else {
                    super.visitElement(element);
                }
            }
        });
    }

    @Nullable
    public static NettePhpType findFirstLatteTemplateType(PsiElement element) {
        List<LattePhpClassUsage> out = new ArrayList<>();
        findLatteTemplateType(out, element);
        return out.isEmpty() ? null : out.get(0).getPhpType();
    }

    public static void findLatteTemplateType(List<LattePhpClassUsage> classes, PsiElement psiElement) {
        psiElement.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof LattePhpClassUsage && ((LattePhpClassUsage) element).isTemplateType()) {
                    classes.add((LattePhpClassUsage) element);
                } else {
                    super.visitElement(element);
                }
            }
        });
    }

    public static void findLatteMacroTemplateType(List<LatteMacroTag> classes, LatteFile file) {
        file.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof LatteMacroTag && ((LatteMacroTag) element).matchMacroName("templateType")) {
                    classes.add((LatteMacroTag) element);
                } else {
                    super.visitElement(element);
                }
            }
        });
    }

    @NotNull
    public static List<LattePhpVariableDefinition> getVariableDefinition(@NotNull LattePhpVariable element) {
        PsiElement context = element.getVariableContext();
        if (context == null || element.isDefinition()) {
            return new ArrayList<>();
        }

        int offset = getStartOffsetInFile(element);
        List<LattePhpVariableDefinition> definitions = getParentDefinition(element, context, offset);
        if (definitions.size() == 0) {
            List<PsiPositionedElement> contextDefinitions = new ArrayList<>();
            findLattePhpVariables(contextDefinitions, context, element.getVariableName(), true, offset, null);
            for (PsiPositionedElement contextDefinition : contextDefinitions) {
                if (contextDefinition.getElement() != element) {
                    definitions.add(new LattePhpVariableDefinition(true, contextDefinition.getElement()));
                }
            }
        }
        return definitions;
    }

    @NotNull
    private static List<LattePhpVariableDefinition> getParentDefinition(
            @NotNull LattePhpVariable element,
            @NotNull PsiElement context,
            int offset
    ) {
        List<PsiPositionedElement> contextDefinitions = new ArrayList<>();
        findLattePhpVariables(
                contextDefinitions,
                context,
                element.getVariableName(),
                true,
                offset,
                context
        );
        if (contextDefinitions.size() > 0) {
            List<LattePhpVariableDefinition> out = new ArrayList<>();
            for (PsiPositionedElement contextDefinition : contextDefinitions) {
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
        if (!(element instanceof LattePhpVariable) || !((LattePhpVariable) element).isDefinition()) {
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

    public static String normalizeMacroModifier(String name) {
        return name.startsWith("|") ? name.substring(1) : name;
    }

    public static String normalizeNAttrNameModifier(String name) {
        return name.startsWith("n:") ? name.substring(2) : name;
    }
}