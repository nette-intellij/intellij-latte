package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

public class LatteMacroDoubleLexerAdapter extends MergingLexerAdapter {

	public LatteMacroDoubleLexerAdapter() {
		super(new FlexAdapter(new LatteMacroDoubleLexer((java.io.Reader) null)),
			TokenSet.create(LatteTypes.T_MACRO_CONTENT));
	}
}
