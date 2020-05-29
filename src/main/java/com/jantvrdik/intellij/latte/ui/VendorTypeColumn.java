package com.jantvrdik.intellij.latte.ui;

import com.intellij.ui.ColoredTableCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.ColumnInfo;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteFileConfiguration;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;

abstract class VendorTypeColumn<T> extends ColumnInfo<T, LatteFileConfiguration.VendorResult> {

	VendorTypeColumn(String name) {
		super(name);
	}

	@Override
	public @Nullable TableCellRenderer getRenderer(T settings) {
		return new ColoredTableCellRenderer() {
			@Override
			protected void customizeCellRenderer(JTable table, Object value,
												 boolean isSelected, boolean hasFocus, int row, int column) {
				if (!(value instanceof LatteFileConfiguration.VendorResult)) {
					return;
				}

				LatteFileConfiguration.VendorResult vendor = (LatteFileConfiguration.VendorResult) value;
				if (vendor.vendor == LatteConfiguration.Vendor.OTHER) {
					append(vendor.vendorName, new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, vendor.vendor.getColor()));
				} else {
					append(vendor.vendor.getName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, vendor.vendor.getColor()));
				}
			}
		};
	}
}
