package com.jantvrdik.intellij.latte.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
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

		}/* else if (element instanceof LeafPsiElement && element.getParent().getLastChild() instanceof PsiErrorElement) {
			LeafPsiElement leaf = (LeafPsiElement) element;
			if (leaf.getElementType() == LatteTypes.T_PHP_DOUBLE_QUOTE_LEFT
					|| leaf.getElementType() == LatteTypes.T_PHP_SINGLE_QUOTE_LEFT) {
				holder.createErrorAnnotation(element, "Unclosed string");
			} else if (leaf.getElementType() == LatteTypes.T_MACRO_OPEN_TAG_OPEN || leaf.getElementType() == LatteTypes.T_MACRO_CLOSE_TAG_OPEN) {
				holder.createErrorAnnotation(element.getParent(), "Malformed tag. Missing closing }");
			}
		}*/
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
			Annotation annotation = holder.createErrorAnnotation(attrName, "Unknown attribute tag " + attrName.getText());
			annotation.registerFix(new AddCustomPairMacro(tagName));
			if (!prefixed) annotation.registerFix(new AddCustomAttrOnlyMacro(tagName));

		} else if (prefixed && macro.getType() != LatteTagSettings.Type.PAIR && macro.getType() != LatteTagSettings.Type.AUTO_EMPTY) {
			holder.createErrorAnnotation(attrName, "Attribute tag n:" + tagName + " can not be used with prefix.");
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
					holder.createErrorAnnotation(openTag, "Can not use n:" + openTagName + " attribute as normal tag");
					if (closeTag != null) {
						holder.createErrorAnnotation(closeTag, "Tag n:" + openTagName + " can not be used as pair tag");
					}

				} else {
					Annotation annotation = holder.createErrorAnnotation(openTag, "Unknown tag {" + openTagName + "}");
					annotation.registerFix(new AddCustomPairMacro(openTagName));
					annotation.registerFix(new AddCustomUnpairedMacro(openTagName));
				}
			}
		}

		String closeTagName = closeTag != null ? closeTag.getMacroName() : null;
		if (closeTagName != null && !closeTagName.isEmpty() && !closeTagName.equals(openTagName)) {
			holder.createErrorAnnotation(closeTag, "Unexpected {/" + closeTagName + "}, expected {/" + openTagName + "}");
		}

		if (
				macro != null
				&& closeTag == null
				&& ((element instanceof LattePairMacro && macro.getType() == LatteTagSettings.Type.AUTO_EMPTY) || macro.getType() == LatteTagSettings.Type.PAIR)
				&& !openTagName.equals("block")
				&& !openTagName.equals("_")
		) {
			holder.createErrorAnnotation(openTag, "Unclosed tag " + openTagName);
		}
	}
}
