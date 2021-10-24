package com.jantvrdik.intellij.latte.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
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

	public static LattePhpMethod createMethod(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "$x->" + name + "()");
		LattePhpContent phpContent = findFirstPhpContent(file);
		if (phpContent == null || phpContent.getPhpStatementList().size() == 0) {
			return null;
		}

		try {
			return phpContent.getPhpStatementList().get(0).getPhpStatementPartList().get(0).getPhpMethod();

		} catch (NullPointerException e) {
			return null;
		}
	}

	public static LattePhpProperty createProperty(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "$x->" + name);
		LattePhpContent phpContent = findFirstPhpContent(file);
		if (phpContent == null) {
			return null;
		}

		try {
			return phpContent.getPhpStatementList().get(0).getPhpStatementPartList().get(0).getPhpProperty();

		} catch (NullPointerException e) {
			return null;
		}
	}

	public static LattePhpConstant createConstant(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "$x::" + name);
		LattePhpContent phpContent = findFirstPhpContent(file);
		if (phpContent == null) {
			return null;
		}

		try {
			return phpContent.getPhpStatementList().get(0).getPhpStatementPartList().get(0).getPhpConstant();

		} catch (NullPointerException e) {
			return null;
		}
	}

	public static LattePhpClassUsage createClassRootUsage(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "\\" + name);
		LattePhpClassUsage firstChild = PsiTreeUtil.findChildOfType(file, LattePhpClassUsage.class);
		if (firstChild != null) {
			try {
				return firstChild;

			} catch (NullPointerException e) {
				return null;
			}
		}
		return null;
	}

	public static LattePhpClassUsage createClassUsage(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "FooNamespace\\" + name);
		LattePhpClassUsage firstChild = PsiTreeUtil.findChildOfType(file, LattePhpClassUsage.class);
		if (firstChild != null) {
			try {
				return firstChild;

			} catch (NullPointerException e) {
				return null;
			}
		}
		return null;
	}

	public static PsiElement createStaticVariable(Project project, String name) {
		final LatteFile file = createFileWithPhpMacro(project, "$x::$" + name);
		LattePhpContent phpContent = findFirstPhpContent(file);
		if (phpContent == null) {
			return null;
		}

		try {
			return phpContent.getPhpStatementList().get(0).getPhpStatementPartList().get(0).getPhpStaticVariable();

		} catch (NullPointerException e) {
			return null;
		}
	}

	@NotNull
	public static PsiElement replaceChildNode(@NotNull PsiElement psiElement, @NotNull PsiElement newElement, @Nullable ASTNode keyNode) {
		ASTNode newKeyNode = newElement.getFirstChild().getNode();
		if (newKeyNode == null) {
			return psiElement;
		}

		if (keyNode == null) {
			psiElement.getNode().addChild(newKeyNode);

		} else {
			psiElement.getNode().replaceChild(keyNode, newKeyNode);
		}
		return psiElement;
	}

	@NotNull
	public static PsiElement replaceLastNode(@NotNull PsiElement psiElement, @NotNull PsiElement newElement, @Nullable ASTNode keyNode) {
		ASTNode newKeyNode = newElement.getLastChild().getNode();
		if (newKeyNode == null) {
			return psiElement;
		}

		if (keyNode == null) {
			psiElement.getNode().addChild(newKeyNode);

		} else {
			psiElement.getNode().replaceChild(keyNode, newKeyNode);
		}
		return psiElement;
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