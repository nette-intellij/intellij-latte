package com.jantvrdik.intellij.latte.psi.elements;

import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.LattePhpStatementFirstPart;
import com.jantvrdik.intellij.latte.psi.LattePhpStatementPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LattePhpStatementElement extends LattePsiElement {

	@NotNull LattePhpStatementFirstPart getPhpStatementFirstPart();

	@NotNull List<LattePhpStatementPart> getPhpStatementPartList();

	NettePhpType getReturnType();

	boolean isPhpVariableOnly();

	boolean isPhpClassReferenceOnly();

	@Nullable BaseLattePhpElement getLastPhpElement();

}