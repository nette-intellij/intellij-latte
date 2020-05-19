package com.jantvrdik.intellij.latte.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteMacro;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.NotNull;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

/**
 * External rules for LatteParser.
 */
public class LatteParserUtil extends GeneratedParserUtilBase {

	/**
	 * Looks for a classic macro a returns true if it finds the macro a and it is pair or unpaired (based on pair parameter).
	 */
	public static boolean checkPairMacro(PsiBuilder builder, int level, Parser parser) {
		boolean pair = parser == LatteParser.TRUE_parser_;
		if (builder.getTokenType() != T_MACRO_OPEN_TAG_OPEN) return false;

		PsiBuilder.Marker marker = builder.mark();
		String macroName = getMacroName(builder);

		boolean result;

		LatteMacro macro = LatteConfiguration.INSTANCE.getMacro(builder.getProject(), macroName);
		if (macro != null && macro.type == LatteMacro.Type.AUTO_EMPTY) {
			result = pair == isAutoEmptyPair(macroName, builder);
		} else if (macroName.equals("_")) {
			// hard coded rule for macro _ because of dg's poor design decision
			// macro _ is pair only if it has empty arguments, otherwise it is unpaired
			// see https://github.com/nette/nette/blob/v2.1.2/Nette/Latte/Macros/CoreMacros.php#L193
			builder.advanceLexer();
			result = ((builder.getTokenType() == T_MACRO_TAG_CLOSE) == pair);

			// all other macros which respect rules
		} else {
			result = (macro != null ? (macro.type == (pair ? LatteMacro.Type.PAIR : LatteMacro.Type.UNPAIRED)) : !pair);
		}

		marker.rollbackTo();
		return result;
	}

	public static boolean checkEmptyMacro(PsiBuilder builder, int level)
	{
		PsiBuilder.Marker marker = builder.mark();
		boolean result = false;
		while (true) {
			IElementType token = builder.getTokenType();
			if (token == null) {
				break;
			} else if (token == LatteTypes.T_MACRO_TAG_CLOSE) {
				break;
			} else if (token == LatteTypes.T_MACRO_TAG_CLOSE_EMPTY) {
				result = true;
				break;
			}
			builder.advanceLexer();
		}
		marker.rollbackTo();

		return result;
	}

	private static boolean isClosingTagExpected(PsiBuilder builder, String macroName)
	{
		IElementType type = builder.getTokenType();
		Builder.Marker marker = builder.mark();
		if (type == T_MACRO_CLOSE_TAG_OPEN || isEmptyPair(builder)) {
			return false;
		}
		marker.rollbackTo();
		LatteMacro macro = LatteConfiguration.INSTANCE.getMacro(builder.getProject(), macroName);

		if (macro != null && macro.type == LatteMacro.Type.AUTO_EMPTY) {
			return isAutoEmptyPair(macroName, builder);
		}
		if (macroName.equals("_")) {
			builder.advanceLexer();
			return builder.getTokenType() == T_MACRO_TAG_CLOSE;
		}
		return macro != null && macro.type == LatteMacro.Type.PAIR;
	}

	@NotNull
	private static String getMacroName(PsiBuilder builder) {
		String macroName;

		consumeTokenFast(builder, T_MACRO_OPEN_TAG_OPEN);
		consumeTokenFast(builder, T_MACRO_CLOSE_TAG_OPEN);
		consumeTokenFast(builder, T_MACRO_NOESCAPE);
		if (nextTokenIsFast(builder, T_MACRO_NAME, T_MACRO_SHORTNAME)) {
			macroName = builder.getTokenText();
			assert macroName != null;

		} else {
			macroName = "=";
		}
		return macroName;
	}

	private static boolean isAutoEmptyPair(String macroName, PsiBuilder builder)
	{
		builder.advanceLexer();
		if (isEmptyPair(builder)) {
			return true;
		}
		int pairMacrosLevel = 0;
		IElementType type = builder.getTokenType();
		while (type != null) {
			if(type == T_HTML_TAG_NATTR_NAME && ("n:" + macroName).equals(builder.getTokenText())) {
				return false;
			}
			if (nextTokenIsFast(builder, T_MACRO_CLOSE_TAG_OPEN, T_MACRO_OPEN_TAG_OPEN)) {
				boolean closing = type == T_MACRO_CLOSE_TAG_OPEN;
				String macroName2 = getMacroName(builder);
				if (macroName2.equals(macroName)) {
					return closing;
				} else if (closing) {
					if (pairMacrosLevel == 0) {
						return true;
					} else {
						pairMacrosLevel--;
					}
				} else if (isClosingTagExpected(builder, macroName2)) {
					pairMacrosLevel++;
				}
			}
			builder.advanceLexer();
			type = builder.getTokenType();
		}
		return false;
	}

	private static boolean isEmptyPair(PsiBuilder builder)
	{
		IElementType type = builder.getTokenType();
		while (type != null) {
			if (type == T_MACRO_TAG_CLOSE_EMPTY) {
				return true;
			} else if (type == T_MACRO_TAG_CLOSE) {
				return false;
			}
			builder.advanceLexer();
			type = builder.getTokenType();
		}
		return false;
	}

	public static boolean isNamespace(PsiBuilder builder, int level) {
		PsiBuilder.Marker marker = builder.mark();

		boolean result = false;

		IElementType type = builder.getTokenType();
		IElementType nextToken = builder.lookAhead(1);
		if (type == LatteTypes.T_PHP_NAMESPACE_REFERENCE && nextToken == LatteTypes.T_PHP_NAMESPACE_RESOLUTION) {
			result = true;
		}

		marker.rollbackTo();

		return result;
	}

}
