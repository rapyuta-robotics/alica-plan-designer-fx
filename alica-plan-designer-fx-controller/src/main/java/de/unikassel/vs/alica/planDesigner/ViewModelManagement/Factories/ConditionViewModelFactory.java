package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.*;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.model.ConditionViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.QuantifierViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.VariableViewModel;

public class ConditionViewModelFactory extends InternalViewModelFactory<ConditionViewModel, Condition> {
    @Override
    ConditionViewModel create(Condition condition) {
        ConditionViewModel conditionViewModel = null;
        if (condition instanceof PreCondition) {
            conditionViewModel = new ConditionViewModel(condition.getId(), condition.getName(), Types.PRECONDITION);
        } else if (condition instanceof RuntimeCondition) {
            conditionViewModel = new ConditionViewModel(condition.getId(), condition.getName(), Types.RUNTIMECONDITION);
        } else if (condition instanceof PostCondition) {
            conditionViewModel = new ConditionViewModel(condition.getId(), condition.getName(), Types.POSTCONDITION);
        }
        conditionViewModel.setConditionString(condition.getConditionString());
        conditionViewModel.setEnabled(condition.getEnabled());
        conditionViewModel.setPluginName(condition.getPluginName());
        conditionViewModel.setComment(condition.getComment());
        for (Variable var : condition.getVariables()) {
            conditionViewModel.getVariables().add((VariableViewModel) viewModelManager.getViewModelElement(var));
        }
        for (Quantifier quantifier : condition.getQuantifiers()) {
            // TODO: Quantifier is not very clean or fully implemented, yet.
            conditionViewModel.getQuantifiers().add((QuantifierViewModel) viewModelManager.getViewModelElement(quantifier));
        }
        return conditionViewModel;
    }
}
