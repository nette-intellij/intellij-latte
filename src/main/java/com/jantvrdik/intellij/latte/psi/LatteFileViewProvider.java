package com.jantvrdik.intellij.latte.psi;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.intellij.psi.tree.IElementType;
import com.jantvrdik.intellij.latte.LatteLanguage;
import com.jantvrdik.intellij.latte.utils.LatteHtmlUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class LatteFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider implements TemplateLanguageFileViewProvider {

	public static LatteElementType OUTER_LATTE = new LatteElementType("Outer latte");
	private static Pattern xmlContentType = Pattern.compile("^\\{contentType [^}]*xml[^}]*}.*");
	private static IElementType templateDataElement = new LatteTemplateDataElementType(
			"Outer HTML/XML in Latte",
			LatteLanguage.INSTANCE,
			LatteHtmlUtil.HTML_TOKENS,
			OUTER_LATTE
	);

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
		Set<Language> languages = new HashSet<>(3);
		languages.add(LatteLanguage.INSTANCE);
		languages.add(getTemplateDataLanguage());

		return languages;
	}

	@Override
	protected MultiplePsiFilesPerDocumentFileViewProvider cloneInner(VirtualFile fileCopy) {
		return new LatteFileViewProvider(getManager(), fileCopy, false);
	}

	@NotNull
	@Override
	public Language getTemplateDataLanguage() {
//		return LatteLanguage.INSTANCE;
		return isXml() ? XMLLanguage.INSTANCE : HTMLLanguage.INSTANCE;
	}

	@Nullable
	protected PsiFile createFile(@NotNull Language lang) {
		ParserDefinition parser = LanguageParserDefinitions.INSTANCE.forLanguage(lang);
		if (parser == null) {
			return null;
		} else if (lang == XMLLanguage.INSTANCE || lang == HTMLLanguage.INSTANCE) {
			PsiFileImpl file = (PsiFileImpl) parser.createFile(this);
			file.setContentElementType(templateDataElement);
			return file;
		} else {
			return lang == this.getBaseLanguage() ? parser.createFile(this) : null;
		}
	}

	private boolean isXml() {
		if (this.getDocument() == null) {
			return false;
		}
		String text = this.getDocument().getText();
		int pos = text.indexOf("\n");
		if (pos > 0) {
			text = text.substring(0, pos);
		}
		return xmlContentType.matcher(text).matches();
	}
}
