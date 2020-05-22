package com.jantvrdik.intellij.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.jantvrdik.intellij.latte.config.LatteFileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LatteXmlFileInspection extends BaseLocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "LatteXmlFileInspection";
	}

	@Nullable
	@Override
	public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
		if (!(file instanceof XmlFile) || file.getLanguage() != XMLLanguage.INSTANCE || !file.getName().equals(LatteFileConfiguration.FILE_NAME)) {
			return null;
		}

		final List<ProblemDescriptor> problems = new ArrayList<>();

		XmlDocument xmlDocument = ((XmlFile) file).getDocument();
		if (xmlDocument == null) {
			addError(manager, problems, file, "Root tag <latte> is missing", isOnTheFly);
			return problems.toArray(new ProblemDescriptor[0]);
		}

		if (xmlDocument.getRootTag() == null || !xmlDocument.getRootTag().getName().equals("latte")) {
			addError(manager, problems, file, "Root tag <latte> is missing", isOnTheFly);
			return problems.toArray(new ProblemDescriptor[0]);
		}

		XmlTag rootTag = xmlDocument.getRootTag();
		if (rootTag.getAttribute("vendor") == null) {
			PsiElement psiElement = rootTag.getFirstChild().getNextSibling();
			addError(
					manager,
					problems,
					psiElement != null ? psiElement : file,
					"Missing required attribute `vendor` on tag <latte>. It must be unique name like composer package name.",
					isOnTheFly
			);
		}

		return problems.toArray(new ProblemDescriptor[0]);
	}
}
