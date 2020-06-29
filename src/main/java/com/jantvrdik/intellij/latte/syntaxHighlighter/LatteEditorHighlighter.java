package com.jantvrdik.intellij.latte.syntaxHighlighter;


import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.tree.IElementType;
import com.jantvrdik.intellij.latte.lexer.LatteHtmlHighlightingLexer;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatteEditorHighlighter extends LayeredLexerEditorHighlighter {
	public LatteEditorHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile, @NotNull EditorColorsScheme colors) {
		super(new LatteSyntaxHighlighter() {
			@Override
			public @NotNull Lexer getHighlightingLexer() {
				return new LatteHtmlHighlightingLexer(super.getHighlightingLexer());
			}
		}, colors);

		final SyntaxHighlighter highlighter = getHighlighter(project, virtualFile);
		this.registerLayer(LatteTypes.T_TEXT, new LayerDescriptor(new SyntaxHighlighter() {
			@NotNull
			public Lexer getHighlightingLexer() {
				return highlighter.getHighlightingLexer();
			}

			@NotNull
			public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
				return highlighter.getTokenHighlights(tokenType);
			}
		}, ""));
	}

	@NotNull
	private static SyntaxHighlighter getHighlighter(Project project, VirtualFile virtualFile) {
		LanguageFileType fileType = HtmlFileType.INSTANCE;
		SyntaxHighlighter highlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(fileType, project, virtualFile);
		assert highlighter != null;

		return highlighter;
	}
}
