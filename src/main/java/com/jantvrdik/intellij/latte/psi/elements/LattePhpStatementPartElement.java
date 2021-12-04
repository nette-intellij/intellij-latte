package com.jantvrdik.intellij.latte.psi.elements;

import com.jantvrdik.intellij.latte.php.LattePhpTypeDetector;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.LattePhpStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LattePhpStatementPartElement extends LattePsiElement {

	default @NotNull NettePhpType getReturnType() {
		return LattePhpTypeDetector.detectPhpType(this);
	}

	@NotNull LattePhpStatement getPhpStatement();

	@Nullable LattePhpStatementPartElement getPrevPhpStatementPart();

	@Nullable BaseLattePhpElement getPhpElement();

}