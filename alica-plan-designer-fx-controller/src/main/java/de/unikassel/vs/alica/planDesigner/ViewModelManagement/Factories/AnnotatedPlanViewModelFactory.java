package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.AnnotatedPlan;
import de.unikassel.vs.alica.planDesigner.alicamodel.Plan;
import de.unikassel.vs.alica.planDesigner.view.model.AnnotatedPlanViewModel;

public class AnnotatedPlanViewModelFactory extends InternalViewModelFactory<AnnotatedPlanViewModel, AnnotatedPlan> {
    @Override
    AnnotatedPlanViewModel create(AnnotatedPlan annotatedPlan) {
        // The AnnotatedPlan may still be holding a place-holder-plan, that was created during deserialization, to get
        // the actual plan the place-holder needs to be resolved
        Plan plan = (Plan) resolveDummy(annotatedPlan.getPlan());
        return new AnnotatedPlanViewModel(annotatedPlan.getId(), plan.getName(), annotatedPlan
                .isActivated(), plan.getId());
    }
}
