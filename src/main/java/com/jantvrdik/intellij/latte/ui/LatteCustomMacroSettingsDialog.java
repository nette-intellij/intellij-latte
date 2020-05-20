package com.jantvrdik.intellij.latte.ui;

import com.intellij.ui.table.TableView;
import com.jantvrdik.intellij.latte.settings.LatteCustomMacroSettings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

public class LatteCustomMacroSettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textVarName;
    private JComboBox<String> macroType;
    private JCheckBox checkBoxAllowedModifiers;
    private JCheckBox checkBosHasParameters;
    private JCheckBox multiLineOnlyUsedCheckBox;
    private JCheckBox deprecatedCheckBox;
    private JTextField deprecatedMessageTextField;
    private LatteCustomMacroSettings latteCustomMacroSettings;
    private TableView<LatteCustomMacroSettings> tableView;

    public LatteCustomMacroSettingsDialog(TableView<LatteCustomMacroSettings> tableView) {
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

        this.deprecatedCheckBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                deprecatedMessageTextField.setEnabled(deprecatedCheckBox.isSelected());
            }
        });
        deprecatedMessageTextField.setEnabled(false);
    }

    public LatteCustomMacroSettingsDialog(TableView<LatteCustomMacroSettings> tableView, LatteCustomMacroSettings latteCustomMacroSettings) {
        this(tableView);

        this.textVarName.setText(latteCustomMacroSettings.getMacroName());
        this.macroType.getModel().setSelectedItem(latteCustomMacroSettings.getMacroType());
        this.latteCustomMacroSettings = latteCustomMacroSettings;
        this.checkBoxAllowedModifiers.setSelected(latteCustomMacroSettings.isAllowedModifiers());
        this.checkBosHasParameters.setSelected(latteCustomMacroSettings.hasParameters());
        this.multiLineOnlyUsedCheckBox.setSelected(latteCustomMacroSettings.isMultiLine());
        this.deprecatedCheckBox.setSelected(latteCustomMacroSettings.isDeprecated());
        this.deprecatedMessageTextField.setText(latteCustomMacroSettings.getDeprecatedMessage());

        deprecatedMessageTextField.setEnabled(latteCustomMacroSettings.isDeprecated());

        attachComboBoxValues();
    }

    private void attachComboBoxValues() {
        LatteCustomMacroSettings.Type[] values = LatteCustomMacroSettings.Type.values();
        if (macroType.getItemCount() == values.length) {
            return;
        }

        for(LatteCustomMacroSettings.Type type: values) {
            macroType.addItem(type.toString());
        }
    }

    private void onOK() {
        LatteCustomMacroSettings settings = new LatteCustomMacroSettings(this.textVarName.getText(), LatteCustomMacroSettings.Type.valueOf((String) this.macroType.getSelectedItem()));

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
        settings.setMultiLine(this.multiLineOnlyUsedCheckBox.isSelected());
        settings.setDeprecated(this.deprecatedCheckBox.isSelected());
        settings.setDeprecatedMessage(this.deprecatedMessageTextField.getText());
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
