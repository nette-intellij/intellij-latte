package com.jantvrdik.intellij.latte;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.EditorHighlighterProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeEditorHighlighterProviders;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import com.jantvrdik.intellij.latte.syntaxHighlighter.LatteEditorHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;

public class LatteFileType extends LanguageFileType {
	public static final LatteFileType INSTANCE = new LatteFileType();

	private LatteFileType() {
		super(LatteLanguage.INSTANCE);

		FileTypeEditorHighlighterProviders.INSTANCE.addExplicitExtension(this, new EditorHighlighterProvider() {
			public EditorHighlighter getEditorHighlighter(@Nullable Project project, @NotNull FileType fileType, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors) {
				return new LatteEditorHighlighter(project, virtualFile,colors);
			}
		});
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
