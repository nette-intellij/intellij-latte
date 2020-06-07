package com.jantvrdik.intellij.latte.settings.xml.exceptions;

public class CanNotReloadXmlFileException extends Exception {
    public CanNotReloadXmlFileException(Throwable e) {
        super("Virtual file for xml configuration can not be laded.", e);
    }
}
