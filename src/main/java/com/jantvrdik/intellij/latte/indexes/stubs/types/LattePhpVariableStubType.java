package com.jantvrdik.intellij.latte.indexes.stubs.types;

import com.intellij.lang.ASTNode;
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
import com.jantvrdik.intellij.latte.indexes.extensions.LattePhpVariableIndex;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpVariableStub;
import com.jantvrdik.intellij.latte.indexes.stubs.LattePhpTypeStub;
import com.jantvrdik.intellij.latte.indexes.stubs.impl.LattePhpVariableStubImpl;
import com.jantvrdik.intellij.latte.parser.LatteElementTypes;
import com.jantvrdik.intellij.latte.php.LattePhpVariableUtil;
import com.jantvrdik.intellij.latte.psi.LattePhpVariable;
import com.jantvrdik.intellij.latte.psi.LatteTypes;
import com.jantvrdik.intellij.latte.psi.impl.LattePhpVariableImpl;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class LattePhpVariableStubType extends LattePhpTypeStub<LattePhpVariableStub, LattePhpVariable> {
    public LattePhpVariableStubType(String debugName) {
        super(debugName, LatteElementTypes.LANG);
    }

    @Override
    public LattePhpVariable createPsi(@NotNull final LattePhpVariableStub stub) {
        return new LattePhpVariableImpl(stub, this);
    }

    @Override
    @NotNull
    public LattePhpVariableStub createStub(@NotNull final LattePhpVariable psi, final StubElement parentStub) {
        return new LattePhpVariableStubImpl(parentStub, psi.getVariableName());
    }

    @Override
    @NotNull
    public String getExternalId() {
        return "latte.phpVariable.prop";
    }

    @Override
    public void serialize(@NotNull final LattePhpVariableStub stub, @NotNull final StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getVariableName());
    }

    @Override
    public boolean shouldCreateStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement<?> parentStub) {
        return LightTreeUtil.firstChildOfType(tree, node, TokenSet.create(LatteTypes.T_MACRO_ARGS_VAR)) != null;
    }

    @Override
    @NotNull
    public LattePhpVariableStub deserialize(@NotNull final StubInputStream dataStream, final StubElement parentStub) throws IOException {
        String name = dataStream.readNameString();
        return new LattePhpVariableStubImpl(parentStub, name);
    }

    @Override
    public void indexStub(@NotNull final LattePhpVariableStub stub, @NotNull final IndexSink sink) {
        sink.occurrence(LattePhpVariableIndex.KEY, stub.getVariableName());
    }

    @NotNull
    @Override
    public LattePhpVariableStub createStub(@NotNull LighterAST tree, @NotNull LighterASTNode node, @NotNull StubElement parentStub) {
        LighterASTNode keyNode = LightTreeUtil.firstChildOfType(tree, node, TokenSet.create(LatteTypes.T_MACRO_ARGS_VAR));
        String key = intern(tree.getCharTable(), keyNode);
        return new LattePhpVariableStubImpl(parentStub, LattePhpVariableUtil.normalizePhpVariable(key));
    }

    public static String intern(@NotNull CharTable table, LighterASTNode node) {
        assert node instanceof LighterASTTokenNode : node;
        return table.intern(((LighterASTTokenNode) node).getText()).toString();
    }
}
