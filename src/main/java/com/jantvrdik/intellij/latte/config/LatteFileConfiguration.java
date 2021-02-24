package com.jantvrdik.intellij.latte.config;

import com.intellij.notification.Notification;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.IndexNotReadyException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.PomTarget;
import com.intellij.pom.PomTargetPsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.XmlFileElement;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.*;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.xml.DomTarget;
import com.jantvrdik.intellij.latte.indexes.LatteIndexExtension;
import com.jantvrdik.intellij.latte.utils.LatteIdeHelper;
import com.jantvrdik.intellij.latte.utils.LatteReparseFilesUtil;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileData;
import com.jantvrdik.intellij.latte.settings.*;
import com.jantvrdik.intellij.latte.settings.xml.LatteXmlFileDataFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

public class LatteFileConfiguration implements Serializable {
    final public static String FILE_NAME = "latte-intellij.xml";

    final public static List<String> REFERENCED_TAGS = Arrays.asList("filter", "variable", "function");

    private static Map<Project, LatteFileConfiguration> instances = new HashMap<>();

    private Map<String, LatteTagSettings> tags = new HashMap<>();

    private Map<String, LatteFilterSettings> filters = new HashMap<>();

    private Map<String, LatteVariableSettings> variables = new HashMap<>();

    private Map<String, LatteFunctionSettings> functions = new HashMap<>();

    private List<LatteConfiguration.Vendor> vendors = new ArrayList<>();

    final private Project project;

    final private Collection<LatteXmlFileData> xmlFileData;

    private Notification notification = null;

    public LatteFileConfiguration(final Project project, final @Nullable Collection<LatteXmlFileData> xmlFileData) {
        this.project = project;
        this.xmlFileData = xmlFileData;
        initialize();
    }

    public static LatteFileConfiguration getInstance(final @NotNull Project project) {
        return getInstance(project, null);
    }

    public static LatteFileConfiguration getInstance(
            final @NotNull Project project,
            final @Nullable Collection<LatteXmlFileData> xmlFileData
    ) {
        if (!instances.containsKey(project)) {
            instances.put(project, new LatteFileConfiguration(project, xmlFileData));
        }
        return instances.get(project);
    }

    private void initialize() {
        if (!LatteSettings.getInstance(project).enableXmlLoading) {
            return;
        }

        if (xmlFileData != null) {
            initializeFromXmlData(xmlFileData);
        } else {
            initializeFromFileIndex();
        }
    }

    private void initializeFromXmlData(@NotNull Collection<LatteXmlFileData> xmlData) {
        for (LatteXmlFileData data : xmlData) {
            if (data == null) {
                continue;
            }
            applyXmlData(data);
        }
    }

    private void initializeFromFileIndex() {
        if (false && ActionUtil.isDumbMode(project)) {
            if (notification == null || notification.isExpired() || LatteReparseFilesUtil.isNotificationOutdated(notification)) {
                notification = LatteReparseFilesUtil.notifyReparseFiles(project);
            }
            return;
        }

        try {
            Collection<String> val = FileBasedIndex.getInstance().getAllKeys(LatteIndexExtension.KEY, project);
            for (String key : val) {
                Collection<LatteXmlFileData> xmlData = FileBasedIndex.getInstance().getValues(
                        LatteIndexExtension.KEY,
                        key,
                        GlobalSearchScope.allScope(project)
                );
                if (xmlData != null) {
                    initializeFromXmlData(xmlData);
                }
            }

        } catch (IndexNotReadyException e) {
            notification = LatteReparseFilesUtil.notifyReparseFiles(project);
        }
        //PsiFile[] files = FilenameIndex.getFilesByName(project, FILE_NAME, GlobalSearchScope.allScope(project));
    }

    public void applyXmlData(LatteXmlFileData data) {
        removeAllWithVendor(data.getVendorResult());

        tags.putAll(data.getTags());
        filters.putAll(data.getFilters());
        variables.putAll(data.getVariables());
        functions.putAll(data.getFunctions());

        if (!vendors.contains(data.getVendorResult().vendor)) {
            vendors.add(data.getVendorResult().vendor);
        }
    }

    public boolean hasVendor(LatteConfiguration.Vendor vendor) {
        return vendors.contains(vendor);
    }

    private void removeAllWithVendor(LatteXmlFileData.VendorResult vendorResult) {
        tags.entrySet().removeIf(entry -> entry.getValue().getVendorName().equals(vendorResult.vendorName));
        filters.entrySet().removeIf(entry -> entry.getValue().getVendorName().equals(vendorResult.vendorName));
        variables.entrySet().removeIf(entry -> entry.getValue().getVendorName().equals(vendorResult.vendorName));
        functions.entrySet().removeIf(entry -> entry.getValue().getVendorName().equals(vendorResult.vendorName));
    }

    public void reinitialize() {
        tags = new HashMap<>();
        filters = new HashMap<>();
        variables = new HashMap<>();
        functions = new HashMap<>();
        vendors = new ArrayList<>();
        initialize();
    }

    @Nullable
    public LatteXmlFileData reinitialize(XmlFile file) {
        LatteXmlFileData data = LatteXmlFileDataFactory.parse(file);
        if (data == null) {
            return null;
        }
        applyXmlData(data);
        return data;
    }

    public Map<String, LatteTagSettings> getTags() {
        return Collections.unmodifiableMap(tags);
    }

    public Map<String, LatteFilterSettings> getFilters() {
        return Collections.unmodifiableMap(filters);
    }

    public Map<String, LatteVariableSettings> getVariables() {
        return Collections.unmodifiableMap(variables);
    }

