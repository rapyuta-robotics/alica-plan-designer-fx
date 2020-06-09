package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.Variable;
import de.unikassel.vs.alica.planDesigner.view.model.VariableViewModel;

public class VariableViewModelFactory extends InternalViewModelFactory<VariableViewModel, Variable> {

    @Override
    VariableViewModel create(Variable var) {
        VariableViewModel variableViewModel = new VariableViewModel(var.getId(), var.getName());
        variableViewModel.setVariableType(var.getVariableType());
        variableViewModel.setComment(var.getComment());
        return variableViewModel;
    }
}
