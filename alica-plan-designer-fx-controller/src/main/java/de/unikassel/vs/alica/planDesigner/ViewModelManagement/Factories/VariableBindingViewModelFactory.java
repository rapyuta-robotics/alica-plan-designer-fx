package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.VariableBinding;
import de.unikassel.vs.alica.planDesigner.view.model.AbstractPlanViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.VariableBindingViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.VariableViewModel;

public class VariableBindingViewModelFactory extends InternalViewModelFactory<VariableBindingViewModel, VariableBinding> {
    @Override
    VariableBindingViewModel create(VariableBinding variableBinding) {
        VariableBindingViewModel variableBindingViewModel = new VariableBindingViewModel(variableBinding.getId(), variableBinding.getName());
        variableBindingViewModel.setComment(variableBinding.getComment());
        variableBindingViewModel.setSubPlan((AbstractPlanViewModel) viewModelManager.getViewModelElement(resolveDummy(variableBinding.getSubPlan())));
        variableBindingViewModel.setSubVariable((VariableViewModel) viewModelManager.getViewModelElement(resolveDummy(variableBinding.getSubVariable())));
        variableBindingViewModel.setVariable((VariableViewModel) viewModelManager.getViewModelElement(variableBinding.getVariable()));
        return variableBindingViewModel;
    }
}
