package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.*;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.model.*;

public class PlanViewModelFactory extends InternalViewModelFactory<PlanViewModel, Plan> {
    @Override
    PlanViewModel create(Plan plan) {
        PlanViewModel planViewModel;
        if (plan.getMasterPlan()) {
            planViewModel = new PlanViewModel(plan.getId(), plan.getName(), Types.MASTERPLAN);
        } else {
            planViewModel = new PlanViewModel(plan.getId(), plan.getName(), Types.PLAN);
        }
        planViewModel.setMasterPlan(plan.getMasterPlan());
        planViewModel.setUtilityThreshold(plan.getUtilityThreshold());
        planViewModel.setComment(plan.getComment());
        planViewModel.setRelativeDirectory(plan.getRelativeDirectory());

        for (Variable var : plan.getVariables()) {
            VariableViewModel variableViewModel = (VariableViewModel) viewModelManager.getViewModelElement(var);
            variableViewModel.setParentId(plan.getId());
            planViewModel.getVariables().add(variableViewModel);
        }
        for (State state : plan.getStates()) {
            StateViewModel stateViewModel = (StateViewModel) viewModelManager.getViewModelElement(state);
            stateViewModel.setParentId(plan.getId());
            planViewModel.getStates().add(stateViewModel);
        }
        for (EntryPoint ep : plan.getEntryPoints()) {
            EntryPointViewModel entryPointViewModel = (EntryPointViewModel) viewModelManager.getViewModelElement(ep);
            entryPointViewModel.setParentId(plan.getId());
            planViewModel.getEntryPoints().add(entryPointViewModel);
        }
        for (Transition transition : plan.getTransitions()) {
            TransitionViewModel transitionViewModel = (TransitionViewModel) viewModelManager.getViewModelElement(transition);
            transitionViewModel.setParentId(plan.getId());
            planViewModel.getTransitions().add(transitionViewModel);
        }
        for (Synchronisation synchronisation : plan.getSynchronisations()) {
            SynchronisationViewModel synchronisationViewModel = (SynchronisationViewModel) viewModelManager.getViewModelElement(synchronisation);
            synchronisationViewModel.setParentId(plan.getId());
            planViewModel.getSynchronisations().add(synchronisationViewModel);
        }
        if (plan.getPreCondition() != null) {
            ConditionViewModel conditionViewModel = (ConditionViewModel) viewModelManager.getViewModelElement(plan.getPreCondition());
            conditionViewModel.setParentId(plan.getId());
            planViewModel.setPreCondition(conditionViewModel);
        }
        if (plan.getRuntimeCondition() != null) {
            ConditionViewModel conditionViewModel = (ConditionViewModel) viewModelManager.getViewModelElement(plan.getRuntimeCondition());
            conditionViewModel.setParentId(plan.getId());
            planViewModel.setRuntimeCondition(conditionViewModel);
        }

        return planViewModel;
    }
}
