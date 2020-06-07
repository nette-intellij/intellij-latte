package com.jantvrdik.intellij.latte.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.jantvrdik.intellij.latte.LatteLanguage;
import com.jantvrdik.intellij.latte.lexer.LatteHighlightingLexer;
import com.jantvrdik.intellij.latte.lexer.LatteLexer;
import com.jantvrdik.intellij.latte.lexer.LatteLookAheadLexer;
import com.jantvrdik.intellij.latte.psi.LatteFile;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import org.jetbrains.annotations.NotNull;

public class LatteParserDefinition implements ParserDefinition {
	public static final TokenSet WHITE_SPACES = TokenSet.create(LatteTypes.T_WHITESPACE, TokenType.WHITE_SPACE);
	public static final TokenSet COMMENTS = TokenSet.create(LatteTypes.T_MACRO_COMMENT);
	public static final TokenSet STRINGS = TokenSet.create(LatteTypes.T_MACRO_ARGS_STRING);

	public static final IFileElementType FILE = new IFileElementType(LatteLanguage.INSTANCE);

	@NotNull
	@Override
	public Lexer createLexer(Project project) {
		return new LatteLookAheadLexer(new LatteLexer());
	}

	@Override
	public PsiParser createParser(Project project) {
		return new LatteParser();
	}

	@Override
	public IFileElementType getFileNodeType() {
		return FILE;
	}

	@NotNull
	@Override
	public TokenSet getWhitespaceTokens() {
		return WHITE_SPACES;
	}

	@NotNull
	@Override
	public TokenSet getCommentTokens() {
		return COMMENTS;
	}

	@NotNull
	@Override
	public TokenSet getStringLiteralElements() {
		return STRINGS;
	}

	@NotNull
	@Override
	public PsiElement createElement(ASTNode node) {
		return LatteTypes.Factory.createElement(node);
	}

	@Override
	public PsiFile createFile(FileViewProvider viewProvider) {
		return new LatteFile(viewProvider);
	}

	@Override
	public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
		return SpaceRequirements.MAY;
	}
}
