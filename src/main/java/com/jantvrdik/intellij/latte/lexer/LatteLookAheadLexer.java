package com.jantvrdik.intellij.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LookAheadLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.utils.LatteTagsUtil;
import com.jantvrdik.intellij.latte.utils.LatteTypesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lexer used for syntax highlighting
 *
 * It reuses the simple lexer, changing types of some tokens
 */
public class LatteLookAheadLexer extends LookAheadLexer {
	private static final TokenSet WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE, LatteTypes.T_WHITESPACE);
	private static final TokenSet TAG_TAGS = TokenSet.create(LatteTypes.T_HTML_TAG_ATTR_EQUAL_SIGN, LatteTypes.T_HTML_TAG_ATTR_DQ);

	private static final String IDENTIFIER_LINKS = "links";
	private static final String IDENTIFIER_TYPES = "types";

	private final Map<String, Boolean> lastValid = new HashMap<>();
	private final Map<String, Boolean> replaceAs = new HashMap<>();

	final private Lexer lexer;

	public LatteLookAheadLexer(Lexer baseLexer) {
		super(baseLexer, 1);
		lexer = baseLexer;
	}

	@Override
	protected void addToken(int endOffset, IElementType type) {
		boolean wasLinkDestination = false;
		if ((type == LatteTypes.T_PHP_IDENTIFIER || type == LatteTypes.T_PHP_KEYWORD || (type == LatteTypes.T_MACRO_ARGS && isCharacterAtCurrentPosition(lexer, '#', ':'))) && needReplaceAsMacro(IDENTIFIER_LINKS)) {
			type = LatteTypes.T_LINK_DESTINATION;
			wasLinkDestination = true;
		}

		boolean wasTypeDefinition = false;
		boolean isMacroSeparator = type == LatteTypes.T_PHP_MACRO_SEPARATOR;
		boolean isMacroFilters = type == LatteTypes.T_MACRO_FILTERS;
		if ((LatteTypesUtil.phpTypeTokens.contains(type) || isMacroSeparator || isMacroFilters) && needReplaceAsMacro(IDENTIFIER_TYPES)) {
			if (isMacroSeparator) {
				type = LatteTypes.T_PHP_OR_INCLUSIVE;
			} else if (isMacroFilters) {
				type = LatteTypes.T_PHP_IDENTIFIER;
			}
			wasTypeDefinition = true;
		}

		super.addToken(endOffset, type);
		if (!TAG_TAGS.contains(type)) {
			checkMacroType("links", type, LatteTagsUtil.LINK_TAGS_LIST, wasLinkDestination);
			checkMacroType("types", type, LatteTagsUtil.TYPE_TAGS_LIST, wasTypeDefinition);
		}
	}

	private boolean needReplaceAsMacro(@NotNull String identifier) {
		return replaceAs.getOrDefault(identifier, false);
	}

	private void checkMacroType(@NotNull String identifier, IElementType type, @NotNull List<String> types, boolean currentValid) {
		boolean current = (type == LatteTypes.T_MACRO_NAME || type == LatteTypes.T_HTML_TAG_NATTR_NAME) && isMacroTypeMacro(lexer, types);
		replaceAs.put(identifier, (currentValid && !WHITESPACES.contains(type))
				|| (!currentValid && lastValid.containsKey(identifier) && lastValid.getOrDefault(identifier, false) && WHITESPACES.contains(type))
				|| current);
		lastValid.put(identifier, current);
	}

	public static boolean isCharacterAtCurrentPosition(Lexer baseLexer, char ...characters) {
		char current = baseLexer.getBufferSequence().charAt(baseLexer.getCurrentPosition().getOffset());
		for (char character : characters) {
			if (current == character) {
				return true;
			}
		}
		return false;
	}

	private static boolean isMacroTypeMacro(Lexer baseLexer, @NotNull List<String> types) {
		CharSequence tagName = baseLexer.getBufferSequence().subSequence(baseLexer.getTokenStart(), baseLexer.getTokenEnd());
		if (tagName.length() == 0) {
			return false;
		}
		return types.contains(tagName.toString());
	}
}
