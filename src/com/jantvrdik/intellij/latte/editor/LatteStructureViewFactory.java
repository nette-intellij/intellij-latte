package com.jantvrdik.intellij.latte.editor;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.impl.StructureViewComposite;
import com.intellij.ide.structureView.impl.TemplateLanguageStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

public class LatteStructureViewFactory implements PsiStructureViewFactory{

	@Nullable
	@Override
	public StructureViewBuilder getStructureViewBuilder(PsiFile psiFile) {
		return new TemplateLanguageStructureViewBuilder(psiFile) {
			@Override
			protected StructureViewComposite.StructureViewDescriptor createMainView(FileEditor fileEditor, PsiFile mainFile) {
				return null;
			}
		};
	}
}
