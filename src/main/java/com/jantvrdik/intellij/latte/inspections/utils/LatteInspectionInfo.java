package com.jantvrdik.intellij.latte.inspections.utils;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class LatteInspectionInfo {

	@NotNull
	private final PsiElement element;

	@NotNull
	private final String description;

	@NotNull
	private final ProblemHighlightType type;

	@NotNull
	private final List<LocalQuickFix> fixes = new ArrayList<>();

	private LatteInspectionInfo(
			@NotNull final PsiElement element,
			@NotNull final String description,
			@NotNull final ProblemHighlightType type
	) {
		this.element = element;
		this.description = description;
		this.type = type;
	}

	public static LatteInspectionInfo error(@NotNull final PsiElement element, @NotNull final String description) {
		return new LatteInspectionInfo(element, description, ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
	}

	public static LatteInspectionInfo unused(@NotNull final PsiElement element, @NotNull final String description) {
		return new LatteInspectionInfo(element, description, ProblemHighlightType.LIKE_UNUSED_SYMBOL);
	}

	public static LatteInspectionInfo warning(@NotNull final PsiElement element, @NotNull final String description) {
		return new LatteInspectionInfo(element, description, ProblemHighlightType.WARNING);
	}

	public static LatteInspectionInfo deprecated(@NotNull final PsiElement element, @NotNull final String description) {
		return new LatteInspectionInfo(element, description, ProblemHighlightType.LIKE_DEPRECATED);
	}

	public static LatteInspectionInfo weakWarning(@NotNull final PsiElement element, @NotNull final String description) {
		return new LatteInspectionInfo(element, description, ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
	}

	public void addFix(@NotNull LocalQuickFix quickFix) {
		fixes.add(quickFix);
	}

	@NotNull
	public String getDescription() {
		return description;
	}

	@NotNull
	public PsiElement getElement() {
		return element;
	}

	@NotNull
	public ProblemHighlightType getType() {
		return type;
	}

	@NotNull
	public LocalQuickFix[] getFixes() {
		return fixes.toArray(new LocalQuickFix[0]);
	}
}
