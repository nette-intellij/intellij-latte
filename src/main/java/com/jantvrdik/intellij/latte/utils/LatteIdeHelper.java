package com.jantvrdik.intellij.latte.utils;

import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LatteIdeHelper {
    public static String NOTIFICATION_GROUP = "Latte";

    public static void openUrl(String url) {
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

    public static void doNotify(
            @NotNull String title,
            @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String content,
            @NotNull NotificationType type,
            @Nullable Project project
    ) {
        doNotify(title, content, type, project, null);
    }

    public static void doNotify(
            @NotNull String title,
            @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String content,
            @NotNull NotificationType type,
            @Nullable Project project,
            @Nullable NotificationAction notificationAction
    ) {
        doNotify(title, content, type, project, false, notificationAction);
    }

    public static Notification doNotify(
            @NotNull String title,
            @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String content,
            @NotNull NotificationType type,
            @Nullable Project project,
            boolean important,
            @Nullable NotificationAction notificationAction
    ) {
        Notification notification = new Notification(NOTIFICATION_GROUP, title, content, type);
        notification.setImportant(important);
        if (notificationAction != null) {
            notification.addAction(notificationAction);
        }
        doNotify(notification, project);
        return notification;
    }

    public static void doNotify(Notification notification, @Nullable Project project) {
        if (project != null && !project.isDisposed() && !project.isDefault()) {
            project.getMessageBus().syncPublisher(Notifications.TOPIC).notify(notification);
        } else {
            Application app = ApplicationManager.getApplication();
            if (!app.isDisposed()) {
                app.getMessageBus().syncPublisher(Notifications.TOPIC).notify(notification);
            }
        }
    }

    public static void navigateToPsiElement(@NotNull PsiElement psiElement) {
        final Navigatable descriptor = PsiNavigationSupport.getInstance().getDescriptor(psiElement);
        if (descriptor != null) {
            descriptor.navigate(true);
        }
    }

}