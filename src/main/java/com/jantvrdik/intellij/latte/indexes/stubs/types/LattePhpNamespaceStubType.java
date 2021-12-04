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
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpNamespaceStub;
import com.jantvrdik.intellij.latte.indexes.stubs.impl.LattePhpNamespaceStubImpl;
import com.jantvrdik.intellij.latte.parser.LatteElementTypes;
import com.jantvrdik.intellij.latte.psi.LattePhpNamespaceReference;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.psi.impl.LattePhpNamespaceReferenceImpl;
import com.jantvrdik.intellij.latte.php.LattePhpUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LattePhpNamespaceStubType extends LattePhpStubType<LattePhpNamespaceStub, LattePhpNamespaceReference> {
    public LattePhpNamespaceStubType(String debugName) {
        super(debugName, LatteElementTypes.LANG);
    }

    @Override
    public LattePhpNamespaceReference createPsi(@NotNull final LattePhpNamespaceStub stub) {
        return new LattePhpNamespaceReferenceImpl(stub, this);
    }

    @Override
    @NotNull
    public LattePhpNamespaceStub createStub(@NotNull final LattePhpNamespaceReference psi, final StubElement parentStub) {
        return new LattePhpNamespaceStubImpl(parentStub, psi.getNamespaceName());
    }

    @Override
    @NotNull
    public String getExternalId() {
        return "latte.phpNamespace.prop";
    }

    @Override
    public void serialize(@NotNull final LattePhpNamespaceStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getNamespaceName());
    }

    @Override
    @NotNull
    public LattePhpNamespaceStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        String name = dataStream.readNameString();
        return new LattePhpNamespaceStubImpl(parentStub, name);
    }

    @Override
    public void indexStub(@NotNull final LattePhpNamespaceStub stub, @NotNull final IndexSink sink) {
        sink.occurrence(LattePhpNamespaceIndex.KEY, stub.getNamespaceName());
    }

    @NotNull
    @Override
    public LattePhpNamespaceStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        LighterASTNode keyNode = LightTreeUtil.firstChildOfType(tree, node, TokenSet.create(LatteTypes.T_PHP_NAMESPACE_REFERENCE));
        String key = intern(tree.getCharTable(), keyNode);
        return new LattePhpNamespaceStubImpl(parentStub, LattePhpUtil.normalizeClassName(key));
    }

    public static String intern(@NotNull CharTable table, LighterASTNode node) {
        assert node instanceof LighterASTTokenNode : node;
        return table.intern(((LighterASTTokenNode) node).getText()).toString();
    }
}
