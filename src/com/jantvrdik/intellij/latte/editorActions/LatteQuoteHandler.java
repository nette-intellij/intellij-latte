package com.jantvrdik.intellij.latte.editorActions;

import com.intellij.codeInsight.editorActions.QuoteHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.jantvrdik.intellij.latte.psi.LatteTypes;


public class LatteQuoteHandler implements QuoteHandler {

	@Override
	public boolean isClosingQuote(HighlighterIterator iterator, int offset) {
		return iterator.getTokenType() == LatteTypes.T_MACRO_ARGS_DOUBLE_QUOTE_RIGHT || iterator.getTokenType() == LatteTypes.T_MACRO_ARGS_SINGLE_QUOTE_RIGHT;
	}

	@Override
	public boolean isOpeningQuote(HighlighterIterator iterator, int offset) {
		return iterator.getTokenType() == LatteTypes.T_MACRO_ARGS_DOUBLE_QUOTE_LEFT || iterator.getTokenType() == LatteTypes.T_MACRO_ARGS_SINGLE_QUOTE_LEFT;
	}

	@Override
	public boolean hasNonClosedLiteral(Editor editor, HighlighterIterator iterator, int offset) {
		return true;
	}

	@Override
	public boolean isInsideLiteral(HighlighterIterator iterator) {
		return false;
	}
}
