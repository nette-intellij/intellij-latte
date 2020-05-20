package com.jantvrdik.intellij.latte.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.table.TableView;
import com.jantvrdik.intellij.latte.settings.LatteCustomFunctionSettings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LatteCustomFunctionSettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textName;
    private JTextField textHelp;
    private JTextField textReturnType;
    private JTextArea textDescription;
    private LatteCustomFunctionSettings latteCustomFunctionSettings;
    private TableView<LatteCustomFunctionSettings> tableView;

    public LatteCustomFunctionSettingsDialog(Project project, TableView<LatteCustomFunctionSettings> tableView) {
        this.tableView = tableView;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        this.textName.getDocument().addDocumentListener(new ChangeDocumentListener());
        this.textReturnType.getDocument().addDocumentListener(new ChangeDocumentListener());
        this.textHelp.getDocument().addDocumentListener(new ChangeDocumentListener());
        this.textDescription.getDocument().addDocumentListener(new ChangeDocumentListener());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public LatteCustomFunctionSettingsDialog(Project project, TableView<LatteCustomFunctionSettings> tableView, LatteCustomFunctionSettings latteCustomFunctionSettings) {
        this(project, tableView);

        this.textName.setText(latteCustomFunctionSettings.getFunctionName());
        this.textReturnType.setText(latteCustomFunctionSettings.getFunctionReturnType());
        this.textHelp.setText(latteCustomFunctionSettings.getFunctionHelp());
        this.textDescription.setText(latteCustomFunctionSettings.getFunctionDescription());
        this.latteCustomFunctionSettings = latteCustomFunctionSettings;
    }

    private void onOK() {
        LatteCustomFunctionSettings settings = new LatteCustomFunctionSettings(
                this.textName.getText(),
                this.textReturnType.getText(),
                this.textHelp.getText(),
                this.textDescription.getText()
        );

        if (this.latteCustomFunctionSettings != null) {
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
            this.textName.getText().length() > 0 && this.textReturnType.getText().length() > 0
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
