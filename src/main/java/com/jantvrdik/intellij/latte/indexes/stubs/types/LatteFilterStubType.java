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
import com.jantvrdik.intellij.latte.indexes.extensions.LattePhpNamespaceIndex;
import com.jantvrdik.intellij.latte.indexes.stubs.LatteFilterStub;
import com.jantvrdik.intellij.latte.indexes.stubs.impl.LatteFilterStubImpl;
import com.jantvrdik.intellij.latte.parser.LatteElementTypes;
import com.jantvrdik.intellij.latte.psi.LatteMacroModifier;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.psi.impl.LatteMacroModifierImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LatteFilterStubType extends LattePhpStubType<LatteFilterStub, LatteMacroModifier> {
    public LatteFilterStubType(String debugName) {
        super(debugName, LatteElementTypes.LANG);
    }

    @Override
    public LatteMacroModifier createPsi(@NotNull final LatteFilterStub stub) {
        return new LatteMacroModifierImpl(stub, this);
    }

    @Override
    @NotNull
    public LatteFilterStub createStub(@NotNull final LatteMacroModifier psi, final StubElement parentStub) {
        return new LatteFilterStubImpl(parentStub, psi.getModifierName());
    }

    @Override
    @NotNull
    public String getExternalId() {
        return "latte.filter.prop";
    }

    @Override
    public void serialize(@NotNull final LatteFilterStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getModifierName());
    }

    @Override
    @NotNull
    public LatteFilterStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        String name = dataStream.readNameString();
        return new LatteFilterStubImpl(parentStub, name);
    }

    @Override
    public void indexStub(@NotNull final LatteFilterStub stub, @NotNull final IndexSink sink) {
        sink.occurrence(LattePhpNamespaceIndex.KEY, stub.getModifierName());
    }

    @NotNull
    @Override
    public LatteFilterStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        LighterASTNode keyNode = LightTreeUtil.firstChildOfType(tree, node, TokenSet.create(LatteTypes.T_MACRO_FILTERS));
        String key = intern(tree.getCharTable(), keyNode);
        return new LatteFilterStubImpl(parentStub, key);
    }

    public static String intern(@NotNull CharTable table, LighterASTNode node) {
        assert node instanceof LighterASTTokenNode : node;
        return table.intern(((LighterASTTokenNode) node).getText()).toString();
    }
}
