package com.jantvrdik.intellij.latte.utils;

import com.intellij.ide.util.PsiNavigationSupport;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.*;
import com.intellij.openapi.application.ex.ApplicationEx;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class LatteIdeHelper {
    public static String NOTIFICATION_GROUP = "Latte";

    public static boolean holdsReadLock() {
        Application app = ApplicationManager.getApplication();
        try {
            return ((ApplicationEx)app).holdsReadLock();
        } catch (IllegalStateException e) {
            return false;
        }
    }

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

    @Nullable
    public static Path saveFileToProjectTemp(Project project, String setupFile) {
        try {
            Path tempDir = getTempPath(project);
            if (!Files.isDirectory(tempDir)) {
                Files.createDirectory(tempDir);
            }

            Path setupScriptPath = Paths.get(tempDir.toString(), setupFile);
            Path parent = setupScriptPath.getParent();
            if (!Files.isDirectory(parent)) {
                Files.createDirectory(parent);
            }
            InputStream setupResourceStream = LatteIdeHelper.class.getClassLoader().getResourceAsStream(setupFile);
            if (setupResourceStream == null) {
                return null;
            }
            Files.copy(setupResourceStream, setupScriptPath, StandardCopyOption.REPLACE_EXISTING);
            setupResourceStream.close();

            return setupScriptPath;

        } catch (IOException e) {
            return null;
        }
    }

    private static Path getTempPath(Project project) {
        if (project.getBasePath() != null) {
            return Paths.get(project.getBasePath(), ".idea", "intellij-latte");
        } else {
            return Paths.get(PathManager.getPluginsPath(), "intellij-latte");
        }
    }

}