package com.jantvrdik.intellij.latte.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteMacro;
import com.jantvrdik.intellij.latte.intentions.*;
import com.jantvrdik.intellij.latte.psi.*;
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
				holder.createErrorAnnotation(element.getParent(), "Malformed macro. Missing closing }");
			}
		}*/
	}

	private void checkNetteAttr(@NotNull LatteNetteAttr element, @NotNull AnnotationHolder holder) {
		PsiElement attrName = element.getAttrName();
		String macroName = attrName.getText();
		boolean prefixed = false;

		if (macroName.startsWith("n:inner-")) {
			prefixed = true;
			macroName = macroName.substring(8);
		} else if (macroName.startsWith("n:tag-")) {
			prefixed = true;
			macroName = macroName.substring(6);
		} else {
			macroName = macroName.substring(2);
		}

		LatteMacro macro = LatteConfiguration.INSTANCE.getMacro(element.getProject(), macroName);
		if (macro == null || macro.type == LatteMacro.Type.UNPAIRED) {
			Annotation annotation = holder.createErrorAnnotation(attrName, "Unknown attribute tag " + attrName.getText());
			annotation.registerFix(new AddCustomPairMacro(macroName));
			if (!prefixed) annotation.registerFix(new AddCustomAttrOnlyMacro(macroName));

		} else if (prefixed && macro.type != LatteMacro.Type.PAIR && macro.type != LatteMacro.Type.AUTO_EMPTY) {
			holder.createErrorAnnotation(attrName, "Attribute tag n:" + macroName + " can not be used with prefix.");
		}
	}

	private void checkMacroClassic(@NotNull LatteMacroClassic element, @NotNull AnnotationHolder holder) {
		LatteMacroTag openTag = element.getOpenTag();
		LatteMacroTag closeTag = element.getCloseTag();

		String openTagName = openTag.getMacroName();
		LatteMacro macro = LatteConfiguration.INSTANCE.getMacro(element.getProject(), openTagName);
		if (macro == null || macro.type == LatteMacro.Type.ATTR_ONLY) {
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
				&& ((element instanceof LattePairMacro && macro.type == LatteMacro.Type.AUTO_EMPTY) || macro.type == LatteMacro.Type.PAIR)
				&& !openTagName.equals("block")
				&& !openTagName.equals("_")
		) {
			holder.createErrorAnnotation(openTag, "Unclosed tag " + openTagName);
		}
	}
}
