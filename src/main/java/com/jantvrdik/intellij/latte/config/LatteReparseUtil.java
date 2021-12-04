package com.jantvrdik.intellij.latte.config;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.util.FileContentUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LatteReparseUtil {
    final private static int WAIT_MILIS_BETWEEN_REPARSE = 5000;

    private static final Map<Project, LatteReparseUtil> instances = new HashMap<>();

    private long lastCall = 0;

    private final @NotNull Project project;

    public LatteReparseUtil(@NotNull Project project) {
        this.project = project;
    }

    public static LatteReparseUtil getInstance(@NotNull Project project) {
        if (!instances.containsKey(project)) {
            instances.put(project, new LatteReparseUtil(project));
        }
        return instances.get(project);
    }

    public static void reinitialize(List<Project> projects) {
        for (Project project : projects) {
            LatteReparseUtil.getInstance(project).reinitialize();
        }
    }

    public void reinitialize() {
        //LatteFileConfiguration.getInstance(project).reinitialize();
        reparseOpenedFiles(project, null, false);
    }

    public void reinitialize(Runnable runnable) {
        //if (LatteFileConfiguration.getInstance(project).reinitialize()) {
        reparseOpenedFiles(project, runnable, false);
        //}
    }

    public void reinitializeDefault() {
        //if (LatteDefaultConfiguration.getInstance(project).reinitialize()) {
        reparseOpenedFiles(project, null, true);
        //}
    }

    private void reparseOpenedFiles(@NotNull Project project, @Nullable Runnable runnable, boolean def) {
        /*if ((lastCall + WAIT_MILIS_BETWEEN_REPARSE) > System.currentTimeMillis()) {
            run(runnable);
            return;
        }*/

        DumbService.getInstance(project).runWhenSmart(() -> {
            if (def && !LatteDefaultConfiguration.getInstance(project).reinitialize()) {
                return;
            }

            if (!def && !LatteFileConfiguration.getInstance(project).reinitialize()) {
                return;
            }

            if ((lastCall + WAIT_MILIS_BETWEEN_REPARSE) > System.currentTimeMillis()) {
                run(runnable);
                return;
            }

            FileContentUtil.reparseOpenedFiles();
            lastCall = System.currentTimeMillis();
            run(runnable);
        });
    }

    private void run(@Nullable Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }
}
