package com.jantvrdik.intellij.latte.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteMacro;
import com.jantvrdik.intellij.latte.intentions.AddCustomAttrOnlyMacro;
import com.jantvrdik.intellij.latte.intentions.AddCustomPairMacro;
import com.jantvrdik.intellij.latte.intentions.AddCustomUnpairedMacro;
import com.jantvrdik.intellij.latte.psi.*;
import org.jetbrains.annotations.NotNull;

/**
 * Annotator is mostly used to check semantic rules which can not be easily checked during parsing.
 */
public class LatteAnnotator implements Annotator {
	@Override
	public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
		if (element instanceof LatteMacroClassic) {
			LatteMacroTag openTag = ((LatteMacroClassic) element).getOpenTag();
			LatteMacroTag closeTag = ((LatteMacroClassic) element).getCloseTag();

			String openTagName = openTag.getMacroName();
			LatteMacro macro = LatteConfiguration.INSTANCE.getMacro(element.getProject(), openTagName);
			if (macro == null || macro.type == LatteMacro.Type.ATTR_ONLY) {
				Annotation annotation = holder.createErrorAnnotation(openTag, "Unknown macro {" + openTagName + "}");
				annotation.registerFix(new AddCustomPairMacro(openTagName));
				annotation.registerFix(new AddCustomUnpairedMacro(openTagName));
			}

			String closeTagName = closeTag != null ? closeTag.getMacroName() : null;
			if (closeTagName != null && !closeTagName.isEmpty() && !closeTagName.equals(openTagName)) {
				holder.createErrorAnnotation(closeTag, "Unexpected {/" + closeTagName + "}, expected {/" + openTagName + "}");
			}

		} else if (element instanceof LatteNetteAttr) {
			PsiElement attrName = ((LatteNetteAttr) element).getAttrName();
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
				Annotation annotation = holder.createErrorAnnotation(attrName, "Unknown attribute macro " + attrName.getText());
				annotation.registerFix(new AddCustomPairMacro(macroName));
				if (!prefixed) annotation.registerFix(new AddCustomAttrOnlyMacro(macroName));

			} else if (prefixed && macro.type != LatteMacro.Type.PAIR) {
				holder.createErrorAnnotation(attrName, "Attribute macro n:" + macroName + " can not be used with prefix.");
			}

		} else if (element instanceof LattePairHtmlTag) {
			LattePairHtmlTagOpen openTag = ((LattePairHtmlTag) element).getPairHtmlTagOpen();
			LattePairHtmlTagClose closeTag = ((LattePairHtmlTag) element).getPairHtmlTagClose();

			String openTagName = openTag.getTagName();
			String closeTagName = closeTag.getTagName();

			if (!closeTagName.equalsIgnoreCase(openTagName)) {
				holder.createErrorAnnotation(closeTag, "Unexpected </" + closeTagName + ">, expected </" + openTagName + ">");
			}
		}
	}
}
