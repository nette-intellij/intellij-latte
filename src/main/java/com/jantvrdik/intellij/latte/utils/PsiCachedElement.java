package com.jantvrdik.intellij.latte.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PsiCachedElement {

    private final int position;
    @NotNull
    private final LattePhpVariable element;
    private PsiElement context;
    private boolean contextInitialized = false;
    private boolean definition = false;
    private List<LattePhpVariable> definitions = null;
    private boolean definitionInitialized = false;
    private boolean varTypeDefinition = false;
    private boolean varTypeDefinitionInitialized = false;
    private String variableName;

    public PsiCachedElement(int position, @NotNull LattePhpVariable element) {
        this.position = position;
        this.element = element;
    }

    public int getPosition() {
        return position;
    }

    @NotNull
    public LattePhpVariable getElement() {
        return element;
    }

    public boolean matchElement(PsiCachedElement cachedElement) {
        return element == cachedElement.element;
    }

    public String getVariableName() {
        if (variableName == null) {
            variableName = element.getVariableName();
        }
        return variableName;
    }

    @Nullable
    public PsiElement getVariableContext() {
        if (!contextInitialized) {
            context = element.getVariableContext();
            contextInitialized = true;
        }
        return context;
    }

    @NotNull
    public List<LattePhpVariable> getVariableDefinitions() {
        if (isDefinition()) {
            return Collections.emptyList();
        }

        if (definitions == null) {
            definitions = new ArrayList<>();
            for (LattePhpVariableDefinition variableDefinition : element.getVariableDefinition()) {
                definitions.add(variableDefinition.getElement());
            }
        }
        return definitions;
    }

    public boolean isDefinition() {
        if (!definitionInitialized) {
            definition = element.isDefinition();
            definitionInitialized = true;
        }
        return definition;
    }

    public boolean isVarTypeDefinition() {
        if (!varTypeDefinitionInitialized) {
            varTypeDefinition = element.isVarTypeDefinition();
            varTypeDefinitionInitialized = true;
        }
        return varTypeDefinition;
    }

    @NotNull
    public Project getProject() {
        return element.getProject();
    }
}