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
import com.jantvrdik.intellij.latte.settings.LatteCustomMacroSettings;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.utils.LatteIdeHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LatteCustomMacroSettingsForm implements Configurable {
	private JPanel panel1;
	private JPanel panelConfigTableView;
	private JCheckBox enableCustomMacrosCheckBox;
	private JButton buttonHelp;
	private JButton resetToDefaultsButton;

	private TableView<LatteCustomMacroSettings> tableView;
	private Project project;
	private boolean changed = false;
	private ListTableModel<LatteCustomMacroSettings> modelList;

	public LatteCustomMacroSettingsForm(Project project) {
		this.project = project;

		this.tableView = new TableView<LatteCustomMacroSettings>();
		this.modelList = new ListTableModel<LatteCustomMacroSettings>(
				new MacroNameColumn(),
				new TypeColumn(),
				new AllowedModifiersColumn(),
				new HasParametersColumn(),
				new SourceColumn()
		);

		this.attachItems();

		this.tableView.setModelAndUpdateColumns(this.modelList);
		this.tableView.getModel().addTableModelListener(e -> LatteCustomMacroSettingsForm.this.changed = true);

		buttonHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteIdeHelper.openUrl(LatteConfiguration.LATTE_HELP_URL + "en/tags");
			}
		});

		resetToDefaultsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				resetToDefaults();
			}
		});

		enableCustomMacrosCheckBox.setSelected(getSettings().enableDefaultVariables);

		enableCustomMacrosCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteCustomMacroSettingsForm.this.changed = true;
			}
		});
	}

	private void attachItems() {

		if(this.getSettings().customMacroSettings == null) {
			return;
		}

		for (LatteCustomMacroSettings customMacroSettings : this.getSettings().customMacroSettings) {
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
		ToolbarDecorator tablePanel = ToolbarDecorator.createDecorator(this.tableView, new ElementProducer<LatteCustomMacroSettings>() {
			@Override
			public LatteCustomMacroSettings createElement() {
				//IdeFocusManager.getInstance(TwigSettingsForm.this.project).requestFocus(TwigNamespaceDialog.getWindows(), true);
				return null;
			}

			@Override
			public boolean canCreateElement() {
				return true;
			}
		});

		tablePanel.setEditAction(anActionButton ->
				LatteCustomMacroSettingsForm.this.openMacroDialog(LatteCustomMacroSettingsForm.this.tableView.getSelectedObject())
		);

		tablePanel.setAddAction(anActionButton ->
				LatteCustomMacroSettingsForm.this.openMacroDialog(null)
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
		getSettings().customMacroSettings = new ArrayList<>(this.tableView.getListTableModel().getItems());
		getSettings().enableCustomMacros = enableCustomMacrosCheckBox.isSelected();

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
		List<String> foundMacros = new ArrayList<String>();
		List<LatteCustomMacroSettings> newSettings = new ArrayList<LatteCustomMacroSettings>();
		for (LatteCustomMacroSettings macroSettings : this.modelList.getItems()) {
			LatteCustomMacroSettings defaultMacro = DefaultSettings.getDefaultMacro(macroSettings.getMacroName());
			if (defaultMacro != null) {
				newSettings.add(defaultMacro);
				foundMacros.add(macroSettings.getMacroName());
			} else {
				newSettings.add(macroSettings);
			}
		}

		for (LatteCustomMacroSettings customMacroSettings : DefaultSettings.getDefaultMacros()) {
			if (!foundMacros.contains(customMacroSettings.getMacroName())) {
				newSettings.add(customMacroSettings);
			}
		}

		this.resetList();

		for (LatteCustomMacroSettings customMacroSettings : newSettings) {
			this.modelList.addRow(customMacroSettings);
		}
	}

	@Override
	public void disposeUIResources() {

	}

	private static class MacroNameColumn extends ColumnInfo<LatteCustomMacroSettings, String> {

		public MacroNameColumn() {
			super("Name");
		}

		@Nullable
		@Override
		public String valueOf(LatteCustomMacroSettings customMacroSettings) {
			return customMacroSettings.getMacroName();
		}
	}
	private static class TypeColumn extends ColumnInfo<LatteCustomMacroSettings, String> {

		public TypeColumn() {
			super("Type");
		}

		@Nullable
		@Override
		public String valueOf(LatteCustomMacroSettings latteVariableSettings) {
			return latteVariableSettings.getMacroType();
		}
	}

	private static class AllowedModifiersColumn extends ColumnInfo<LatteCustomMacroSettings, String> {

		public AllowedModifiersColumn() {
			super("Allowed modifiers");
		}

		@Nullable
		@Override
		public String valueOf(LatteCustomMacroSettings customMacroSettings) {
			return customMacroSettings.isAllowedModifiers() ? "yes" : "no";
		}
	}

	private static class HasParametersColumn extends ColumnInfo<LatteCustomMacroSettings, String> {

		public HasParametersColumn() {
			super("Has parameters");
		}

		@Nullable
		@Override
		public String valueOf(LatteCustomMacroSettings customMacroSettings) {
			return customMacroSettings.hasParameters() ? "yes" : "no";
		}
	}

	private static class SourceColumn extends SourceTypeColumn<LatteCustomMacroSettings> {

		public SourceColumn() {
			super("Source");
		}

		@Nullable
		@Override
		public Type valueOf(LatteCustomMacroSettings customMacroSettings) {
			return DefaultSettings.isDefaultMacro(customMacroSettings.getMacroName()) ? Type.NETTE : Type.CUSTOM;
		}
	}

	private void openMacroDialog(@Nullable LatteCustomMacroSettings customMacroSettings) {
		LatteCustomMacroSettingsDialog latteVariableDialog;
		if(customMacroSettings == null) {
			latteVariableDialog = new LatteCustomMacroSettingsDialog(project, this.tableView);
		} else {
			latteVariableDialog = new LatteCustomMacroSettingsDialog(project, this.tableView, customMacroSettings);
		}

		Dimension dim = new Dimension();
		dim.setSize(500, 130);
		latteVariableDialog.setTitle("LatteCustomTagSettings");
		latteVariableDialog.setMinimumSize(dim);
		latteVariableDialog.pack();
		latteVariableDialog.setLocationRelativeTo(this.panel1);

		latteVariableDialog.setVisible(true);
	}
}