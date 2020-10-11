package com.jantvrdik.intellij.latte.ide;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.jantvrdik.intellij.latte.icons.LatteIcons;
import org.jetbrains.annotations.NotNull;

public class CreateLatteFileAction extends CreateFileFromTemplateAction implements DumbAware {

  public CreateLatteFileAction() {
    super("Latte File", "Creates new Latte file", LatteIcons.FILE);
  }

  @Override
  protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
    builder
      .setTitle("New Latte file")
      .addKind("Empty file", LatteIcons.FILE, "EmptyLatteFile")
      .addKind("Template file", LatteIcons.FILE, "TemplateLatteFile")
      .addKind("Layout file", LatteIcons.FILE, "LayoutLatteFile");
  }

  @Override
  protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
    return "Latte File";
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof CreateLatteFileAction;
  }
}
