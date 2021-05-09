package com.jantvrdik.intellij.latte.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.ColumnInfo;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

abstract class PhpTypeColumn<T> extends ColumnInfo<T, String> {

	private final Project project;

	PhpTypeColumn(String name, Project project) {
		super(name);
		this.project = project;
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

				NettePhpType type = NettePhpType.create((String) value);
				if (type.hasUndefinedClass(project)) {
					append((String) value, new SimpleTextAttributes(Font.PLAIN, JBColor.RED));
				} else {
					append((String) value, new SimpleTextAttributes(Font.PLAIN, null));
				}
			}
		};
	}

	@Override
	public @Nullable String getTooltipText() {
		return "If you see a red font, some class is probably undefined.";
	}
}
