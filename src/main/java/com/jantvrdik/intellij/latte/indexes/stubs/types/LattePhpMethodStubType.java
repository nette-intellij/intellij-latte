package com.jantvrdik.intellij.latte.indexes.stubs.types;

import com.intellij.lang.LighterAST;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.LighterASTTokenNode;
import com.intellij.psi.impl.source.tree.LightTreeUtil;
import com.intellij.psi.stubs.*;
import com.intellij.util.CharTable;
import com.jantvrdik.intellij.latte.indexes.extensions.LattePhpMethodIndex;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpMethodStub;
import com.jantvrdik.intellij.latte.indexes.stubs.impl.LattePhpMethodStubImpl;
import com.jantvrdik.intellij.latte.parser.LatteElementTypes;
import com.jantvrdik.intellij.latte.psi.LattePhpMethod;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.psi.impl.LattePhpMethodImpl;
import com.jantvrdik.intellij.latte.utils.LatteTypesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LattePhpMethodStubType extends LattePhpStubType<LattePhpMethodStub, LattePhpMethod> {
    public LattePhpMethodStubType(String debugName) {
        super(debugName, LatteElementTypes.LANG);
    }

    @Override
    public LattePhpMethod createPsi(@NotNull final LattePhpMethodStub stub) {
        return new LattePhpMethodImpl(stub, this);
    }

    @Override
    @NotNull
    public LattePhpMethodStub createStub(@NotNull final LattePhpMethod psi, final StubElement parentStub) {
        return new LattePhpMethodStubImpl(parentStub, psi.getMethodName());
    }

    @Override
    @NotNull
    public String getExternalId() {
        return "latte.phpMethod.prop";
    }

    @Override
    public void serialize(@NotNull final LattePhpMethodStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getMethodName());
        //writePhpType(dataStream, stub.getPhpType());
    }

    @Override
    @NotNull
    public LattePhpMethodStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        String name = dataStream.readNameString();
        //LattePhpType type = readPhpType(dataStream);
        return new LattePhpMethodStubImpl(parentStub, name);
    }

    @Override
    public void indexStub(@NotNull final LattePhpMethodStub stub, @NotNull final IndexSink sink) {
        sink.occurrence(LattePhpMethodIndex.KEY, stub.getMethodName());
    }
/*
    @NotNull
    @Override
    public LattePhpMethodStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        LighterASTNode keyNode = LightTreeUtil.firstChildOfType(tree, node, TokenSet.create(LatteTypes.T_PHP_IDENTIFIER));
        String key = intern(tree.getCharTable(), keyNode);

        LighterASTNode statementPart = LightTreeUtil.getParentOfType(tree, node, TokenSet.create(LatteTypes.PHP_STATEMENT_PART), TokenSet.create(LatteTypes.PHP_CONTENT));
        if (statementPart == null) {
            phpType = LattePhpType.MIXED;
        }
        statementPart = part.getPrevPhpStatementPart();
        if (part == null || part.getPhpElement() == null) {
            return LattePhpType.MIXED;
        }
        return part.getPhpType().withDepth(part.getPhpElement().getPhpArrayLevel());

        return new LattePhpMethodStubImpl(parentStub, key);
    }
*/

    @NotNull
    @Override
    public LattePhpMethodStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        LighterASTNode keyNode = LightTreeUtil.firstChildOfType(tree, node, LatteTypesUtil.methodTokens);
        if (keyNode != null && keyNode.getTokenType() == LatteTypes.PHP_VARIABLE) {
            keyNode = LightTreeUtil.firstChildOfType(tree, keyNode, LatteTypes.T_MACRO_ARGS_VAR);
        }
        String key = intern(tree.getCharTable(), keyNode);
        return new LattePhpMethodStubImpl(parentStub, key);
    }

    public static String intern(@NotNull CharTable table, LighterASTNode node) {
        assert node instanceof LighterASTTokenNode : node;
        return table.intern(((LighterASTTokenNode) node).getText()).toString();
    }
}
