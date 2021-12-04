package com.jantvrdik.intellij.latte.php;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.utils.*;

import static com.jantvrdik.intellij.latte.utils.LatteTagsUtil.*;

import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LattePhpVariableUtil {
    static boolean isVariableDefinition(LattePhpVariable element) {
        LattePhpCachedVariable cachedVariable = element.getCachedVariable();
        return cachedVariable != null && element.getCachedVariable().isDefinition();
    }

    public static @NotNull Map<PsiElement, LattePhpCachedVariable> getAllVariablesInFile(PsiElement psiElement) {
        LatteFile file = psiElement instanceof LatteFile
                ? (LatteFile) psiElement
                : PsiTreeUtil.getParentOfType(psiElement, LatteFile.class);
        if (file == null) {
            return Collections.emptyMap();
        }
        Map<PsiElement, LattePhpCachedVariable> out = new HashMap<>();
        psiElement.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof LattePhpVariable) {
                    out.put(element, new LattePhpCachedVariable(LatteUtil.getStartOffsetInFile(element), (LattePhpVariable) element));
                } else {
                    super.visitElement(element);
                }
            }
        });
        return out;
    }

    public static @Nullable PsiElement getCurrentContext(@NotNull PsiElement element) {
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

    public static @NotNull List<Field> findPhpFiledListFromTemplateTypeTag(
        @NotNull PsiElement element,
        @NotNull String variableName
    ) {
        if (!(element.getContainingFile() instanceof LatteFile)) {
            return Collections.emptyList();
        }

        NettePhpType templateType = ((LatteFile) element.getContainingFile()).getFirstLatteTemplateType();
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

    public static String normalizePhpVariable(String name) {
        return name == null ? null : (name.startsWith("$") ? name.substring(1) : name);
    }

}