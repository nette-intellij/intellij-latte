package com.jantvrdik.intellij.latte.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.jantvrdik.intellij.latte.LatteFileType;
import com.jantvrdik.intellij.latte.LatteLanguage;
import com.jantvrdik.intellij.latte.php.NettePhpType;
import com.jantvrdik.intellij.latte.psi.elements.LattePhpVariableElement;
import com.jantvrdik.intellij.latte.utils.LattePhpCachedVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

public class LatteFile extends PsiFileBase {

	@Nullable Map<LattePhpVariableElement, LattePhpCachedVariable> variables = null;
	@Nullable List<PsiElement> contexts = null;
	@Nullable Map<PsiElement, List<LattePhpCachedVariable>> contextData = null;
	@Nullable List<LattePhpCachedVariable> sortedVariables = null;
	@Nullable NettePhpType templateType = null;
	@Nullable List<LattePhpClassUsage> templateTypes = null;

	public LatteFile(@NotNull FileViewProvider viewProvider) {
		super(viewProvider, LatteLanguage.INSTANCE);
	}

	@Override
	public @NotNull FileType getFileType() {
		return LatteFileType.INSTANCE;
	}

	@Override
	public String toString() {
		return "Latte file";
	}

	@Override
	public Icon getIcon(int flags) {
		return super.getIcon(flags);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		variables = null;
		sortedVariables = null;
		templateType = null;
		contextData = null;
		contexts = null;
	}

	public @NotNull List<LattePhpCachedVariable> getCachedVariables() {
		loadVariables();
		assert sortedVariables != null;
		return sortedVariables;
	}

	public List<LattePhpCachedVariable> getCachedVariables(int maxOffset) {
		return getCachedVariables(maxOffset, null, false);
	}

	public List<LattePhpCachedVariable> getCachedVariables(@NotNull String searchName) {
		return getCachedVariables(searchName, false, false);
	}

	public List<LattePhpCachedVariable> getCachedVariableDefinitions(@NotNull String searchName) {
		return getCachedVariables(searchName, true, false);
	}

	public List<LattePhpCachedVariable> getCachedVariableDefinitions(int maxOffset) {
		return getCachedVariables(maxOffset, null, true);
	}

	public List<LattePhpCachedVariable> getCachedVariableDefinitions(@NotNull LattePhpVariableElement element) {
		//PsiElement context = element.getVariableContext();
		//todo: use contexts
		String searchName = element.getVariableName();
		int maxOffset = element.getTextOffset();
		List<LattePhpCachedVariable> out = new ArrayList<>();
		for (int i = 0; i < getCachedVariables().size(); i++) {
			LattePhpCachedVariable variable = getCachedVariables().get(i);
			if (variable.getPosition() > maxOffset) {
				break;
			}
			if (variable.isDefinition() && variable.getVariableName().equals(searchName)) {
				out.add(variable);
			}
		}
		return out;
	}

	public List<LattePhpCachedVariable> getCachedVariableDefinitions(int maxOffset, @NotNull String searchName) {
		return getCachedVariables(maxOffset, searchName, true);
	}

	public List<LattePhpCachedVariable> getCachedVariableUsages(@NotNull String searchName) {
		return getCachedVariables(searchName, false, true);
	}

	public List<LattePhpCachedVariable> getCachedVariables(int maxOffset, @Nullable String searchName, boolean onlyDefinitions) {
		List<LattePhpCachedVariable> out = new ArrayList<>();
		for (int i = 0; i < getCachedVariables().size(); i++) {
			LattePhpCachedVariable variable = getCachedVariables().get(i);
			if (variable.getPosition() > maxOffset) {
				break;
			}
			if ((!onlyDefinitions || variable.isDefinition()) && (searchName == null || variable.getVariableName().equals(searchName))) {
				out.add(variable);
			}
		}
		return out;
	}

	private List<LattePhpCachedVariable> getCachedVariables(
			@NotNull String searchName,
			boolean onlyDefinitions,
			boolean onlyUsages
	) {
		return getCachedVariables().stream()
				.filter(
						variable -> variable.getVariableName().equals(searchName)
								&& ((!onlyUsages || !variable.isDefinition()) && (!onlyDefinitions || variable.isDefinition()))
				)
				.collect(Collectors.toList());
	}

	public @Nullable LattePhpCachedVariable getCachedVariable(@NotNull LattePhpVariableElement variable) {
		loadVariables();
		assert variables != null;
		return variables.containsKey(variable) ? variables.getOrDefault(variable, null) : null;
	}

	private void loadVariables() {
		if (variables == null) {
			Map<LattePhpVariableElement, LattePhpCachedVariable> allVariables = findLattePhpVariables();
			sortedVariables = allVariables.values().stream()
					.sorted(Comparator.comparing(LattePhpCachedVariable::getPosition))
					.collect(Collectors.toList());
			//todo: load context dynamically
			/*contextData = new HashMap<>();
			contexts = new ArrayList<>();
			for (LattePhpCachedVariable variable: sortedVariables) {
				PsiElement context = variable.getVariableContext();
				List<LattePhpCachedVariable> data = new ArrayList<>();
				if (contextData.containsKey(context)) {
					data.addAll(contextData.get(context));
				}
				contextData.replace(
						context,
						data.stream().sorted(Comparator.comparing(LattePhpCachedVariable::getPosition)).collect(Collectors.toList())
				);
				contexts.add(context);
			}
			contexts = contexts.stream()
					.sorted(Comparator.comparing(PsiElement::getTextOffset))
					.collect(Collectors.toList());*/
			variables = findLattePhpVariables();
		}
	}

	private Map<LattePhpVariableElement, LattePhpCachedVariable> findLattePhpVariables() {
		Map<LattePhpVariableElement, LattePhpCachedVariable> out = new HashMap<>();
		this.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
			@Override
			public void visitElement(@NotNull PsiElement element) {
				if (element instanceof LattePhpVariableElement) {
					LattePhpCachedVariable cachedVariable = new LattePhpCachedVariable(
							element.getTextOffset(),
							(LattePhpVariableElement) element
					);
					out.put((LattePhpVariableElement) element, cachedVariable);
				} else {
					super.visitElement(element);
				}
			}
		});
		return out;
	}

	@Nullable
	public NettePhpType getFirstLatteTemplateType() {
		if (templateType == null) {
			List<LattePhpClassUsage> types = getLatteTemplateTypes();
			templateType = types.isEmpty() ? null : types.get(0).getReturnType();
		}
		return templateType;
	}

	public @NotNull List<LattePhpClassUsage> getLatteTemplateTypes() {
		if (templateTypes == null) {
			List<LattePhpClassUsage> classes = new ArrayList<>();
			this.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
				@Override
				public void visitElement(@NotNull PsiElement element) {
					if (element instanceof LattePhpClassUsage && ((LattePhpClassUsage) element).isTemplateType()) {
						classes.add((LattePhpClassUsage) element);
					} else {
						super.visitElement(element);
					}
				}
			});
			templateTypes = classes.stream()
					.sorted(Comparator.comparing(LattePhpClassUsage::getTextOffset))
					.collect(Collectors.toList());
		}
		return templateTypes;
	}
}
