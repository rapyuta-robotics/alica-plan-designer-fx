package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.*;
import de.unikassel.vs.alica.planDesigner.view.model.*;

public class PlanTypeViewModelFactory extends InternalViewModelFactory<PlanTypeViewModel, PlanType> {
    @Override
    PlanTypeViewModel create(PlanType planType) {
        PlanTypeViewModel planTypeViewModel = new PlanTypeViewModel(planType.getId(), planType.getName());
        planTypeViewModel.setRelativeDirectory(planType.getRelativeDirectory());
        planTypeViewModel.setComment(planType.getComment());

        // Putting the PlanType into the map, before all fields and related elements are set, prevents an endless
        // recursion, which would otherwise occur, whenever any plan (or, to be precise, a plans state) contains the
        // PlanType, because each PlanType contains all Plans in a list
        viewModelManager.putViewModelForAvoidingLoops(planTypeViewModel);

        for (Plan plan : modelManager.getPlans()) {
            planTypeViewModel.addPlanToAllPlans((PlanViewModel) viewModelManager.getViewModelElement(plan));
        }

        for (AnnotatedPlan annotatedPlan : planType.getAnnotatedPlans()) {
            planTypeViewModel.removePlanFromAllPlans(annotatedPlan.getPlan().getId());
            planTypeViewModel.getPlansInPlanType().add((AnnotatedPlanViewModel) viewModelManager.getViewModelElement(annotatedPlan));
        }

        for (VariableBinding param: planType.getVariableBindings()) {
            planTypeViewModel.addVariableBinding((VariableBindingViewModel) viewModelManager.getViewModelElement(param));
        }

        for (Variable var : planType.getVariables()) {
            planTypeViewModel.getVariables().add((VariableViewModel) viewModelManager.getViewModelElement(var));
        }

        return planTypeViewModel;
    }
}
