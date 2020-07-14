package com.jantvrdik.intellij.latte.indexes;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.notification.Notification;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jantvrdik.intellij.latte.config.LatteFileConfiguration;
import com.jantvrdik.intellij.latte.indexes.externalizer.ObjectStreamDataExternalizer;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileData;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileDataFactory;
import com.jantvrdik.intellij.latte.utils.LatteReparseFilesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.coverage.gnu.trove.THashMap;

import java.util.*;

public class LatteIndexExtension extends FileBasedIndexExtension<String, LatteXmlFileData> {
    public static final ID<String, LatteXmlFileData> KEY = ID.create("com.jantvrdik.intellij.latte.configuration_index");

    private final KeyDescriptor<String> KEY_DESCRIPTOR = new EnumeratorStringDescriptor();
    private static ObjectStreamDataExternalizer<LatteXmlFileData> DATA_EXTERNALIZER = new ObjectStreamDataExternalizer<>();

    private Notification notification = null;

    @NotNull
    @Override
    public DataIndexer<String, LatteXmlFileData, FileContent> getIndexer() {
        return fileContent -> {
            PsiFile psiFile = fileContent.getPsiFile();
            if (!LatteFileConfiguration.isXmlConfigurationFile(psiFile)) {
                return Collections.emptyMap();
            }

            Project project = psiFile.getProject();
            Map<String, LatteXmlFileData> out = new THashMap<>();
            LatteXmlFileData.VendorResult vendor = LatteXmlFileDataFactory.getVendor(((XmlFile) psiFile).getDocument());
            if (vendor != null) {
                LatteXmlFileData xmlFileData = LatteFileConfiguration.getInstance(project).reinitialize((XmlFile) psiFile);
                if (xmlFileData != null) {
                    out.put(xmlFileData.getVendorResult().vendorName, xmlFileData);
                }
            }

            if (notification == null || notification.isExpired() || LatteReparseFilesUtil.isNotificationOutdated(notification)) {
                notification = LatteReparseFilesUtil.notifyReparseFiles(project);
            }

            return out;
        };
    }

    @NotNull
    @Override
    public ID<String, LatteXmlFileData> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return KEY_DESCRIPTOR;
    }

    @NotNull
    @Override
    public DataExternalizer<LatteXmlFileData> getValueExternalizer() {
        return DATA_EXTERNALIZER;
    }

    @Override
    public int getVersion() {
        return 2;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> file.getFileType() == XmlFileType.INSTANCE;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

}