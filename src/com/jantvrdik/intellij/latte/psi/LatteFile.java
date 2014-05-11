package com.jantvrdik.intellij.latte.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.jantvrdik.intellij.latte.LatteFileType;
import com.jantvrdik.intellij.latte.LatteLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LatteFile extends PsiFileBase {

	public LatteFile(@NotNull FileViewProvider viewProvider) {
		super(viewProvider, LatteLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public FileType getFileType() {
		return LatteFileType.INSTANCE;
	}

	@Override
	public String toString() {
		return "Latte file";
	}

	@Override
	public Icon getIcon(int flags) {
		return super.getIcon(flags);
	}
}
