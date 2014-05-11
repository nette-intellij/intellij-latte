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
		registerLayer(new LatteMacroLexerAdapter(), LatteTypes.T_MACRO_CLASSIC);
	}
}
