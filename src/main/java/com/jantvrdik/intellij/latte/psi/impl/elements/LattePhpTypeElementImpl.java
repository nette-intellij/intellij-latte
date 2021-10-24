package com.jantvrdik.intellij.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.indexes.LatteStubBasedPsiElement;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpTypeStub;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.LattePhpTypePart;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpTypeElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class LattePhpTypeElementImpl extends LatteStubBasedPsiElement<LattePhpTypeStub> implements LattePhpTypeElement {

	@Nullable NettePhpType phpType = null;

	public LattePhpTypeElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpTypeElementImpl(final LattePhpTypeStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		this.phpType = null;
	}

	@Override
	public @NotNull NettePhpType getReturnType() {
		if (phpType == null) {
			return detectPhpType(this);
		}
		return phpType;
	}

	private static @NotNull NettePhpType detectPhpType(@NotNull LattePhpTypeElement phpType) {
		final LattePhpTypeStub stub = phpType.getStub();
		if (stub != null) {
			return NettePhpType.create(stub.getPhpType());
		}

		List<String> out = new ArrayList<>();
		PsiElement firstElement = phpType.getFirstChild();
		if (TokenSet.create(LatteTypes.T_PHP_NULL_MARK, LatteTypes.T_MACRO_ARGS).contains(firstElement.getNode().getElementType())) {
			out.add("null");
		}

		for (LattePhpTypePart part : phpType.getPhpTypePartList()) {
			String item;
			if (part.getPhpClassReference() != null) {
				item = part.getPhpClassReference().getClassName();
			} else {
				item = part.getFirstChild().getText();
			}

			int size = part.getPhpTypeIterableList().size();
			if (size > 0) {
				item += String.join("", Collections.nCopies(part.getPhpTypeIterableList().size(), "[]"));
			}
			out.add(item);
		}
		return NettePhpType.create(String.join("|", out));
	}
}