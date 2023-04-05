package com.jantvrdik.intellij.latte.psi.elements;

import com.jantvrdik.intellij.latte.psi.LatteHtmlOpenTag;

public interface LatteHtmlTagContainerElement extends LattePsiElement {

    LatteHtmlOpenTag getHtmlOpenTag();

}