package com.jantvrdik.intellij.latte.settings.xml;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.*;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomElementsAnnotator;
import com.jantvrdik.intellij.latte.config.LatteFileConfiguration;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import com.jantvrdik.intellij.latte.settings.xml.elements.LatteRootDomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LatteDomFileDescription extends DomFileDescription<LatteRootDomElement> {
	public LatteDomFileDescription() {
		this(LatteRootDomElement.class, "latte", "");
	}

	public LatteDomFileDescription(Class<LatteRootDomElement> rootElementClass, String rootTagName, @NotNull String... allPossibleRootTagNamespaces) {
		super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);
	}

	@Override
	public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
		return (LatteFileConfiguration.isXmlConfigurationFile(file) || LatteFileConfiguration.isXmlConfiguration(file)) && super.isMyFile(file, module);
	}

	@Override
	public @Nullable Icon getFileIcon(int flags) {
		return LatteIcons.FILE_XML;
	}

	@Override
	public @Nullable DomElementsAnnotator createAnnotator() {
		return new DomElementsAnnotator() {
			@Override
			public void annotate(DomElement element, DomElementAnnotationHolder holder) {
				String s = "";
				//holder.createProblem(element);
			}
		};
	}

}
