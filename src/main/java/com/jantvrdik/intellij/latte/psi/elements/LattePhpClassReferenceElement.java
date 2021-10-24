package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpClassStub;
import com.jantvrdik.intellij.latte.psi.LattePhpClassUsage;
import org.jetbrains.annotations.NotNull;

public interface LattePhpClassReferenceElement extends BaseLattePhpElement, StubBasedPsiElement<LattePhpClassStub> {

	public abstract String getClassName();

	@NotNull
	LattePhpClassUsage getPhpClassUsage();

}