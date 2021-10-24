package com.jantvrdik.intellij.latte.indexes.stubs.types;

import com.intellij.lang.LighterAST;
import com.intellij.lang.LighterASTNode;
import com.intellij.psi.impl.source.tree.LightTreeUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.jantvrdik.intellij.latte.indexes.extensions.LattePhpTypeIndex;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpTypeStub;
import com.jantvrdik.intellij.latte.indexes.stubs.impl.LattePhpTypeStubImpl;
import com.jantvrdik.intellij.latte.parser.LatteElementTypes;
import com.jantvrdik.intellij.latte.psi.LattePhpType;
import com.jantvrdik.intellij.latte.psi.impl.LattePhpTypeImpl;
import com.jantvrdik.intellij.latte.utils.LatteTypesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LattePhpTypeStubType extends LattePhpStubType<LattePhpTypeStub, LattePhpType> {
    public LattePhpTypeStubType(String debugName) {
        super(debugName, LatteElementTypes.LANG);
    }

    @Override
    public LattePhpType createPsi(@NotNull final LattePhpTypeStub stub) {
        return new LattePhpTypeImpl(stub, this);
    }

    @Override
    @NotNull
    public LattePhpTypeStub createStub(@NotNull final LattePhpType psi, final StubElement parentStub) {
        return new LattePhpTypeStubImpl(parentStub, psi.getReturnType().toString());
    }

    @Override
    @NotNull
    public String getExternalId() {
        return "latte.phpType.prop";
    }

    @Override
    public void serialize(@NotNull final LattePhpTypeStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getPhpType());
    }

    @Override
    @NotNull
    public LattePhpTypeStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        String name = dataStream.readNameString();
        return new LattePhpTypeStubImpl(parentStub, name);
    }

    @Override
    public void indexStub(@NotNull final LattePhpTypeStub stub, @NotNull final IndexSink sink) {
        sink.occurrence(LattePhpTypeIndex.KEY, stub.getPhpType());
    }

    @NotNull
    @Override
    public LattePhpTypeStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        String phpType = LightTreeUtil.toFilteredString(tree, node, LatteTypesUtil.whitespaceTokens);
        return new LattePhpTypeStubImpl(parentStub, phpType);
    }
}
