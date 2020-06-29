package com.jantvrdik.intellij.latte.liveTemplates;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;

public class LatteTemplateProvider implements DefaultLiveTemplatesProvider {
	@Override
	public String[] getDefaultLiveTemplateFiles() {
		return new String[]{"liveTemplates/Latte"};
	}

	@Override
	public String[] getHiddenLiveTemplateFiles() {
		return null;
	}
}
