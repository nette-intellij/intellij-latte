package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.lexer.MergeFunction;
import com.intellij.lexer.MergingLexerAdapterBase;
import com.intellij.psi.tree.IElementType;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.NotNull;

public class LatteSyntaxLexer extends MergingLexerAdapterBase {

	private MyMergeFunction mergeFunction = new MyMergeFunction();

	public LatteSyntaxLexer(Lexer original) {
		super(original);
	}

	@Override
	public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
		((LatteTopLexer)((FlexAdapter) getDelegate()).getFlex()).syntax = 0;
		super.start(buffer, startOffset, endOffset, initialState);
	}

	@Override
	public int getTokenEnd() {
		this.mergeFunction.previousTokenText = getDelegate().getTokenText();
		return super.getTokenEnd();
	}

	@Override
	public IElementType getTokenType() {
		this.mergeFunction.previousTokenText = getDelegate().getTokenText();
		return super.getTokenType();
	}

	@Override
	public int getTokenStart() {
		this.mergeFunction.previousTokenText = getDelegate().getTokenText();
		return super.getTokenStart();
	}

	@Override
	public int getState() {
		this.mergeFunction.previousTokenText = getDelegate().getTokenText();
		return super.getState();
	}

	@Override
	public MergeFunction getMergeFunction() {
		return mergeFunction;
	}

	private static class MyMergeFunction implements MergeFunction {

		public String previousTokenText;

		private String tagName;

		private int tagLevel = 0;

		private boolean expectingSyntax = false;

		@Override
		public IElementType merge(IElementType type, Lexer originalLexer) {
			LatteTopLexer flex = (LatteTopLexer) ((FlexAdapter) originalLexer).getFlex();
			if (flex.syntax == 0) {
				if (type == LatteTypes.T_MACRO_CLASSIC && previousTokenText.equals("{syntax off}")) {
					flex.syntax = 2;
					return type;
				} else if (type == LatteTypes.T_MACRO_CLASSIC && previousTokenText.equals("{syntax double}")) {
					flex.syntax = 1;
					return type;
				}
			} else if (flex.syntax == 1) {
				if (type == LatteTypes.T_MACRO_CLASSIC_DOUBLE && previousTokenText.equals("{{syntax off}}")) {
					flex.syntax = 2;
					return type;
				} else if (type == LatteTypes.T_MACRO_CLASSIC_DOUBLE && previousTokenText.equals("{{syntax latte}}")) {
					flex.syntax = 0;
					return type;
				} else if (type == LatteTypes.T_MACRO_CLASSIC_DOUBLE && previousTokenText.equals("{{/syntax}}")) {
					flex.syntax = 0;
					return type;
				}
			}
			if (tagLevel > 0 && tagName != null) {
				if (originalLexer.getState() == LatteTopLexer.HTML_OPEN_TAG_OPEN) {
					if (originalLexer.getTokenText().equals(tagName)) {
						tagLevel++;
					}
				} else if (originalLexer.getState() == LatteTopLexer.HTML_CLOSE_TAG_OPEN) {
					if (originalLexer.getTokenText().equals(tagName)) {
						tagLevel--;
					}
					if (tagLevel == 0) {
						flex.syntax = 0;
					}
				}
			}

			if (originalLexer.getState() == LatteTopLexer.HTML_OPEN_TAG_OPEN) {
				tagName = originalLexer.getTokenText();
			}
			if (originalLexer.getTokenType() == LatteTypes.T_HTML_TAG_NATTR_NAME && originalLexer.getTokenText().equals("n:syntax")) {
				expectingSyntax = true;
			} else if (expectingSyntax && originalLexer.getTokenType() == LatteTypes.T_MACRO_CONTENT) {
				String tokenText = originalLexer.getTokenText();
				flex.syntax = tokenText.equals("off") ? 2 : tokenText.equals("double") ? 1 : 0;
				expectingSyntax = false;
				tagLevel = 1;
			}

			return type;
		}

	}
}
