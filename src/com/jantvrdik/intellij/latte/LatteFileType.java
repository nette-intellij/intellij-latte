package com.jantvrdik.intellij.latte;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LatteFileType extends LanguageFileType {
	public static final LatteFileType INSTANCE = new LatteFileType();

	private LatteFileType() {
		super(LatteLanguage.INSTANCE);
	}

	@NotNull
	@Override
	public String getName() {
		return "Latte file";
	}

	@NotNull
	@Override
	public String getDescription() {
		return "Latte template files";
	}

	@NotNull
	@Override
	public String getDefaultExtension() {
		return "latte";
	}

	@Nullable
	@Override
	public Icon getIcon() {
		return LatteIcons.FILE;
	}
}
