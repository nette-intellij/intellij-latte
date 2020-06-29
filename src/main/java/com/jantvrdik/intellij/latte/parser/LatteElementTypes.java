package com.jantvrdik.intellij.latte.parser;

import com.intellij.lang.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.ILightStubFileElementType;
import com.intellij.util.diff.FlyweightCapableTreeStructure;
import com.jantvrdik.intellij.latte.LatteLanguage;
import com.jantvrdik.intellij.latte.indexes.stubs.LatteFileStub;

public class LatteElementTypes {
    public static LatteLanguage LANG = LatteLanguage.INSTANCE;

    public static final ILightStubFileElementType<LatteFileStub> FILE = new ILightStubFileElementType<LatteFileStub>(LANG)  {
        @Override
        public FlyweightCapableTreeStructure<LighterASTNode> parseContentsLight(ASTNode chameleon) {
            PsiElement psi = chameleon.getPsi();
            assert psi != null : "Bad chameleon: " + chameleon;

            Project project = psi.getProject();
            PsiBuilderFactory factory = PsiBuilderFactory.getInstance();
            PsiBuilder builder = factory.createBuilder(project, chameleon);
            ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(getLanguage());
            assert parserDefinition != null : this;

            LatteParser parser = new LatteParser();
            parser.parseLight(this, builder);
            return builder.getLightTree();
        }
    };

}