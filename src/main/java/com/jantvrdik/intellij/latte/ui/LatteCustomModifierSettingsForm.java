package com.jantvrdik.intellij.latte.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ElementProducer;
import com.intellij.util.ui.ListTableModel;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteFileConfiguration;
import com.jantvrdik.intellij.latte.settings.LatteFilterSettings;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;
import com.jantvrdik.intellij.latte.utils.LatteIdeHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class LatteCustomModifierSettingsForm implements Configurable {
	private JPanel panel1;
	private JPanel panelConfigTableView;
	private JCheckBox enableCustomModifiersCheckBox;
	private JButton buttonHelp;

	private TableView<LatteFilterSettings> tableView;
	private Project project;
	private boolean changed = false;
	private ListTableModel<LatteFilterSettings> modelList;

	public LatteCustomModifierSettingsForm(Project project) {
		this.project = project;

		this.tableView = new TableView<>();
		this.modelList = new ListTableModel<>(
				new NameColumn(),
				new HelpColumn(),
				new DescriptionColumn(),
				new InsertColonColumn(),
				new VendorColumn()
		);

		this.attachItems();

		this.tableView.setModelAndUpdateColumns(this.modelList);
		this.tableView.getModel().addTableModelListener(e -> LatteCustomModifierSettingsForm.this.changed = true);

		buttonHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteIdeHelper.openUrl(LatteConfiguration.LATTE_HELP_URL + "en/filters");
			}
		});

		enableCustomModifiersCheckBox.setSelected(getSettings().enableDefaultVariables);

		enableCustomModifiersCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteCustomModifierSettingsForm.this.changed = true;
			}
		});
	}

	private void attachItems() {

		if(this.getSettings().tagSettings == null) {
			return;
		}

		for (LatteFilterSettings customMacroSettings : this.getSettings().filterSettings) {
			this.modelList.addRow(customMacroSettings);
		}
	}

	@Nls
	@Override
	public String getDisplayName() {
		return null;
	}

	@Nullable
	@Override
	public String getHelpTopic() {
		return null;
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		ToolbarDecorator tablePanel = ToolbarDecorator.createDecorator(this.tableView, new ElementProducer<LatteFilterSettings>() {
			@Override
			public LatteFilterSettings createElement() {
				//IdeFocusManager.getInstance(TwigSettingsForm.this.project).requestFocus(TwigNamespaceDialog.getWindows(), true);
				return null;
			}

			@Override
			public boolean canCreateElement() {
				return true;
			}
		});

		tablePanel.setEditAction(anActionButton ->
				LatteCustomModifierSettingsForm.this.openModifierDialog(LatteCustomModifierSettingsForm.this.tableView.getSelectedObject())
		);

		tablePanel.setAddAction(anActionButton ->
				LatteCustomModifierSettingsForm.this.openModifierDialog(null)
		);

		tablePanel.disableUpAction();
		tablePanel.disableDownAction();

		this.panelConfigTableView.add(tablePanel.createPanel());

		return this.panel1;
	}

	@Override
	public boolean isModified() {
		return this.changed;
	}

	@Override
	public void apply() throws ConfigurationException {
		getSettings().filterSettings = new ArrayList<>(this.tableView.getListTableModel().getItems());
		getSettings().enableCustomModifiers = enableCustomModifiersCheckBox.isSelected();

		this.changed = false;
	}

	private LatteSettings getSettings() {
		return LatteSettings.getInstance(this.project);
	}

	private void resetList() {
		// clear list, easier?
		while(this.modelList.getRowCount() > 0) {
			this.modelList.removeRow(0);
		}

	}

	@Override
	public void reset() {
		this.resetList();
		this.attachItems();
		this.changed = false;
	}

	@Override
	public void disposeUIResources() {

	}

	private static class NameColumn extends ColumnInfo<LatteFilterSettings, String> {

		public NameColumn() {
			super("Name");
		}

		@Nullable
		@Override
		public String valueOf(LatteFilterSettings modifierSettings) {
			return modifierSettings.getModifierName();
		}
	}

	private static class HelpColumn extends ColumnInfo<LatteFilterSettings, String> {

		public HelpColumn() {
			super("Arguments");
		}

		@Nullable
		@Override
		public String valueOf(LatteFilterSettings modifierSettings) {
			return modifierSettings.getModifierHelp();
		}
	}

	private static class DescriptionColumn extends ColumnInfo<LatteFilterSettings, String> {

		public DescriptionColumn() {
			super("Description");
		}

		@Nullable
		@Override
		public String valueOf(LatteFilterSettings modifierSettings) {
			return modifierSettings.getModifierDescription();
		}
	}

	private static class InsertColonColumn extends ColumnInfo<LatteFilterSettings, String> {

		public InsertColonColumn() {
			super("Insert color");
		}

		@Nullable
		@Override
		public String valueOf(LatteFilterSettings modifierSettings) {
			return modifierSettings.getModifierInsert();
		}
	}

	private class VendorColumn extends VendorTypeColumn<LatteFilterSettings> {

		public VendorColumn() {
			super("Vendor");
		}

		@Nullable
		@Override
		public LatteFileConfiguration.VendorResult valueOf(LatteFilterSettings customMacroSettings) {
			return LatteConfiguration.getInstance(project).getVendorForFilter(customMacroSettings.getModifierName());
		}
	}

	private void openModifierDialog(@Nullable LatteFilterSettings customMacroSettings) {
		LatteCustomModifierSettingsDialog latteVariableDialog;
		if(customMacroSettings == null) {
			latteVariableDialog = new LatteCustomModifierSettingsDialog(this.tableView, project);
		} else {
			latteVariableDialog = new LatteCustomModifierSettingsDialog(this.tableView, project, customMacroSettings);
		}

		Dimension dim = new Dimension();
		dim.setSize(500, 130);
		latteVariableDialog.setTitle("LatteCustomModifierSettings");
		latteVariableDialog.setMinimumSize(dim);
		latteVariableDialog.pack();
		latteVariableDialog.setLocationRelativeTo(this.panel1);

		latteVariableDialog.setVisible(true);
	}
}