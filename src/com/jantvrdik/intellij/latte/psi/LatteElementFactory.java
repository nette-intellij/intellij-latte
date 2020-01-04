package com.jantvrdik.intellij.latte.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.jantvrdik.intellij.latte.LatteFileType;

public class LatteElementFactory {
	public static LattePhpVariable createVariable(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "$" + name);
		PsiElement firstChild = file.getFirstChild().getFirstChild().getFirstChild();
		if (firstChild != null) {
			try {
				return (LattePhpVariable) firstChild.getNextSibling().getNextSibling().getFirstChild().getFirstChild().getNextSibling();

			} catch (NullPointerException e) {
				return null;
			}
		}
		return null;
	}

	public static PsiElement createMethod(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "->" + name + "()");
		PsiElement firstChild = file.getFirstChild().getFirstChild().getFirstChild();
		if (firstChild != null) {
			try {
				return firstChild.getNextSibling().getNextSibling().getFirstChild().getFirstChild().getNextSibling();

			} catch (NullPointerException e) {
				return null;
			}
		}
		return null;
	}

	public static PsiElement createProperty(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "$x->" + name);
		PsiElement firstChild = file.getFirstChild().getFirstChild().getFirstChild();
		if (firstChild != null) {
			try {
				return firstChild.getNextSibling().getNextSibling().getFirstChild().getLastChild();

			} catch (NullPointerException e) {
				return null;
			}
		}
		return null;
	}

	public static LatteFile createFileWithPhpMacro(Project project, String text) {
		String name = "dummy.latte";
		return (LatteFile) PsiFileFactory.getInstance(project).
				createFileFromText(name, LatteFileType.INSTANCE, "{php " + text + "}");
	}
}