package com.jantvrdik.intellij.latte.files;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.jantvrdik.intellij.latte.utils.LatteReparseFilesUtil;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileData;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileDataFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LatteFileListener implements BulkFileListener {
    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        for (VFileEvent event : events) {
            VirtualFile file = event.getFile();
            if (!(file instanceof XmlFile) || !file.getName().equals("latte-intellij.xml") || file.isValid()) {
                continue;
            }

            XmlDocument document = ((XmlFile) file).getDocument();
            if (document == null || document.getRootTag() == null) {
                continue;
            }

            LatteXmlFileData.VendorResult vendorResult = LatteXmlFileDataFactory.getVendor(document);
            if (vendorResult == null) {
                continue;
            }

            List<Project> projects = new ArrayList<>();
            Project project = ProjectUtil.guessProjectForContentFile(file);
            if (project != null) {
                projects.add(project);
            } else {
                Collections.addAll(projects, ProjectManager.getInstance().getOpenProjects());
            }

            LatteReparseFilesUtil.notifyRemovedFiles(projects);
        }
    }
}
