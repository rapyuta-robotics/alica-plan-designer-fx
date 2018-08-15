package de.uni_kassel.vs.cn.planDesigner.view.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class PlanTypeViewModel extends PlanElementViewModel {
    private ObservableList<ViewModelElement> allPlans;
    private ObservableList<AnnotatedPlanView> plansInPlanType;

    public PlanTypeViewModel(long id, String name, String type) {
        super(id, name, type);
        allPlans = FXCollections.observableArrayList(new ArrayList<>());
        plansInPlanType = FXCollections.observableArrayList(new ArrayList<>());
    }

    public void addPlanToAllPlans(ViewModelElement plan) {
        allPlans.add(plan);
    }

    public void addPlanToPlansInPlanType(AnnotatedPlanView plan) {
        plansInPlanType.add(plan);
    }

    public void clearAllPlans() {
        allPlans.clear();
    }

    public void clearPlansInPlanType() {
        plansInPlanType.clear();
    }

    public ObservableList<ViewModelElement> getAllPlans() {
        return allPlans;
    }

    public ObservableList<AnnotatedPlanView> getPlansInPlanType() {
        return plansInPlanType;
    }

    public void setRelativeDirectory(String relativeDirectory) {
        this.relativeDirectory.set(relativeDirectory);
    }

    public String getRelativeDirectory() {
        return this.relativeDirectory.get();
    }


}
