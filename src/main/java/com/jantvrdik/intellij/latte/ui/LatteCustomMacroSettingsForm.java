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
import com.jantvrdik.intellij.latte.settings.LatteTagSettings;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.utils.LatteIdeHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class LatteCustomMacroSettingsForm implements Configurable {
	private JPanel panel1;
	private JPanel panelConfigTableView;
	private JCheckBox enableCustomMacrosCheckBox;
	private JButton buttonHelp;

	private TableView<LatteTagSettings> tableView;
	private Project project;
	private boolean changed = false;
	private ListTableModel<LatteTagSettings> modelList;

	public LatteCustomMacroSettingsForm(Project project) {
		this.project = project;

		this.tableView = new TableView<>();
		this.modelList = new ListTableModel<>(
				new MacroNameColumn(),
				new TypeColumn(),
				new AllowedModifiersColumn(),
				new ArgumentsColumn(),
				new IsMultiLineColumn(),
				new IsDeprecatedColumn(),
				new VendorColumn()
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

		if(this.getSettings().tagSettings == null) {
			return;
		}

		for (LatteTagSettings customMacroSettings : this.getSettings().tagSettings) {
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
		ToolbarDecorator tablePanel = ToolbarDecorator.createDecorator(this.tableView, new ElementProducer<LatteTagSettings>() {
			@Override
			public LatteTagSettings createElement() {
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
		getSettings().tagSettings = new ArrayList<>(this.tableView.getListTableModel().getItems());
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

	@Override
	public void disposeUIResources() {

	}

	private static class MacroNameColumn extends ColumnInfo<LatteTagSettings, String> {

		public MacroNameColumn() {
			super("Name");
		}

		@Nullable
		@Override
		public String valueOf(LatteTagSettings customMacroSettings) {
			return customMacroSettings.getMacroName();
		}
	}
	private static class TypeColumn extends ColumnInfo<LatteTagSettings, String> {

		public TypeColumn() {
			super("Type");
		}

		@Nullable
		@Override
		public String valueOf(LatteTagSettings latteVariableSettings) {
			return latteVariableSettings.getMacroType();
		}
	}

	private static class AllowedModifiersColumn extends ColumnInfo<LatteTagSettings, String> {

		public AllowedModifiersColumn() {
			super("Allowed modifiers");
		}

		@Nullable
		@Override
		public String valueOf(LatteTagSettings customMacroSettings) {
			return customMacroSettings.isAllowedModifiers() ? "yes" : "no";
		}
	}

	private static class ArgumentsColumn extends ColumnInfo<LatteTagSettings, String> {

		public ArgumentsColumn() {
			super("Arguments");
		}

		@Nullable
		@Override
		public String valueOf(LatteTagSettings customMacroSettings) {
			return customMacroSettings.getArguments();
		}
	}

	private static class IsMultiLineColumn extends ColumnInfo<LatteTagSettings, String> {

		public IsMultiLineColumn() {
			super("Multi line");
		}

		@Nullable
		@Override
		public String valueOf(LatteTagSettings customMacroSettings) {
			return customMacroSettings.isMultiLine() ? "yes" : "no";
		}
	}

	private static class IsDeprecatedColumn extends ColumnInfo<LatteTagSettings, String> {

		public IsDeprecatedColumn() {
			super("Deprecated");
		}

		@Nullable
		@Override
		public String valueOf(LatteTagSettings customMacroSettings) {
			return customMacroSettings.isDeprecated() ? "yes" : "no";
		}
	}

	private class VendorColumn extends VendorTypeColumn<LatteTagSettings> {

		public VendorColumn() {
			super("Vendor");
		}

		@Nullable
		@Override
		public LatteFileConfiguration.VendorResult valueOf(LatteTagSettings customMacroSettings) {
			return LatteConfiguration.getInstance(project).getVendorForTag(customMacroSettings.getMacroName());
		}
	}

	private void openMacroDialog(@Nullable LatteTagSettings customMacroSettings) {
		LatteCustomMacroSettingsDialog latteVariableDialog;
		if(customMacroSettings == null) {
			latteVariableDialog = new LatteCustomMacroSettingsDialog(this.tableView, project);
		} else {
			latteVariableDialog = new LatteCustomMacroSettingsDialog(this.tableView, project, customMacroSettings);
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