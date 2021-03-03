package de.unikassel.vs.alica.planDesigner.alicamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlanType extends AbstractPlan {

    protected final ArrayList<VariableBinding> variableBindings = new ArrayList<>();
    protected final ArrayList<AnnotatedPlan> annotatedPlans = new ArrayList<>();

    public void addParametrisation(VariableBinding variableBinding) {
        variableBindings.add(variableBinding);
        variableBinding.registerDirtyFlag(this.changeListenerForDirtyFlag);
        this.changeListenerForDirtyFlag.setDirty();
    }
    public void removeParametrisation(VariableBinding variableBinding) {
        variableBindings.remove(variableBinding);
        this.changeListenerForDirtyFlag.setDirty();
    }
    public List<VariableBinding> getVariableBindings() {
        return Collections.unmodifiableList(variableBindings);
    }

    public void addAnnotatedPlan(AnnotatedPlan annotatedPlan) {
        annotatedPlans.add(annotatedPlan);
        annotatedPlan.registerDirtyFlag(this.changeListenerForDirtyFlag);
        this.changeListenerForDirtyFlag.setDirty();
    }
    public void removeAnnotatedPlan(AnnotatedPlan annotatedPlan) {
        annotatedPlans.remove(annotatedPlan);
        this.changeListenerForDirtyFlag.setDirty();
    }
    public List<AnnotatedPlan> getAnnotatedPlans() {
        return Collections.unmodifiableList(annotatedPlans);
    }

    public void registerDirtyFlag() {
        super.registerDirtyFlag();
        for (AnnotatedPlan annotatedPlan: annotatedPlans) {
            annotatedPlan.registerDirtyFlag(this.changeListenerForDirtyFlag);
        }
    }
}
