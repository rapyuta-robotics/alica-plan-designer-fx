package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.Synchronisation;
import de.unikassel.vs.alica.planDesigner.alicamodel.Transition;
import de.unikassel.vs.alica.planDesigner.uiextensionmodel.UiElement;
import de.unikassel.vs.alica.planDesigner.view.model.SynchronisationViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.TransitionViewModel;

public class SynchronizationViewModelFactory extends InternalViewModelFactory<SynchronisationViewModel, Synchronisation> {
    @Override
    SynchronisationViewModel create(Synchronisation synchronisation) {
        SynchronisationViewModel synchronisationViewModel = new SynchronisationViewModel(synchronisation.getId(), synchronisation.getName());
        for (Transition transition : synchronisation.getSyncedTransitions()) {
            synchronisationViewModel.getTransitions().add((TransitionViewModel) viewModelManager.getViewModelElement(transition));
        }
        UiElement uiElement = modelManager.getPlanUIExtensionPair(synchronisation.getPlan().getId()).getUiElement(synchronisation.getId());
        synchronisationViewModel.setXPosition(uiElement.getX());
        synchronisationViewModel.setYPosition(uiElement.getY());
        synchronisationViewModel.setSyncTimeout(synchronisation.getSyncTimeout());
        synchronisationViewModel.setFailOnSyncTimeout(synchronisation.getFailOnSyncTimeout());
        synchronisationViewModel.setTalkTimeout(synchronisation.getTalkTimeout());
        synchronisationViewModel.setParentId(synchronisation.getPlan().getId());
        return synchronisationViewModel;
    }
}
