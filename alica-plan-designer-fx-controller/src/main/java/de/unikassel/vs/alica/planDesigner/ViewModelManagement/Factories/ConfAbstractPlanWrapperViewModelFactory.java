package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.ConfAbstractPlanWrapper;
import de.unikassel.vs.alica.planDesigner.view.model.AbstractPlanViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.ConfAbstractPlanWrapperViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.ConfigurationViewModel;

public class ConfAbstractPlanWrapperViewModelFactory extends InternalViewModelFactory<ConfAbstractPlanWrapperViewModel, ConfAbstractPlanWrapper>{

    @Override
    public ConfAbstractPlanWrapperViewModel create(ConfAbstractPlanWrapper confAbstractPlanWrapper) {
        ConfAbstractPlanWrapperViewModel confAbstractPlanWrapperViewModel = new ConfAbstractPlanWrapperViewModel(confAbstractPlanWrapper.getId(), confAbstractPlanWrapper.getName());
        confAbstractPlanWrapperViewModel.setAbstractPlan((AbstractPlanViewModel) viewModelManager.getViewModelElement(resolveDummy(confAbstractPlanWrapper.getAbstractPlan())));
        if (confAbstractPlanWrapper.getConfiguration() != null) {
            confAbstractPlanWrapperViewModel.setConfiguration((ConfigurationViewModel) viewModelManager.getViewModelElement(resolveDummy(confAbstractPlanWrapper.getConfiguration())));
        }
        return confAbstractPlanWrapperViewModel;
    }
}
