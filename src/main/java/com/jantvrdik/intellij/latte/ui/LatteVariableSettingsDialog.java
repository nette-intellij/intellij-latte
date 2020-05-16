package com.jantvrdik.intellij.latte.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.table.TableView;
import com.jantvrdik.intellij.latte.settings.LatteVariableSettings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Daniel Espendiller <daniel@espendiller.net>
 */
public class LatteVariableSettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textVarName;
    private JTextField textVarType;
    private LatteVariableSettings latteVariableSettings;
    private TableView<LatteVariableSettings> tableView;

    public LatteVariableSettingsDialog(Project project, TableView<LatteVariableSettings> tableView) {
        this.tableView = tableView;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        this.textVarName.getDocument().addDocumentListener(new ChangeDocumentListener());
        this.textVarType.getDocument().addDocumentListener(new ChangeDocumentListener());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public LatteVariableSettingsDialog(Project project, TableView<LatteVariableSettings> tableView, LatteVariableSettings latteVariableSettings) {
        this(project, tableView);

        this.textVarName.setText(latteVariableSettings.getVarName());
        this.textVarType.setText(latteVariableSettings.getVarType());
        this.latteVariableSettings = latteVariableSettings;

    }

    private void onOK() {
        LatteVariableSettings settings = new LatteVariableSettings(this.textVarName.getText(), this.textVarType.getText());

        // re-add old item to not use public setter wor twigpaths
        // update ?
        if(this.latteVariableSettings != null) {
            int row = this.tableView.getSelectedRows()[0];
            this.tableView.getListTableModel().removeRow(row);
            this.tableView.getListTableModel().insertRow(row, settings);
            this.tableView.setRowSelectionInterval(row, row);
        } else {
            int row = this.tableView.getRowCount();
            this.tableView.getListTableModel().addRow(settings);
            this.tableView.setRowSelectionInterval(row, row);
        }

        dispose();
    }

    private void setOkState() {
        this.buttonOK.setEnabled(
            this.textVarName.getText().length() > 0 &&
            this.textVarType.getText().length() > 0
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
