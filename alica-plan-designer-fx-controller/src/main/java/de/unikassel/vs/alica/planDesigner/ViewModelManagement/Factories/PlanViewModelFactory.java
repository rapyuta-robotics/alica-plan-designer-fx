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
            planViewModel.getVariables().add((VariableViewModel) viewModelManager.getViewModelElement(var));
        }
        for (State state : plan.getStates()) {
            planViewModel.getStates().add(
                    (StateViewModel) viewModelManager.getViewModelElement(state));
        }
        for (EntryPoint ep : plan.getEntryPoints()) {
            planViewModel.getEntryPoints().add((EntryPointViewModel) viewModelManager.getViewModelElement(ep));
        }
        for (Transition transition : plan.getTransitions()) {
            planViewModel.getTransitions().add((TransitionViewModel) viewModelManager.getViewModelElement(transition));
        }
        for (Synchronisation synchronisation : plan.getSynchronisations()) {
            planViewModel.getSynchronisations().add((SynchronisationViewModel) viewModelManager.getViewModelElement(synchronisation));
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
