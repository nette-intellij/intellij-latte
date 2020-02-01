package com.jantvrdik.intellij.latte.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.table.TableView;
import com.jantvrdik.intellij.latte.config.LatteMacro;
import com.jantvrdik.intellij.latte.settings.LatteCustomMacroSettings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class LatteCustomMacroSettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textVarName;
    private JComboBox<String> macroType;
    private JCheckBox checkBoxAllowedModifiers;
    private JCheckBox checkBosHasParameters;
    private LatteCustomMacroSettings latteCustomMacroSettings;
    private TableView<LatteCustomMacroSettings> tableView;

    public LatteCustomMacroSettingsDialog(Project project, TableView<LatteCustomMacroSettings> tableView) {
        this.tableView = tableView;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        this.textVarName.getDocument().addDocumentListener(new ChangeDocumentListener());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        attachComboBoxValues();
    }

    public LatteCustomMacroSettingsDialog(Project project, TableView<LatteCustomMacroSettings> tableView, LatteCustomMacroSettings latteCustomMacroSettings) {
        this(project, tableView);

        this.textVarName.setText(latteCustomMacroSettings.getMacroName());
        this.macroType.getModel().setSelectedItem(latteCustomMacroSettings.getMacroType());
        this.latteCustomMacroSettings = latteCustomMacroSettings;
        this.checkBoxAllowedModifiers.setSelected(latteCustomMacroSettings.isAllowedModifiers());
        this.checkBosHasParameters.setSelected(latteCustomMacroSettings.hasParameters());

        attachComboBoxValues();
    }

    private void attachComboBoxValues() {
        LatteMacro.Type[] values = LatteMacro.Type.values();
        if (macroType.getItemCount() == values.length) {
            return;
        }

        for(LatteMacro.Type type: values) {
            macroType.addItem(type.toString());
        }
    }

    private void onOK() {
        LatteCustomMacroSettings settings = new LatteCustomMacroSettings(this.textVarName.getText(), LatteMacro.Type.valueOf((String) this.macroType.getSelectedItem()));

        if(this.latteCustomMacroSettings != null) {
            int row = this.tableView.getSelectedRows()[0];
            this.tableView.getListTableModel().removeRow(row);
            this.tableView.getListTableModel().insertRow(row, settings);
            this.tableView.setRowSelectionInterval(row, row);
        } else {
            int row = this.tableView.getRowCount();
            this.tableView.getListTableModel().addRow(settings);
            this.tableView.setRowSelectionInterval(row, row);
        }

        settings.setAllowedModifiers(this.checkBoxAllowedModifiers.isSelected());
        settings.setHasParameters(this.checkBosHasParameters.isSelected());
        dispose();
    }

    private void setOkState() {
        this.buttonOK.setEnabled(
            this.textVarName.getText().length() > 0
        );
    }

    private class ChangeDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            setOkState();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            setOkState();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            setOkState();
        }
    }

    private void onCancel() {
        dispose();
    }

}
