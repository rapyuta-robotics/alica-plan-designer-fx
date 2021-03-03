package de.unikassel.vs.alica.planDesigner.view.model;

import de.unikassel.vs.alica.planDesigner.view.Types;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;

public class PlanTypeViewModel extends AbstractPlanViewModel implements HasVariableBinding {

    private ObservableList<PlanViewModel> allPlans;
    private ObservableList<AnnotatedPlanViewModel> plansInPlanType;
    private ObservableList<VariableBindingViewModel> variableBindings;


    public PlanTypeViewModel(long id, String name) {
        super(id, name, Types.PLANTYPE);
        allPlans = FXCollections.observableArrayList();
        plansInPlanType = FXCollections.observableArrayList();
        variableBindings = FXCollections.observableArrayList();

        this.uiPropertyList.clear();
        this.uiPropertyList.addAll(Arrays.asList("name", "id", "comment", "relativeDirectory"));
    }

    public void addPlanToAllPlans(PlanViewModel plan) {
        if (!containsPlan(plan.getId())) {
            allPlans.add(plan);
        }
    }

    public void removePlanFromAllPlans(long id) {
        for (ViewModelElement element : allPlans) {
            if (element.getId() == id) {
                allPlans.remove(element);
                break;
            }
        }
    }

    public ObservableList<PlanViewModel> getAllPlans() {
        return allPlans;
    }

    public ObservableList<AnnotatedPlanViewModel> getPlansInPlanType() {
        return plansInPlanType;
    }

    public void setRelativeDirectory(String relativeDirectory) {
        this.relativeDirectory.set(relativeDirectory);
    }

    public String getRelativeDirectory() {
        return this.relativeDirectory.get();
    }

    /**
     * Checks whether the plan is already in the plantype as annotated plan.
     * @param id
     * @return
     */
    public boolean containsPlan(long id) {
        for (AnnotatedPlanViewModel annotatedPlan : plansInPlanType) {
            if (annotatedPlan.getPlanId() == id) {
                return true;
            }
        }
        return false;
    }

    public ObservableList<VariableBindingViewModel> getVariableBindings() {
        return variableBindings;
    }

    public void addVariableBinding(VariableBindingViewModel binding) {
        if(!this.variableBindings.contains(binding)) {
            this.variableBindings.add(binding);
        }
    }

    public void removeVariableBinding(VariableBindingViewModel binding) {
        this.variableBindings.remove(binding);
    }
}
