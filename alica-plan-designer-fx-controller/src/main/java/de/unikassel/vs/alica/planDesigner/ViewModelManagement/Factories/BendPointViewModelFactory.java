package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.uiextensionmodel.BendPoint;
import de.unikassel.vs.alica.planDesigner.view.model.BendPointViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.TransitionViewModel;

public class BendPointViewModelFactory extends InternalViewModelFactory<BendPointViewModel, BendPoint> {
    @Override
    BendPointViewModel create(BendPoint bendPoint) {
        BendPointViewModel bendPointViewModel = new BendPointViewModel(bendPoint.getId(), bendPoint.getName());
        bendPointViewModel.setComment(bendPoint.getComment());
        bendPointViewModel.setX(bendPoint.getX());
        bendPointViewModel.setY(bendPoint.getY());
        bendPointViewModel.setTransition((TransitionViewModel) viewModelManager.getViewModelElement(bendPoint.getTransition()));
        return bendPointViewModel;
    }
}
