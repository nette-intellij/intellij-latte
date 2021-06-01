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
import com.jantvrdik.intellij.latte.indexes.extensions.LattePhpClassIndex;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpClassStub;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpTypeStub;
import com.jantvrdik.intellij.latte.indexes.stubs.impl.LattePhpClassStubImpl;
import com.jantvrdik.intellij.latte.parser.LatteElementTypes;
import com.jantvrdik.intellij.latte.psi.LattePhpClassReference;
import com.jantvrdik.intellij.latte.psi.impl.LattePhpClassReferenceImpl;
import com.jantvrdik.intellij.latte.php.LattePhpUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static com.jantvrdik.intellij.latte.psi.LatteTypes.*;

public class LattePhpClassStubType extends LattePhpTypeStub<LattePhpClassStub, LattePhpClassReference> {
    public LattePhpClassStubType(String debugName) {
        super(debugName, LatteElementTypes.LANG);
    }

    @Override
    public LattePhpClassReference createPsi(@NotNull final LattePhpClassStub stub) {
        return new LattePhpClassReferenceImpl(stub, this);
    }

    @Override
    @NotNull
    public LattePhpClassStub createStub(@NotNull final LattePhpClassReference psi, final StubElement parentStub) {
        return new LattePhpClassStubImpl(parentStub, psi.getClassName());
    }

    @Override
    @NotNull
    public String getExternalId() {
        return "latte.phpClass.name";
    }

    @Override
    public void serialize(@NotNull final LattePhpClassStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getClassName());
    }

    @Override
    @NotNull
    public LattePhpClassStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        String name = dataStream.readNameString();
        return new LattePhpClassStubImpl(parentStub, name);
    }

    @Override
    public void indexStub(@NotNull final LattePhpClassStub stub, @NotNull final IndexSink sink) {
        sink.occurrence(LattePhpClassIndex.KEY, stub.getClassName());
    }

    @NotNull
    @Override
    public LattePhpClassStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        List<LighterASTNode> keyNode = LightTreeUtil.getChildrenOfType(tree, node, TokenSet.create(PHP_NAMESPACE_REFERENCE, T_PHP_NAMESPACE_RESOLUTION, PHP_CLASS_USAGE));
        StringBuilder builder = new StringBuilder();
        for (LighterASTNode astNode : keyNode) {
            if (TokenSet.create(T_PHP_NAMESPACE_REFERENCE, T_PHP_NAMESPACE_RESOLUTION, T_PHP_IDENTIFIER).contains(astNode.getTokenType())) {
                builder.append(intern(tree.getCharTable(), astNode));
                continue;
            }

            List<LighterASTNode> children = LightTreeUtil.getChildrenOfType(tree, astNode, TokenSet.create(T_PHP_NAMESPACE_REFERENCE, T_PHP_NAMESPACE_RESOLUTION, T_PHP_IDENTIFIER));
            for (LighterASTNode current : children) {
                builder.append(intern(tree.getCharTable(), current));
            }
        }
        return new LattePhpClassStubImpl(parentStub, LattePhpUtil.normalizeClassName(builder.toString()));
    }

    public static String intern(@NotNull CharTable table, LighterASTNode node) {
        assert node instanceof LighterASTTokenNode : node;
        return table.intern(((LighterASTTokenNode) node).getText()).toString();
    }
}
