package com.jantvrdik.intellij.latte.ui;

import com.intellij.ide.plugins.*;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationUtil;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.updateSettings.impl.pluginsAdvertisement.PluginsAdvertiser;
import com.intellij.openapi.vcs.changes.shelf.ShelveChangesManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;

public class LattePluginStartupActivity extends ShelveChangesManager.PostStartupActivity {

    private static final @NotNull Logger LOG = Logger.getInstance(CommandProcessor.class);
    private static final @NotNull String NOTIFICATION_GROUP = "Latte Pro Group";
    private static final @NotNull String PLUGIN_PAGE_URL = "https://plugins.jetbrains.com/plugin/19661-latte-pro";

    @Override
    public void runActivity(@NotNull Project project) {
        try {
            Thread.sleep(80000);

            ApplicationUtil.tryRunReadAction(() -> {
                latteProSuggestion("More advanced and faster plugin for Latte with XML configuration support, included files support, documentation, etc. is here!");
                return null;
            });

        } catch (InterruptedException e) {
            //do nothing
        }
    }

    public static void latteProSuggestion(final @NotNull String message) {
        try {
            final Notification notification = new Notification(NOTIFICATION_GROUP, message, NotificationType.IDE_UPDATE);
            notification.addAction(new NotificationAction("Try new plugin") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                    PluginId pluginId = PluginId.getId("com.mesour.intellij.latte");
                    PluginManagerCore.getPlugin(pluginId);
                    PluginsAdvertiser.installAndEnable(
                            null,
                            new HashSet<>() {{ add(pluginId); }},
                            true,
                            () -> PluginManager.disablePlugin("com.jantvrdik.intellij.latte")
                    );
                }
            });
            notification.addAction(new NotificationAction("See more...") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                    openUrl(PLUGIN_PAGE_URL);
                }
            });
            final Application app = ApplicationManager.getApplication();
            if (!app.isDisposed()) {
                app.getMessageBus().syncPublisher(Notifications.TOPIC).notify(notification);
            }

        } catch (IllegalMonitorStateException e) {
            LOG.error(e);
        }
    }

    private static void openUrl(final String url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();

            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    URI uri = new URI(url);
                    desktop.browse(uri);
                } catch (URISyntaxException | IOException ignored) {
                }
            }
        }
    }

}
