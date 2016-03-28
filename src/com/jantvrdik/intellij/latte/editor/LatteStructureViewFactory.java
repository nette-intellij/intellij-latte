package com.jantvrdik.intellij.latte.editor;

import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.structureView.impl.StructureViewComposite;
import com.intellij.ide.structureView.impl.TemplateLanguageStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatteStructureViewFactory implements PsiStructureViewFactory{

	@Nullable
	@Override
	public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
		return new TemplateLanguageStructureViewBuilder(psiFile) {
			@Override
			protected StructureViewComposite.StructureViewDescriptor createMainView(FileEditor fileEditor, PsiFile mainFile) {
				StructureView mainView = (new TreeBasedStructureViewBuilder() {
					@NotNull
					public StructureViewModel createStructureViewModel(@Nullable Editor editor) {
						return new StructureViewModelBase(psiFile, editor, new LatteStructureViewTreeElement(psiFile));
					}
				}).createStructureView(fileEditor, mainFile.getProject());
				return new StructureViewComposite.StructureViewDescriptor("Latte", mainView, mainFile.getFileType().getIcon());
			}
		};
	}
}
