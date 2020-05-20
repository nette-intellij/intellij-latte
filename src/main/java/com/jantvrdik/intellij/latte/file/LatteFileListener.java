package com.jantvrdik.intellij.latte.file;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.jantvrdik.intellij.latte.config.LatteFileConfiguration;
import com.jantvrdik.intellij.latte.utils.LatteIdeHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LatteFileListener implements BulkFileListener {
    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        for (VFileEvent event : events) {
            VirtualFile file = event.getFile();
            if (file == null || !file.getName().equals("latte-intellij.xml")) {
                continue;
            }
            Project project = ProjectUtil.guessProjectForContentFile(file);

            LatteIdeHelper.doNotify(
                    "Latte plugin settings",
                    "File latte-intellij.xml was changed. You can refresh configurations.",
                    NotificationType.INFORMATION,
                    project,
                    new NotificationAction("Refresh Configuration") {
                        @Override
                        public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                            LatteFileConfiguration.getInstance(project).reinitialize();
                            notification.expire();

                            LatteIdeHelper.doNotify(
                                    "Latte plugin settings",
                                    "Configuration from latte-intellij.xml file was loaded.",
                                    NotificationType.INFORMATION,
                                    project
                            );
                        }
                    }
            );
        }
    }
}
