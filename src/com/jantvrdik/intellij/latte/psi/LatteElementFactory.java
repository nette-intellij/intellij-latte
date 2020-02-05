package com.jantvrdik.intellij.latte.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.LatteFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatteElementFactory {
	public static PsiElement createVariable(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "$" + name);
		LattePhpContent phpContent = findFirstPhpContent(file);
		if (phpContent == null) {
			return null;
		}

		try {
			return phpContent.getFirstChild();

		} catch (NullPointerException e) {
			return null;
		}
	}

	public static PsiElement createMethod(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "->" + name + "()");
		LattePhpContent phpContent = findFirstPhpContent(file);
		if (phpContent == null) {
			return null;
		}

		try {
			return phpContent.getFirstChild().getNextSibling();

		} catch (NullPointerException e) {
			return null;
		}
	}

	public static PsiElement createProperty(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "$x->" + name);
		LattePhpContent phpContent = findFirstPhpContent(file);
		if (phpContent == null) {
			return null;
		}

		try {
			return phpContent.getFirstChild().getNextSibling().getNextSibling();

		} catch (NullPointerException e) {
			return null;
		}
	}

	public static PsiElement createConstant(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "$x::" + name);
		LattePhpContent phpContent = findFirstPhpContent(file);
		if (phpContent == null) {
			return null;
		}

		try {
			return phpContent.getFirstChild().getNextSibling().getNextSibling();

		} catch (NullPointerException e) {
			return null;
		}
	}

	public static PsiElement createClassType(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, name);
		LattePhpContent phpContent = findFirstPhpContent(file);
		if (phpContent == null) {
			return null;
		}

		try {
			return phpContent.getFirstChild();

		} catch (NullPointerException e) {
			return null;
		}
	}

	public static PsiElement createStaticVariable(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "$x::$" + name);
		LattePhpContent phpContent = findFirstPhpContent(file);
		if (phpContent == null) {
			return null;
		}

		try {
			return phpContent.getFirstChild().getNextSibling().getNextSibling();

		} catch (NullPointerException e) {
			return null;
		}
	}

	@Nullable
	private static LattePhpContent findFirstPhpContent(@NotNull LatteFile file) {
		PsiElement firstChild = file.getFirstChild().getFirstChild();
		if (!(firstChild instanceof LatteMacroOpenTag)) {
			return null;
		}

		LatteMacroContent content = ((LatteMacroOpenTag) firstChild).getMacroContent();
		if (content != null && content.getFirstPhpContent() != null) {
			try {
				return content.getFirstPhpContent();

			} catch (NullPointerException e) {
				return null;
			}
		}
		return null;
	}

	@NotNull
	private static LatteFile createFileWithPhpMacro(Project project, String text) {
		String name = "dummy.latte";
		return (LatteFile) PsiFileFactory.getInstance(project).
				createFileFromText(name, LatteFileType.INSTANCE, "{php " + text + "}");
	}
}