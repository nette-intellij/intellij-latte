package com.jantvrdik.intellij.latte.indexes.stubs;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.*;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class LattePhpTypeStub<S extends StubElement, P extends PsiElement> extends ILightStubElementType<S, P> {
    public LattePhpTypeStub(@NotNull String debugName, @Nullable Language language) {
        super(debugName, language);
    }

    protected static void writePhpType(StubOutputStream dataStream, @NotNull LattePhpType type) throws IOException {
        Collection<String> types = type.getTypes();
        dataStream.writeVarInt(types.size());
        for (String s : types) {
            dataStream.writeName(s);
        }
    }

    @NotNull
    protected static LattePhpType readPhpType(StubInputStream dataStream) throws IOException {
        int i = dataStream.readVarInt();

        List<String> types = new ArrayList<>();
        for(int j = 0; j < i; ++j) {
            String s = dataStream.readNameString();
            if (s != null) {
                types.add(s);
            }
        }
        return LattePhpType.create(String.join("|", types));
    }
}
