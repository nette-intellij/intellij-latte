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
import com.jantvrdik.intellij.latte.settings.DefaultSettings;
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

public class LatteVariableSettingsForm implements Configurable {
	private JPanel panel1;
	private JPanel panelConfigTableView;
	private JCheckBox enableCustomSignatureTypesCheckBox;
	private JButton buttonHelp;
	private JButton resetToDefaultsButton;

	private TableView<LatteVariableSettings> tableView;
	private Project project;
	private boolean changed = false;
	private ListTableModel<LatteVariableSettings> modelList;

	public LatteVariableSettingsForm(Project project) {
		this.project = project;

		this.tableView = new TableView<LatteVariableSettings>();
		this.modelList = new ListTableModel<LatteVariableSettings>(
				new VarNameColumn(),
				new VarTypeColumn(),
				new SourceColumn()
		);

		this.attachItems();

		this.tableView.setModelAndUpdateColumns(this.modelList);
		this.tableView.getModel().addTableModelListener(e -> LatteVariableSettingsForm.this.changed = true);

		buttonHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteIdeHelper.openUrl(LatteConfiguration.LATTE_HELP_URL + "en/guide");
			}
		});

		resetToDefaultsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				resetToDefaults();
			}
		});

		enableCustomSignatureTypesCheckBox.setSelected(getSettings().enableDefaultVariables);

		enableCustomSignatureTypesCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteVariableSettingsForm.this.changed = true;
			}
		});
	}

	private void attachItems() {

		if(this.getSettings().variableSettings == null) {
			return;
		}

		for (LatteVariableSettings methodParameterSetting : this.getSettings().variableSettings) {
			this.modelList.addRow(methodParameterSetting);
		}
	}

	private void attachDefaultVariables() {
		for (LatteVariableSettings variableSettings : DefaultSettings.getDefaultVariables()) {
			this.modelList.addRow(variableSettings);
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
		ToolbarDecorator tablePanel = ToolbarDecorator.createDecorator(this.tableView, new ElementProducer<LatteVariableSettings>() {
			@Override
			public LatteVariableSettings createElement() {
				//IdeFocusManager.getInstance(TwigSettingsForm.this.project).requestFocus(TwigNamespaceDialog.getWindows(), true);
				return null;
			}

			@Override
			public boolean canCreateElement() {
				return true;
			}
		});

		tablePanel.setEditAction(anActionButton ->
				LatteVariableSettingsForm.this.openVariablePathDialog(LatteVariableSettingsForm.this.tableView.getSelectedObject())
		);

		tablePanel.setAddAction(anActionButton ->
				LatteVariableSettingsForm.this.openVariablePathDialog(null)
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
		getSettings().variableSettings = new ArrayList<>(this.tableView.getListTableModel().getItems());
		getSettings().enableDefaultVariables = enableCustomSignatureTypesCheckBox.isSelected();

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

	public void resetToDefaults() {
		this.resetList();
		this.attachDefaultVariables();
	}

	@Override
	public void disposeUIResources() {

	}

	private static class VarNameColumn extends ColumnInfo<LatteVariableSettings, String> {

		public VarNameColumn() {
			super("Name");
		}

		@Nullable
		@Override
		public String valueOf(LatteVariableSettings methodParameterSetting) {
			return methodParameterSetting.getVarName();
		}
	}

	private class VarTypeColumn extends PhpTypeColumn<LatteVariableSettings> {

		public VarTypeColumn() {
			super("Type", project);
		}

		@Nullable
		@Override
		public String valueOf(LatteVariableSettings latteVariableSettings) {
			return latteVariableSettings.getVarType();
		}
	}

	private static class SourceColumn extends SourceTypeColumn<LatteVariableSettings> {

		public SourceColumn() {
			super("Source");
		}

		@Nullable
		@Override
		public Type valueOf(LatteVariableSettings methodParameterSetting) {
			return DefaultSettings.isDefaultVariable(methodParameterSetting.getVarName()) ? Type.NETTE : Type.CUSTOM;
		}
	}

	private void openVariablePathDialog(@Nullable LatteVariableSettings variableSettings) {
		LatteVariableSettingsDialog latteVariableDialog;
		if(variableSettings == null) {
			latteVariableDialog = new LatteVariableSettingsDialog(project, this.tableView);
		} else {
			latteVariableDialog = new LatteVariableSettingsDialog(project, this.tableView, variableSettings);
		}

		Dimension dim = new Dimension();
		dim.setSize(500, 160);
		latteVariableDialog.setTitle("LatteVariableSettings");
		latteVariableDialog.setMinimumSize(dim);
		latteVariableDialog.pack();
		latteVariableDialog.setLocationRelativeTo(this.panel1);

		latteVariableDialog.setVisible(true);
	}
}