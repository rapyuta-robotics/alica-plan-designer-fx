package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.Behaviour;
import de.unikassel.vs.alica.planDesigner.alicamodel.Variable;
import de.unikassel.vs.alica.planDesigner.view.model.BehaviourViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.ConditionViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.VariableViewModel;

public class BehaviourViewModelFactory extends InternalViewModelFactory<BehaviourViewModel, Behaviour> {

    public BehaviourViewModel create(Behaviour behaviour) {
        BehaviourViewModel behaviourViewModel = new BehaviourViewModel(behaviour.getId(), behaviour.getName());
        behaviourViewModel.setComment(behaviour.getComment());
        behaviourViewModel.setRelativeDirectory(behaviour.getRelativeDirectory());
        behaviourViewModel.setFrequency(behaviour.getFrequency());
        behaviourViewModel.setDeferring(behaviour.getDeferring());
        behaviourViewModel.setEventDriven(behaviour.isEventDriven());

        for (Variable variable : behaviour.getVariables()) {
            behaviourViewModel.getVariables().add((VariableViewModel) viewModelManager.getViewModelElement(variable));
        }

        if (behaviour.getPreCondition() != null) {
            ConditionViewModel preConditionViewModel = (ConditionViewModel) viewModelManager.getViewModelElement(behaviour.getPreCondition());
            preConditionViewModel.setParentId(behaviour.getId());
            behaviourViewModel.setPreCondition(preConditionViewModel);
        }

        if (behaviour.getRuntimeCondition() != null) {
            ConditionViewModel runtimeConditionViewModel = (ConditionViewModel) viewModelManager.getViewModelElement(behaviour.getRuntimeCondition());
            runtimeConditionViewModel.setParentId(behaviour.getId());
            behaviourViewModel.setRuntimeCondition(runtimeConditionViewModel);
        }

        if (behaviour.getPostCondition() != null) {
            ConditionViewModel postConditionViewModel = (ConditionViewModel) viewModelManager.getViewModelElement(behaviour.getPostCondition());
            postConditionViewModel.setParentId(behaviour.getId());
            behaviourViewModel.setPostCondition(postConditionViewModel);
        }

        return behaviourViewModel;
    }
}
