package com.jantvrdik.intellij.latte.ui;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
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
	private JCheckBox disableDefaultLoadingForCheckBox;
	private JButton buttonReinitialize;
	private JButton moreInformationButton;

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
				LatteConfiguration.getInstance(project).reinitialize();
				LatteIdeHelper.doNotify(
						"Latte plugin settings",
						"Configuration from .xml files was refreshed.",
						NotificationType.INFORMATION,
						project
				);
				buttonReinitialize.setBackground(null);
			}
		});

		disableDefaultLoadingForCheckBox.setSelected(getSettings().disableDefaultLoading);

		disableDefaultLoadingForCheckBox.addMouseListener(new MouseAdapter() {
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
		boolean wasSelected = getSettings().disableDefaultLoading;
		getSettings().disableDefaultLoading = disableDefaultLoadingForCheckBox.isSelected();
		LatteConfiguration.getInstance(project).reinitialize();
		this.changed = false;

		if (!wasSelected && getSettings().enableCustomFunctions) {
			buttonReinitialize.setBackground(JBColor.RED);
		}
	}

	private LatteSettings getSettings() {
		return LatteSettings.getInstance(this.project);
	}

	@Override
	public void disposeUIResources() {

	}

}