package com.jantvrdik.intellij.latte.annotator;

import com.intellij.lang.annotation.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.intentions.*;
import com.jantvrdik.intellij.latte.psi.*;
import com.jantvrdik.intellij.latte.settings.LatteTagSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Annotator is mostly used to check semantic rules which can not be easily checked during parsing.
 */
public class LatteAnnotator implements Annotator {
	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
		if (element instanceof LatteMacroClassic) {
			checkMacroClassic((LatteMacroClassic) element, holder);

		} else if (element instanceof LatteNetteAttr) {
			checkNetteAttr((LatteNetteAttr) element, holder);

		} else if (element instanceof LeafPsiElement && element.getParent().getLastChild() instanceof PsiErrorElement) {
			LeafPsiElement leaf = (LeafPsiElement) element;
			if (leaf.getElementType() == LatteTypes.T_MACRO_OPEN_TAG_OPEN || leaf.getElementType() == LatteTypes.T_MACRO_CLOSE_TAG_OPEN) {
				createErrorAnnotation(holder, "Malformed tag. Missing closing }");
			}
		}
	}

	private void checkNetteAttr(@NotNull LatteNetteAttr element, @NotNull AnnotationHolder holder) {
		PsiElement attrName = element.getAttrName();
		String tagName = attrName.getText();
		boolean prefixed = false;

		if (tagName.startsWith("n:inner-")) {
			prefixed = true;
			tagName = tagName.substring(8);
		} else if (tagName.startsWith("n:tag-")) {
			prefixed = true;
			tagName = tagName.substring(6);
		} else {
			tagName = tagName.substring(2);
		}

		Project project = element.getProject();
		LatteTagSettings macro = LatteConfiguration.getInstance(project).getTag(tagName);
		if (macro == null || macro.getType() == LatteTagSettings.Type.UNPAIRED) {
			AnnotationBuilder builder = holder.newAnnotation(HighlightSeverity.ERROR, "Unknown attribute tag " + attrName.getText())
					.range(attrName)
					.withFix(new AddCustomPairMacro(tagName));
			if (!prefixed) {
				builder = builder.withFix(new AddCustomAttrOnlyMacro(tagName));
			}
			builder.create();

		} else if (prefixed && macro.getType() != LatteTagSettings.Type.PAIR && macro.getType() != LatteTagSettings.Type.AUTO_EMPTY) {
			createErrorAnnotation(holder, attrName, "Attribute tag n:" + tagName + " can not be used with prefix.");
		}
	}

	private void checkMacroClassic(@NotNull LatteMacroClassic element, @NotNull AnnotationHolder holder) {
		LatteMacroTag openTag = element.getOpenTag();
		LatteMacroTag closeTag = element.getCloseTag();

		String openTagName = openTag.getMacroName();
		LatteTagSettings macro = LatteConfiguration.getInstance(element.getProject()).getTag(openTagName);
		if (macro == null || macro.getType() == LatteTagSettings.Type.ATTR_ONLY) {
			boolean isOk = false;
			LatteMacroContent content = openTag.getMacroContent();
			if (content != null) {
				LattePhpContent phpContent = content.getFirstPhpContent();
				if (phpContent != null && phpContent.getFirstChild() instanceof LattePhpVariable) {
					isOk = true;
				}
			}

			if (!isOk) {
				if (macro != null) {
					createErrorAnnotation(holder, openTag, "Can not use n:" + openTagName + " attribute as normal tag");
					if (closeTag != null) {
						createErrorAnnotation(holder, closeTag, "Tag n:" + openTagName + " can not be used as pair tag");
					}

				} else {
					AnnotationBuilder annotation = holder.newAnnotation(HighlightSeverity.ERROR, "Unknown tag {" + openTagName + "}").range(openTag);
					annotation = annotation.withFix(new AddCustomPairMacro(openTagName));
					annotation = annotation.withFix(new AddCustomUnpairedMacro(openTagName));
					annotation.create();
				}
			}
		}

		String closeTagName = closeTag != null ? closeTag.getMacroName() : null;
		if (closeTagName != null && !closeTagName.isEmpty() && !closeTagName.equals(openTagName)) {
			createErrorAnnotation(holder, closeTag, "Unexpected {/" + closeTagName + "}, expected {/" + openTagName + "}");
		}

		if (
				macro != null
				&& closeTag == null
				&& ((element instanceof LattePairMacro && macro.getType() == LatteTagSettings.Type.AUTO_EMPTY) || macro.getType() == LatteTagSettings.Type.PAIR)
		) {
			//if (!macro.isTagBlock() || element.getContainingFile().getLastChild() == openTag.getParent()) {
			final int[] unclosed = {0};
			openTag.getParent().acceptChildren(new PsiRecursiveElementWalkingVisitor() {
				@Override
				public void visitElement(PsiElement element) {
					if (element instanceof LattePairMacro) {
						LatteMacroTag tag = ((LattePairMacro) element).getOpenTag();
						if (tag.getMacroName().equals("block") && ((LattePairMacro) element).getCloseTag() == null) {
							unclosed[0]++;
						} else {
							super.visitElement(element);
						}

					} else {
						super.visitElement(element);
					}
				}
			});
			PsiElement el = PsiTreeUtil.getChildOfAnyType(openTag.getParent(), LattePairMacro.class);
			if (!macro.isTagBlock() || unclosed[0] > 0) {
				createErrorAnnotation(holder, openTag, "Unclosed tag " + openTagName);
			}
		}
	}

	private void createErrorAnnotation(final @NotNull AnnotationHolder holder, final @NotNull String message) {
		holder.newAnnotation(HighlightSeverity.ERROR, message).create();
	}

	private void createErrorAnnotation(
			final @NotNull AnnotationHolder holder,
			final @NotNull PsiElement element,
			final @NotNull String message
	) {
		holder.newAnnotation(HighlightSeverity.ERROR, message).range(element).create();
	}
}
