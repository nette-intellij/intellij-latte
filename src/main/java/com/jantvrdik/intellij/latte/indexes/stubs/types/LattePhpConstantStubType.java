package com.jantvrdik.intellij.latte.indexes.stubs.types;

import com.intellij.lang.LighterAST;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.LighterASTTokenNode;
import com.intellij.psi.impl.source.tree.LightTreeUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.CharTable;
import com.jantvrdik.intellij.latte.indexes.extensions.LattePhpConstantIndex;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpConstantStub;
import com.jantvrdik.intellij.latte.indexes.stubs.impl.LattePhpConstantStubImpl;
import com.jantvrdik.intellij.latte.parser.LatteElementTypes;
import com.jantvrdik.intellij.latte.psi.LattePhpConstant;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.psi.impl.LattePhpConstantImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LattePhpConstantStubType extends LattePhpStubType<LattePhpConstantStub, LattePhpConstant> {
    public LattePhpConstantStubType(String debugName) {
        super(debugName, LatteElementTypes.LANG);
    }

    @Override
    public LattePhpConstant createPsi(@NotNull final LattePhpConstantStub stub) {
        return new LattePhpConstantImpl(stub, this);
    }

    @Override
    @NotNull
    public LattePhpConstantStub createStub(@NotNull final LattePhpConstant psi, final StubElement parentStub) {
        return new LattePhpConstantStubImpl(parentStub, psi.getConstantName());
    }

    @Override
    @NotNull
    public String getExternalId() {
        return "latte.phpConstant.prop";
    }

    @Override
    public void serialize(@NotNull final LattePhpConstantStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getConstantName());
    }

    @Override
    @NotNull
    public LattePhpConstantStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        String name = dataStream.readNameString();
        return new LattePhpConstantStubImpl(parentStub, name);
    }

    @Override
    public void indexStub(@NotNull final LattePhpConstantStub stub, @NotNull final IndexSink sink) {
        sink.occurrence(LattePhpConstantIndex.KEY, stub.getConstantName());
    }

    @NotNull
    @Override
    public LattePhpConstantStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        LighterASTNode keyNode = LightTreeUtil.firstChildOfType(tree, node, TokenSet.create(LatteTypes.T_PHP_IDENTIFIER));
        String key = intern(tree.getCharTable(), keyNode);
        return new LattePhpConstantStubImpl(parentStub, key);
    }

    public static String intern(@NotNull CharTable table, LighterASTNode node) {
        assert node instanceof LighterASTTokenNode : node;
        return table.intern(((LighterASTTokenNode) node).getText()).toString();
    }
}
