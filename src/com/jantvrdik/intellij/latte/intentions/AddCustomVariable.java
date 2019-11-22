package com.jantvrdik.intellij.latte.intentions;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.jantvrdik.intellij.latte.LatteLanguage;
import com.jantvrdik.intellij.latte.config.LatteConfiguration;
import com.jantvrdik.intellij.latte.config.LatteDefaultVariable;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for intentions which will register a custom variable.
 */
abstract public class AddCustomVariable extends BaseIntentionAction {

	/** custom macro which will be registered on invocation */
	protected final LatteDefaultVariable defaultVariable;

	public AddCustomVariable(String variableName) {
		this.defaultVariable = new LatteDefaultVariable(variableName, new LattePhpType(variableName, "mixed", isNullable()));
	}

	protected abstract boolean isNullable();

	@NotNull
	@Override
	public String getFamilyName() {
		return "Latte";
	}

	@Override
	public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
		return file.getLanguage() == LatteLanguage.INSTANCE;
	}

	@Override
	public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
		LatteConfiguration.INSTANCE.addCustomVariable(project, defaultVariable);
		DaemonCodeAnalyzer.getInstance(project).restart(); // force re-analyzing
	}
}
