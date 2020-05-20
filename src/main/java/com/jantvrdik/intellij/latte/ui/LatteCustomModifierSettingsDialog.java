package com.jantvrdik.intellij.latte.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.table.TableView;
import com.jantvrdik.intellij.latte.settings.LatteCustomModifierSettings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LatteCustomModifierSettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textName;
    private JTextField textHelp;
    private JTextArea textDescription;
    private JTextField textInsert;
    private LatteCustomModifierSettings latteCustomModifierSettings;
    private TableView<LatteCustomModifierSettings> tableView;

    public LatteCustomModifierSettingsDialog(Project project, TableView<LatteCustomModifierSettings> tableView) {
        this.tableView = tableView;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        this.textName.getDocument().addDocumentListener(new ChangeDocumentListener());
        this.textHelp.getDocument().addDocumentListener(new ChangeDocumentListener());
        this.textDescription.getDocument().addDocumentListener(new ChangeDocumentListener());
        this.textInsert.getDocument().addDocumentListener(new ChangeDocumentListener());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public LatteCustomModifierSettingsDialog(Project project, TableView<LatteCustomModifierSettings> tableView, LatteCustomModifierSettings latteCustomModifierSettings) {
        this(project, tableView);

        this.textName.setText(latteCustomModifierSettings.getModifierName());
        this.textHelp.setText(latteCustomModifierSettings.getModifierHelp());
        this.textDescription.setText(latteCustomModifierSettings.getModifierDescription());
        this.textInsert.setText(latteCustomModifierSettings.getModifierInsert());
        this.latteCustomModifierSettings = latteCustomModifierSettings;
    }

    private void onOK() {
        LatteCustomModifierSettings settings = new LatteCustomModifierSettings(
                this.textName.getText(),
                this.textDescription.getText(),
                this.textHelp.getText(),
                this.textInsert.getText()
        );

        if (this.latteCustomModifierSettings != null) {
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
            this.textName.getText().length() > 0 && this.textDescription.getText().length() > 0
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
