package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.Transition;
import de.unikassel.vs.alica.planDesigner.uiextensionmodel.BendPoint;
import de.unikassel.vs.alica.planDesigner.view.model.BendPointViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.ConditionViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.StateViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.TransitionViewModel;

public class TransitionViewModelFactory extends InternalViewModelFactory<TransitionViewModel, Transition> {
    @Override
    TransitionViewModel create(Transition transition) {
        TransitionViewModel transitionViewModel = new TransitionViewModel(transition.getId(), transition.getName());
        transitionViewModel.setInState((StateViewModel) viewModelManager.getViewModelElement(transition.getInState()));
        transitionViewModel.setOutState((StateViewModel) viewModelManager.getViewModelElement(transition.getOutState()));
        transitionViewModel.setParentId(transition.getInState().getParentPlan().getId());
        StateViewModel inStateViewModel = transitionViewModel.getInState();
        StateViewModel outStateViewModel = transitionViewModel.getOutState();
        inStateViewModel.getOutTransitions().add(transitionViewModel);
        outStateViewModel.getInTransitions().add(transitionViewModel);
        if (transition.getPreCondition() != null) {
            ConditionViewModel conditionViewModel = (ConditionViewModel) viewModelManager.getViewModelElement(transition.getPreCondition());
            conditionViewModel.setParentId(transition.getId());
            transitionViewModel.setPreCondition(conditionViewModel);
        }
        // we need to put the transition before creating bendpoints, in order to avoid circles (Transition <-> BendPoint)
        viewModelManager.putViewModelForAvoidingLoops(transitionViewModel);
        for (BendPoint bendPoint : modelManager.getPlanUIExtensionPair(transition.getInState().getParentPlan().getId()).getUiElement(transition.getId()).getBendPoints()) {
            transitionViewModel.addBendpoint((BendPointViewModel) viewModelManager.getViewModelElement(bendPoint));
        }
        return transitionViewModel;
    }
}
