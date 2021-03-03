package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.EntryPoint;
import de.unikassel.vs.alica.planDesigner.uiextensionmodel.UiElement;
import de.unikassel.vs.alica.planDesigner.view.model.EntryPointViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.StateViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.TaskViewModel;

public class EntryPointViewModelFactory extends InternalViewModelFactory<EntryPointViewModel, EntryPoint> {
    @Override
    EntryPointViewModel create(EntryPoint ep) {
        EntryPointViewModel entryPointViewModel = new EntryPointViewModel(ep.getId(), ep.getName());
        entryPointViewModel.setComment(ep.getComment());
        // we need to put the ep before creating the state, in order to avoid circles (EntryPoint <-> State)
        viewModelManager.putViewModelForAvoidingLoops(entryPointViewModel);
        if (ep.getState() != null) {
            StateViewModel entryState = (StateViewModel) viewModelManager.getViewModelElement(ep.getState());
            entryPointViewModel.setState(entryState);
            entryState.setEntryPoint(entryPointViewModel);
        }
        if (ep.getTask() != null) {
            entryPointViewModel.setTask((TaskViewModel) viewModelManager.getViewModelElement(ep.getTask()));
        }
        if(ep.getMaxCardinality() >= Integer.MAX_VALUE){
            entryPointViewModel.setMaxCardinality("*");
        } else {
            entryPointViewModel.setMaxCardinality(Integer.toString(ep.getMaxCardinality()));
        }
        entryPointViewModel.setMinCardinality(ep.getMinCardinality());
        entryPointViewModel.setSuccessRequired(ep.getSuccessRequired());
        entryPointViewModel.setParentId(ep.getPlan().getId());
        UiElement uiElement = modelManager.getPlanUIExtensionPair(ep.getPlan().getId()).getUiElement(ep.getId());
        entryPointViewModel.setXPosition(uiElement.getX());
        entryPointViewModel.setYPosition(uiElement.getY());
        return entryPointViewModel;
    }
}
