package com.jantvrdik.intellij.latte.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
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
	}
	/*
        private void setupPathComponent(final JPanel panel) {
            SeparatorWithText separatorWithText = new SeparatorWithText();
            separatorWithText.setCaption("Useful links");
            separatorWithText.setCaptionCentered(false);
            panel.add(separatorWithText, BorderLayout.BEFORE_FIRST_LINE);

            separatorWithText.add(createHyperLink(), BorderLayout.AFTER_LINE_ENDS);

            HighlightableComponent label = new HighlightableComponent();
            label.setText("Test text");
            label.setIcon(LatteIcons.FILE);
            panel.add(label);



            //ContextHelpLabel contextLabel = ContextHelpLabel.create("test help", "with description");
            //panel.add(contextLabel);
        }

        private HyperlinkLabel createHyperLink() {
            final HyperlinkLabel settingsLink = new HyperlinkLabel("Documentation x");
            settingsLink.setHyperlinkTarget("https://latte.nette.org/en/guide");
            return settingsLink;
        }
    */
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
		//getSettings().codeCompletionEnabled = codeCompletionEnabled.isSelected();

		this.changed = false;
	}

	private LatteSettings getSettings() {
		return LatteSettings.getInstance(this.project);
	}

	@Override
	public void disposeUIResources() {

	}

}