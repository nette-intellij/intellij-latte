package com.jantvrdik.intellij.latte.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ElementProducer;
import com.intellij.util.ui.ListTableModel;
import com.jantvrdik.intellij.latte.settings.LatteCustomModifierSettings;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
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

	private TableView<LatteCustomModifierSettings> tableView;
	private Project project;
	private boolean changed = false;
	private ListTableModel<LatteCustomModifierSettings> modelList;

	public LatteCustomModifierSettingsForm(Project project) {
		this.project = project;

		this.tableView = new TableView<LatteCustomModifierSettings>();
		this.modelList = new ListTableModel<LatteCustomModifierSettings>(
				new NameColumn(),
				new HelpColumn(),
				new DescriptionColumn()
		);

		this.attachItems();

		this.tableView.setModelAndUpdateColumns(this.modelList);
		this.tableView.getModel().addTableModelListener(e -> LatteCustomModifierSettingsForm.this.changed = true);

		buttonHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				//todo: add url to docs
				//IdeHelper.openUrl(Symfony2ProjectComponent.HELP_URL + "extension/signature_type.html");
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

		if(this.getSettings().customMacroSettings == null) {
			return;
		}

		for (LatteCustomModifierSettings customMacroSettings : this.getSettings().customModifierSettings) {
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
		ToolbarDecorator tablePanel = ToolbarDecorator.createDecorator(this.tableView, new ElementProducer<LatteCustomModifierSettings>() {
			@Override
			public LatteCustomModifierSettings createElement() {
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
		getSettings().customModifierSettings = new ArrayList<>(this.tableView.getListTableModel().getItems());
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

	private class NameColumn extends ColumnInfo<LatteCustomModifierSettings, String> {

		public NameColumn() {
			super("Name");
		}

		@Nullable
		@Override
		public String valueOf(LatteCustomModifierSettings modifierSettings) {
			return modifierSettings.getModifierName();
		}
	}

	private class HelpColumn extends ColumnInfo<LatteCustomModifierSettings, String> {

		public HelpColumn() {
			super("Help");
		}

		@Nullable
		@Override
		public String valueOf(LatteCustomModifierSettings modifierSettings) {
			return modifierSettings.getModifierHelp();
		}
	}

	private class DescriptionColumn extends ColumnInfo<LatteCustomModifierSettings, String> {

		public DescriptionColumn() {
			super("Description");
		}

		@Nullable
		@Override
		public String valueOf(LatteCustomModifierSettings modifierSettings) {
			return modifierSettings.getModifierDescription();
		}
	}

	private void openModifierDialog(@Nullable LatteCustomModifierSettings customMacroSettings) {
		LatteCustomModifierSettingsDialog latteVariableDialog;
		if(customMacroSettings == null) {
			latteVariableDialog = new LatteCustomModifierSettingsDialog(project, this.tableView);
		} else {
			latteVariableDialog = new LatteCustomModifierSettingsDialog(project, this.tableView, customMacroSettings);
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