package com.jantvrdik.intellij.latte.ui;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteFileConfiguration;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import com.jantvrdik.intellij.latte.indexes.LatteIndexUtil;
import com.jantvrdik.intellij.latte.settings.LatteSettings;
import com.jantvrdik.intellij.latte.utils.LatteIdeHelper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LatteSettingsForm implements Configurable {
	private JPanel panel1;
	private JButton buttonHelp;
	private JLabel logoLabel;
	private JCheckBox enableXmlLoadingCheckBox;
	private JButton buttonReinitialize;
	private JButton moreInformationButton;
	private JCheckBox enableNetteCheckBox;

	private Project project;
	private boolean changed = false;

	public LatteSettingsForm(Project project) {
		this.project = project;

		logoLabel.setIcon(LatteIcons.LOGO);

		buttonHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteIdeHelper.openUrl(LatteConfiguration.LATTE_HELP_URL + "en/");
			}
		});

		moreInformationButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteIdeHelper.openUrl(LatteConfiguration.LATTE_DOCS_XML_FILES_URL);
			}
		});

		buttonReinitialize.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteFileConfiguration.getInstance(project).reinitialize();
				LatteIdeHelper.doNotify(
						"Latte plugin settings",
						"Configuration from .xml files was refreshed.",
						NotificationType.INFORMATION,
						project
				);
				buttonReinitialize.setBackground(null);
			}
		});

		enableXmlLoadingCheckBox.setSelected(getSettings().enableXmlLoading);
		enableXmlLoadingCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteSettingsForm.this.changed = true;
			}
		});

		enableNetteCheckBox.setSelected(getSettings().enableNette);
		enableNetteCheckBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				LatteSettingsForm.this.changed = true;
			}
		});
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
		return this.panel1;
	}

	@Override
	public boolean isModified() {
		return this.changed;
	}

	@Override
	public void apply() throws ConfigurationException {
		getSettings().enableXmlLoading = enableXmlLoadingCheckBox.isSelected();
		getSettings().enableNette = enableNetteCheckBox.isSelected();

		boolean success = LatteIndexUtil.reinitialize(project);
		if (success) {
			this.changed = false;
		}
	}

	private LatteSettings getSettings() {
		return LatteSettings.getInstance(this.project);
	}

	@Override
	public void disposeUIResources() {

	}

}