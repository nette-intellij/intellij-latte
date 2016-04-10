package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.LayeredLexer;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

/**
 * Main Latte lexer which combines "top lexer" and "macro lexer".
 *
 * LatteMacroLexerAdapter is used to further process token T_MACRO_CLASSIC.
 */
public class LatteLexer extends LayeredLexer {
	public LatteLexer() {
		super(new LatteTopLexerAdapter());
		LayeredLexer macroLexer = new LayeredLexer(new LatteMacroLexerAdapter());
		macroLexer.registerLayer(new LatteMacroContentLexerAdapter(), LatteTypes.T_MACRO_CONTENT);
		registerLayer(macroLexer, LatteTypes.T_MACRO_CLASSIC);
		LayeredLexer macroDoubleLexer = new LayeredLexer(new LatteMacroDoubleLexerAdapter());
		macroDoubleLexer.registerLayer(new LatteMacroContentLexerAdapter(), LatteTypes.T_MACRO_CONTENT);
		registerLayer(macroDoubleLexer, LatteTypes.T_MACRO_CLASSIC_DOUBLE);
		registerLayer(new LatteMacroContentLexerAdapter(), LatteTypes.T_MACRO_CONTENT);
	}
}
