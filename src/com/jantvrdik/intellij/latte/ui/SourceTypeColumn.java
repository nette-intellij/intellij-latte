package com.jantvrdik.intellij.latte.ui;

import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

abstract class SourceTypeColumn<T> extends ColumnInfo<T, SourceTypeColumn.Type> {

	SourceTypeColumn(String name) {
		super(name);
	}

	@Override
	public @Nullable TableCellRenderer getRenderer(T settings) {
		return new ColoredTableCellRenderer() {
			@Override
			protected void customizeCellRenderer(JTable table, Object value,
												 boolean isSelected, boolean hasFocus, int row, int column) {
				if (value == null) {
					return;
				}

				if (value.equals(Type.NETTE)) {
					append("Nette", new SimpleTextAttributes(Font.PLAIN, null));
				} else {
					append("Custom", new SimpleTextAttributes(Font.PLAIN, JBColor.ORANGE));
				}
			}
		};
	}

	public enum Type {
		NETTE,
		CUSTOM,
	}
}
