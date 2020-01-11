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
import com.jantvrdik.intellij.latte.settings.LatteCustomFunctionSettings;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.utils.LatteIdeHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class LatteCustomFunctionSettingsForm implements Configurable {
	private JPanel panel1;
	private JPanel panelConfigTableView;
	private JCheckBox enableCustomFunctionsCheckBox;
	private JButton buttonHelp;
	private JButton resetToDefaultsButton;

	private TableView<LatteCustomFunctionSettings> tableView;
	private Project project;
	private boolean changed = false;
	private ListTableModel<LatteCustomFunctionSettings> modelList;

	public LatteCustomFunctionSettingsForm(Project project) {
		this.project = project;

		this.tableView = new TableView<LatteCustomFunctionSettings>();
		this.modelList = new ListTableModel<LatteCustomFunctionSettings>(
				new NameColumn(),
				new ReturnTypeColumn(),
				new HelpColumn()
		);

		this.attachItems();

		this.tableView.setModelAndUpdateColumns(this.modelList);
		this.tableView.getModel().addTableModelListener(e -> LatteCustomFunctionSettingsForm.this.changed = true);

		buttonHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteIdeHelper.openUrl(LatteConfiguration.FORUM_URL + "en/32885-latte-version-2-6-0-released");
			}
		});

		resetToDefaultsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				resetToDefaults();
			}
		});

		enableCustomFunctionsCheckBox.setSelected(getSettings().enableDefaultVariables);

		enableCustomFunctionsCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteCustomFunctionSettingsForm.this.changed = true;
			}
		});
	}

	private void attachItems() {

		if(this.getSettings().customMacroSettings == null) {
			return;
		}

		for (LatteCustomFunctionSettings customMacroSettings : this.getSettings().customFunctionSettings) {
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
		ToolbarDecorator tablePanel = ToolbarDecorator.createDecorator(this.tableView, new ElementProducer<LatteCustomFunctionSettings>() {
			@Override
			public LatteCustomFunctionSettings createElement() {
				//IdeFocusManager.getInstance(TwigSettingsForm.this.project).requestFocus(TwigNamespaceDialog.getWindows(), true);
				return null;
			}

			@Override
			public boolean canCreateElement() {
				return true;
			}
		});

		tablePanel.setEditAction(anActionButton ->
				LatteCustomFunctionSettingsForm.this.openFunctionDialog(LatteCustomFunctionSettingsForm.this.tableView.getSelectedObject())
		);

		tablePanel.setAddAction(anActionButton ->
				LatteCustomFunctionSettingsForm.this.openFunctionDialog(null)
		);

		tablePanel.disableUpAction();
		tablePanel.disableDownAction();

		this.panelConfigTableView.add(tablePanel.createPanel());

		return this.panel1;
	}

	private void attachDefaultVariables() {
		for (LatteCustomFunctionSettings customFunctionSettings : DefaultSettings.getDefaultCustomFunctions()) {
			this.modelList.addRow(customFunctionSettings);
		}
	}

	@Override
	public boolean isModified() {
		return this.changed;
	}

	@Override
	public void apply() throws ConfigurationException {
		getSettings().customFunctionSettings = new ArrayList<>(this.tableView.getListTableModel().getItems());
		getSettings().enableCustomFunctions = enableCustomFunctionsCheckBox.isSelected();

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

	private class NameColumn extends ColumnInfo<LatteCustomFunctionSettings, String> {

		public NameColumn() {
			super("Name");
		}

		@Nullable
		@Override
		public String valueOf(LatteCustomFunctionSettings functionSettings) {
			return functionSettings.getFunctionName();
		}
	}

	private class ReturnTypeColumn extends PhpTypeColumn<LatteCustomFunctionSettings> {

		public ReturnTypeColumn() {
			super("ReturnType", project);
		}

		@Nullable
		@Override
		public String valueOf(LatteCustomFunctionSettings functionSettings) {
			return functionSettings.getFunctionReturnType();
		}
	}
	private class HelpColumn extends ColumnInfo<LatteCustomFunctionSettings, String> {


		public HelpColumn() {
			super("Help");
		}
		@Nullable
		@Override
		public String valueOf(LatteCustomFunctionSettings functionSettings) {
			return functionSettings.getFunctionHelp();
		}

	}

	private void openFunctionDialog(@Nullable LatteCustomFunctionSettings customMacroSettings) {
		LatteCustomFunctionSettingsDialog latteVariableDialog;
		if(customMacroSettings == null) {
			latteVariableDialog = new LatteCustomFunctionSettingsDialog(project, this.tableView);
		} else {
			latteVariableDialog = new LatteCustomFunctionSettingsDialog(project, this.tableView, customMacroSettings);
		}

		Dimension dim = new Dimension();
		dim.setSize(500, 130);
		latteVariableDialog.setTitle("LatteCustomFunctionSettings");
		latteVariableDialog.setMinimumSize(dim);
		latteVariableDialog.pack();
		latteVariableDialog.setLocationRelativeTo(this.panel1);

		latteVariableDialog.setVisible(true);
	}
}