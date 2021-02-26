package com.jantvrdik.intellij.latte.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.settings.LatteTagSettings;
import com.jantvrdik.intellij.latte.utils.LatteHtmlUtil;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

/**
 * External rules for LatteParser.
 */
public class LatteParserUtil extends GeneratedParserUtilBase {
	/**
	 * Looks for a classic macro a returns true if it finds the macro a and it is pair or unpaired (based on pair parameter).
	 */
	public static boolean checkPairMacro(PsiBuilder builder, int level, Parser parser) {
		if (builder.getTokenType() != T_MACRO_OPEN_TAG_OPEN) return false;

		PsiBuilder.Marker marker = builder.mark();
		String macroName = getMacroName(builder);

		boolean pair = parser == LatteParser.TRUE_parser_;
		boolean result;

		LatteTagSettings tag = getTag(builder);
		if (tag == null || tag.getType() == LatteTagSettings.Type.AUTO_EMPTY) {
			result = pair == isPair(macroName, builder);
		} else if (macroName.equals("_")) {
			// hard coded rule for macro _ because of dg's poor design decision
			// macro _ is pair only if it has empty arguments, otherwise it is unpaired
			// see https://github.com/nette/nette/blob/v2.1.2/Nette/Latte/Macros/CoreMacros.php#L193
			boolean emptyArgs = true;
			builder.advanceLexer();
			while (emptyArgs && nextTokenIsFast(builder, T_MACRO_ARGS, T_MACRO_ARGS_NUMBER, T_MACRO_ARGS_STRING, T_MACRO_ARGS_VAR, T_PHP_METHOD)) {
				emptyArgs = (builder.getTokenText().trim().length() == 0);
				builder.advanceLexer();
			}
			result = (emptyArgs == pair);

			// all other macros which respect rules
		} else {
			result = (tag != null ? (pair ? (LatteTagSettings.Type.PAIR == tag.getType()) : LatteTagSettings.Type.unpairedSet.contains(tag.getType())) : !pair);
		}

		marker.rollbackTo();
		return result;
	}

	public static boolean checkPairHtmlTag(PsiBuilder builder, int level, Parser parser) {
		boolean pair = parser == LatteParser.TRUE_parser_;
		if (builder.getTokenType() != T_HTML_OPEN_TAG_OPEN) return false;

		PsiBuilder.Marker marker = builder.mark();
		String tagName = getHtmlTagName(builder);

		boolean isVoidTag = LatteHtmlUtil.isVoidTag(tagName);
		boolean result = (!isVoidTag && pair) || (isVoidTag && !pair);

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

	@NotNull
	private static String getHtmlTagName(PsiBuilder builder) {
		String macroName = "?";

		consumeTokenFast(builder, T_HTML_OPEN_TAG_OPEN);
		if (nextTokenIsFast(builder, T_TEXT)) {
			macroName = builder.getTokenText();
			if (macroName != null && macroName.length() > 0) {
				macroName = macroName.split(" ")[0];

			} else {
				macroName = "?";
			}
		}
		return macroName;
	}

	private static boolean isPair(String macroName, PsiBuilder builder)
	{
		builder.advanceLexer();
		IElementType type = builder.getTokenType();
		while (type != null) {
			if (type == T_MACRO_TAG_CLOSE_EMPTY) {
				return true;
			} else if (type == T_MACRO_TAG_CLOSE) {
				break;
			}
			builder.advanceLexer();
			type = builder.getTokenType();
		}
		while (type != null) {
			if (nextTokenIsFast(builder, T_MACRO_CLOSE_TAG_OPEN, T_MACRO_OPEN_TAG_OPEN) && getMacroName(builder).equals(macroName)) {
				return type == T_MACRO_CLOSE_TAG_OPEN;
			} else if(type == T_HTML_TAG_NATTR_NAME && ("n:" + macroName).equals(builder.getTokenText())) {
				builder.advanceLexer();
				type = builder.getTokenType();
				continue;
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

	private static LatteTagSettings getTag(PsiBuilder builder) {
		return LatteConfiguration.getInstance(builder.getProject()).getTag(getMacroName(builder));
	}

}
