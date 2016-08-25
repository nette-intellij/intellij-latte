package com.jantvrdik.intellij.latte.syntaxHighlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.XmlHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.jantvrdik.intellij.latte.lexer.LatteLexer;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class LatteSyntaxHighlighter extends SyntaxHighlighterBase {

	public static final TextAttributesKey HTML_NATTR_NAME = createTextAttributesKey("LATTE_HTML_NATTR_NAME", DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey MACRO_DELIMITERS = createTextAttributesKey("LATTE_MACRO_DELIMITERS", DefaultLanguageHighlighterColors.BRACKETS);
	public static final TextAttributesKey MACRO_NAME = createTextAttributesKey("LATTE_MACRO_NAME", DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey MACRO_ARGS_VAR = createTextAttributesKey("LATTE_MACRO_ARGS_VAR", DefaultLanguageHighlighterColors.LOCAL_VARIABLE);
	public static final TextAttributesKey MACRO_ARGS_STRING = createTextAttributesKey("LATTE_MACRO_ARGS_STRING", DefaultLanguageHighlighterColors.STRING);
	public static final TextAttributesKey MACRO_ARGS_NUMBER = createTextAttributesKey("LATTE_MACRO_ARGS_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
	public static final TextAttributesKey MACRO_MODIFIERS = createTextAttributesKey("LATTE_MACRO_MODIFIERS", HighlighterColors.TEXT);
	public static final TextAttributesKey MACRO_COMMENT = createTextAttributesKey("LATTE_MACRO_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);


	@NotNull
	@Override
	public Lexer getHighlightingLexer() {
		return new LatteLexer();
	}

	@NotNull
	@Override
	public TextAttributesKey[] getTokenHighlights(IElementType token) {
		if (token == LatteTypes.T_HTML_TAG_NATTR_NAME) {
			return pack(HTML_NATTR_NAME);

		} else if (token == LatteTypes.T_MACRO_OPEN_TAG_OPEN || token == LatteTypes.T_MACRO_CLOSE_TAG_OPEN || token == LatteTypes.T_MACRO_TAG_CLOSE || token == LatteTypes.T_MACRO_TAG_CLOSE_EMPTY) {
			return pack(MACRO_DELIMITERS);

		} else if (token == LatteTypes.T_MACRO_NAME || token == LatteTypes.T_MACRO_SHORTNAME || token == LatteTypes.T_MACRO_NOESCAPE) {
			return pack(MACRO_NAME);

		} else if (token == LatteTypes.T_MACRO_ARGS_VAR) {
			return pack(MACRO_ARGS_VAR);

		} else if (token == LatteTypes.T_MACRO_ARGS_STRING) {
			return pack(MACRO_ARGS_STRING);

		} else if (token == LatteTypes.T_MACRO_ARGS_NUMBER) {
			return pack(MACRO_ARGS_NUMBER);

		} else if (token == LatteTypes.T_MACRO_ARGS_MODIFIERS) {
			return pack(MACRO_MODIFIERS);

		} else if (token == LatteTypes.T_MACRO_COMMENT) {
			return pack(MACRO_COMMENT);

		} else {
			return EMPTY;
		}
	}
}
