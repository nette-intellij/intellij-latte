package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

public class LatteMacroContentLexerAdapter extends MergingLexerAdapter {

	public LatteMacroContentLexerAdapter() {
		super(
				new FlexAdapter(new LatteMacroContentLexer(null)),
				TokenSet.create(LatteTypes.T_MACRO_ARGS_STRING, LatteTypes.T_MACRO_ARGS_OTHER)
		);
	}
}
