package com.jantvrdik.intellij.latte.psi;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.templateLanguages.TemplateDataElementType;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.jantvrdik.intellij.latte.LatteLanguage;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class LatteFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider implements TemplateLanguageFileViewProvider {

	public static LatteElementType OUTER_LATTE = new LatteElementType("Outer latte");

	public LatteFileViewProvider(PsiManager manager, VirtualFile virtualFile, boolean eventSystemEnabled) {
		super(manager, virtualFile, eventSystemEnabled);
	}

	@NotNull
	@Override
	public Language getBaseLanguage() {
		return LatteLanguage.INSTANCE;
	}

	@NotNull
	public Set<Language> getLanguages() {
		return new THashSet(Arrays.asList(new Language[]{LatteLanguage.INSTANCE, this.getTemplateDataLanguage()}));
	}

	@Override
	protected MultiplePsiFilesPerDocumentFileViewProvider cloneInner(VirtualFile fileCopy) {
		return new LatteFileViewProvider(getManager(), fileCopy, false);
	}

	@NotNull
	@Override
	public Language getTemplateDataLanguage() {
		return HTMLLanguage.INSTANCE;
	}

	@Nullable
	protected PsiFile createFile(@NotNull Language lang) {
		ParserDefinition parser = LanguageParserDefinitions.INSTANCE.forLanguage(lang);
		if (parser == null) {
			return null;
		} else if (lang == this.getTemplateDataLanguage()) {
			PsiFileImpl file = (PsiFileImpl) parser.createFile(this);
			file.setContentElementType(new TemplateDataElementType("Outer HTML in Latte", getBaseLanguage(), LatteTypes.T_TEXT, OUTER_LATTE));
			return file;
		} else {
			return lang == this.getBaseLanguage() ? parser.createFile(this) : null;
		}
	}
}
