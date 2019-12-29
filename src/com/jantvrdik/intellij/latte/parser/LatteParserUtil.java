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
	public static boolean checkPairMacro(PsiBuilder builder, int level, String pairValue) {
		if (builder.getTokenType() != T_MACRO_OPEN_TAG_OPEN) return false;

		PsiBuilder.Marker marker = builder.mark();
		String macroName = getMacroName(builder);

		boolean pair = pairValue.equals("pair");
		boolean result;

		LatteMacro macro = LatteConfiguration.INSTANCE.getMacro(builder.getProject(), macroName);
		if (macro != null && macro.type == LatteMacro.Type.AUTO_EMPTY) {
			result = pair == isPair(macroName, builder);
		} else if (macroName.equals("_")) {
			// hard coded rule for macro _ because of dg's poor design decision
			// macro _ is pair only if it has empty arguments, otherwise it is unpaired
			// see https://github.com/nette/nette/blob/v2.1.2/Nette/Latte/Macros/CoreMacros.php#L193
			boolean emptyArgs = true;
			builder.advanceLexer();
			while (emptyArgs && nextTokenIsFast(builder, T_MACRO_ARGS, T_MACRO_ARGS_NUMBER, T_MACRO_ARGS_STRING, T_MACRO_ARGS_VAR)) {
				emptyArgs = (builder.getTokenText().trim().length() == 0);
				builder.advanceLexer();
			}
			result = (emptyArgs == pair);

		// all other macros which respect rules
		} else {
			result = (macro != null ? (macro.type == (pair ? LatteMacro.Type.PAIR : LatteMacro.Type.UNPAIRED)) : !pair);
		}

		marker.rollbackTo();
		return result;
	}

	/**
	 * Looks for a classic macro a returns true if it finds the macro a and it is pair or unpaired (based on pair parameter).
	 */
	public static boolean checkPhpMethod(PsiBuilder builder, int level) {
		IElementType type = builder.getTokenType();
		String text = builder.getTokenText();
		if (builder.getTokenType() != T_PHP_DOUBLE_COLON && builder.getTokenType() != T_PHP_DOUBLE_ARROW) return false;

		int depth = 0;
		PsiBuilder.Marker marker = builder.mark();

		boolean result = isPhpMethod(builder);

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
				return false;
			}
			builder.advanceLexer();
			type = builder.getTokenType();
		}
		return false;
	}

	private static boolean isPhpConstant(PsiBuilder builder)
	{
		builder.advanceLexer();
		IElementType type = builder.getTokenType();
		while (type != null) {
			/*if (type == T_MACRO_TAG_CLOSE_EMPTY) {
				return true;
			} else */if (type == T_MACRO_TAG_CLOSE) {
				break;
			}
			builder.advanceLexer();
			type = builder.getTokenType();
		}
		return false;
	}

	private static boolean isPhpMethod(PsiBuilder builder)
	{
		builder.advanceLexer();
		IElementType type = builder.getTokenType();
		while (type != null) {
			/*if (type == T_MACRO_TAG_CLOSE_EMPTY) {
				return true;
			} else */if (type == T_MACRO_TAG_CLOSE) {
				break;
			}
			builder.advanceLexer();
			type = builder.getTokenType();
		}
		return false;
	}

}