    public Map<String, LatteFunctionSettings> getFunctions() {
        return Collections.unmodifiableMap(functions);
    }

    public static boolean isXmlConfiguration(XmlFile file) {
        XmlDocument document = file.getDocument();
        if (document == null || document.getRootTag() == null) {
            return false;
        }

        XmlTag configuration = document.getRootTag();
        if (configuration == null) {
            return false;
        }
        return configuration.getName().equals("latte") && configuration.getAttribute("vendor") != null;
    }

    public static boolean isXmlConfigurationFile(PsiFile file) {
        return file instanceof XmlFile && isXmlConfigurationFileName(file.getName());
    }

    public static boolean isXmlConfigurationFile(VirtualFile file) {
        return file instanceof XmlFile && isXmlConfigurationFileName(file.getName());
    }

    public static boolean isXmlConfigurationFileName(String fileName) {
        return fileName.equals(LatteFileConfiguration.FILE_NAME);
    }

    public static boolean hasParentXmlTagName(@NotNull PsiElement element, String tagName) {
        return hasParentXmlTagName(element, Collections.singletonList(tagName));
    }

    public static boolean hasParentXmlTagName(@NotNull PsiElement element, List<String> tagNames) {
        if (!isValidFile(element) || !(element instanceof XmlAttributeValue)) {
            return false;
        }
        PsiElement parent = element.getParent().getParent();
        return parent instanceof XmlTag && tagNames.contains(((XmlTag) parent).getName());
    }

    private static boolean isValidFile(@NotNull PsiElement element) {
        PsiFile file = element.getContainingFile();
        return (file instanceof XmlFileElement || file instanceof XmlFile)
                && LatteFileConfiguration.isXmlConfigurationFileName(file.getVirtualFile() != null ? file.getVirtualFile().getName() : file.getName());
    }

    public static List<XmlAttributeValue> getAllMatchedXmlAttributeValues(
            @NotNull Project project,
            @NotNull String tagName,
            @Nullable String elementName
    ) {
        List<XmlAttributeValue> attributeValues = new ArrayList<>();
        for (PsiFile file : getAllPsiFiles(project)) {
            if (!(file instanceof XmlFile)) {
                continue;
            }

            XmlDocument document = ((XmlFile) file).getDocument();
            if (document == null || document.getRootTag() == null) {
                continue;
            }

            XmlTag configuration = document.getRootTag();
            if (configuration == null) {
                continue;
            }

            XmlTag tags = configuration.findFirstSubTag(tagName + "s");
            if (tags != null) {
                for (XmlTag tag : tags.findSubTags(tagName)) {
                    XmlAttribute name = tag.getAttribute("name");
                    if (name == null || name.getValueElement() == null) {
                        continue;
                    }
                    XmlAttributeValue value = name.getValueElement();
                    if (!value.getValue().equals(elementName)) {
                        continue;
                    }
                    attributeValues.add(value);
                }
            }
        }
        return attributeValues;
    }

    public static PsiFile[] getAllPsiFiles(Project project) {
        final PsiFile[] files = FilenameIndex.getFilesByName(project, LatteFileConfiguration.FILE_NAME, GlobalSearchScope.allScope(project));
        List<PsiFile> psiFiles = new ArrayList<>(Arrays.asList(files));
        LatteSettings settings = LatteSettings.getInstance(project);

        for (Map.Entry<String, LatteConfiguration.Vendor> source : LatteDefaultConfiguration.sourceFiles.entrySet()) {
            LatteConfiguration.Vendor vendor = source.getValue();
            if (!settings.isEnabledSourceVendor(vendor) || !LatteFileConfiguration.getInstance(project).hasVendor(vendor)) {
                continue;
            }

            Path path = LatteIdeHelper.getPathToProjectTemp(project, "xmlSources/" + source.getKey());
            if (path == null) {
                continue;
            }
            XmlFile psiFile = LatteIdeHelper.getXmlFileForPath(project, path);
            if (psiFile == null || !isXmlConfiguration(psiFile)) {
                continue;
            }
            psiFiles.add(psiFile);
        }

        return psiFiles.toArray(new PsiFile[0]);
    }

    @Nullable
    public static XmlAttributeValue getPsiElementFromDomTarget(@NotNull String tagName, @NotNull PsiElement psiElement) {
        return getPsiElementFromDomTarget(Collections.singletonList(tagName), psiElement);
    }

    @Nullable
    public static XmlAttributeValue getPsiElementFromDomTarget(@NotNull List<String> tagName, @NotNull PsiElement psiElement) {
        PsiElement element = LatteFileConfiguration.getPsiElementFromDomTarget(psiElement);
        if (!(element instanceof XmlAttributeValue) || !(element.getParent() instanceof XmlAttribute)) {
            return null;
        }
        if (!(psiElement.getContainingFile() instanceof XmlFile) || !LatteFileConfiguration.isXmlConfiguration((XmlFile) psiElement.getContainingFile())) {
            return null;
        }
        XmlAttribute attribute = (XmlAttribute) element.getParent();
        return attribute.getName().equals("name") && tagName.contains(attribute.getParent().getName())
                ? (XmlAttributeValue) element
                : null;
    }

    @Nullable
    public static PsiElement getPsiElementFromDomTarget(PsiElement element) {
        if (!(element instanceof PomTargetPsiElement)) {
            return null;
        }
        PomTarget target = ((PomTargetPsiElement) element).getTarget();
        return !(target instanceof DomTarget) ? null : ((DomTarget) target).getNavigationElement();
    }

}
