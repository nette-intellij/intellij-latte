package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.MergeFunction;
import com.intellij.lexer.MergingLexerAdapterBase;
import com.intellij.psi.tree.IElementType;
import com.jantvrdik.intellij.latte.psi.LatteTypes;

public class LatteSyntaxOffLexer extends MergingLexerAdapterBase {

	private MyMergeFunction mergeFunction = new MyMergeFunction();

	public LatteSyntaxOffLexer(Lexer original) {
		super(original);
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

		private boolean expectingSyntax = false;

		private boolean expectingTagClose = false;

		private boolean mergeText = false;

		@Override
		public IElementType merge(IElementType type, Lexer originalLexer) {
			if (type == LatteTypes.T_MACRO_CLASSIC && previousTokenText.equals("{syntax off}")) {
				mergeText = true;
				return type;
			}
			if (mergeText) {
				mergeText = false;
				int level = 1;
				while (true) {
					if (tagName == null) {
						//{syntax off} do nothing
					} else if (originalLexer.getState() == LatteTopLexer.HTML_OPEN_TAG_OPEN) {
						if (originalLexer.getTokenText().equals(tagName)) {
							level++;
						}
					} else if (originalLexer.getState() == LatteTopLexer.HTML_CLOSE_TAG_OPEN) {
						if (originalLexer.getTokenText().equals(tagName)) {
							level--;
						}
						if (level == 0) {
							return LatteTypes.T_TEXT;
						}
					}
					originalLexer.advance();
					if (originalLexer.getTokenType() == null) {
						return LatteTypes.T_TEXT;
					}
				}
			}

			if (originalLexer.getState() == LatteTopLexer.HTML_OPEN_TAG_OPEN) {
				tagName = originalLexer.getTokenText();
			}
			if (originalLexer.getTokenType() == LatteTypes.T_HTML_TAG_NATTR_NAME && originalLexer.getTokenText().equals("n:syntax")) {
				expectingSyntax = true;
			} else if (expectingSyntax && originalLexer.getTokenType() == LatteTypes.T_MACRO_CONTENT) {
				expectingTagClose = originalLexer.getTokenText().equals("off");
				expectingSyntax = false;
			}
			if (expectingTagClose
				&& (originalLexer.getState() == LatteTopLexer.HTML_TAG || originalLexer.getState() == LatteTopLexer.SCRIPT_TAG)
				&& originalLexer.getTokenText().equals(">")) {
				mergeText = true;
				expectingTagClose = false;
			}

			return type;
		}
	}
}
