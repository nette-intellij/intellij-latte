package com.jantvrdik.intellij.latte.reference;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.TextOccurenceProcessor;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.jantvrdik.intellij.latte.psi.LattePhpStaticVariable;
import com.jantvrdik.intellij.latte.reference.references.LattePhpStaticVariableReference;
import com.jetbrains.php.lang.psi.elements.Field;
import org.jetbrains.annotations.NotNull;


public class LatteReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

    @Override
    public void processQuery(ReferencesSearch.@NotNull SearchParameters searchParameters, @NotNull Processor<? super PsiReference> processor) {
        if (searchParameters.getElementToSearch() instanceof Field) {
            processField((Field) searchParameters.getElementToSearch(), searchParameters.getScopeDeterminedByUser(), processor);
        }
    }

    private void processField(@NotNull Field field, @NotNull SearchScope searchScope, @NotNull Processor<? super PsiReference> processor) {
        if (!field.getModifier().isStatic() || field.isConstant()) { //todo: fix error sometimes produced by this line
            return;
        }
        String fieldName = field.getName(); //todo: fix error produced by this line (Read access is allowed from event dispatch thread or inside read-action only)

        PsiSearchHelper.SERVICE.getInstance(field.getProject())
                .processElementsWithWord(new TextOccurenceProcessor() {
                    @Override
                    public boolean execute(PsiElement psiElement, int i) {
                        PsiElement currentMethod = psiElement.getParent();
                        if (!(currentMethod instanceof LattePhpStaticVariable)) {
                            return true;
                        }

                        String value = ((LattePhpStaticVariable) currentMethod).getVariableName();
                        processor.process(new LattePhpStaticVariableReference((LattePhpStaticVariable) currentMethod, new TextRange(0, value.length() + 1)));

                        return true;
                    }
                }, searchScope, "$" + fieldName, UsageSearchContext.IN_CODE, true);
    }
}