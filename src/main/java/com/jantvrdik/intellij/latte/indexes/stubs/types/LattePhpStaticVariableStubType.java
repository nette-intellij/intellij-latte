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
import com.jantvrdik.intellij.latte.indexes.extensions.LattePhpStaticVariableIndex;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpStaticVariableStub;
import com.jantvrdik.intellij.latte.indexes.stubs.impl.LattePhpStaticVariableStubImpl;
import com.jantvrdik.intellij.latte.parser.LatteElementTypes;
import com.jantvrdik.intellij.latte.php.LattePhpVariableUtil;
import com.jantvrdik.intellij.latte.psi.LattePhpStaticVariable;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.psi.impl.LattePhpStaticVariableImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LattePhpStaticVariableStubType extends LattePhpStubType<LattePhpStaticVariableStub, LattePhpStaticVariable> {
    public LattePhpStaticVariableStubType(String debugName) {
        super(debugName, LatteElementTypes.LANG);
    }

    @Override
    public LattePhpStaticVariable createPsi(@NotNull final LattePhpStaticVariableStub stub) {
        return new LattePhpStaticVariableImpl(stub, this);
    }

    @Override
    @NotNull
    public LattePhpStaticVariableStub createStub(@NotNull final LattePhpStaticVariable psi, final StubElement parentStub) {
        return new LattePhpStaticVariableStubImpl(parentStub, psi.getVariableName());
    }

    @Override
    @NotNull
    public String getExternalId() {
        return "latte.phpStaticVariable.prop";
    }

    @Override
    public void serialize(@NotNull final LattePhpStaticVariableStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getVariableName());
    }

    @Override
    @NotNull
    public LattePhpStaticVariableStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        String name = dataStream.readNameString();
        return new LattePhpStaticVariableStubImpl(parentStub, name);
    }

    @Override
    public void indexStub(@NotNull final LattePhpStaticVariableStub stub, @NotNull final IndexSink sink) {
        sink.occurrence(LattePhpStaticVariableIndex.KEY, stub.getVariableName());
    }

    @NotNull
    @Override
    public LattePhpStaticVariableStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        LighterASTNode keyNode = LightTreeUtil.firstChildOfType(tree, node, TokenSet.create(LatteTypes.T_MACRO_ARGS_VAR));
        String key = intern(tree.getCharTable(), keyNode);
        return new LattePhpStaticVariableStubImpl(parentStub, LattePhpVariableUtil.normalizePhpVariable(key));
    }

    public static String intern(@NotNull CharTable table, LighterASTNode node) {
        assert node instanceof LighterASTTokenNode : node;
        return table.intern(((LighterASTTokenNode) node).getText()).toString();
    }
}
