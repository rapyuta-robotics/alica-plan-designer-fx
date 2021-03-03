package de.unikassel.vs.alica.planDesigner.view.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;

public class StateViewModel extends PlanElementViewModel implements HasVariableBinding {
    protected ObservableList<ConfAbstractPlanWrapperViewModel> confAbstractPlanWrappers;
    protected ObservableList<TransitionViewModel> inTransitions;
    protected ObservableList<TransitionViewModel> outTransitions;
    protected ObjectProperty<ConditionViewModel> postCondition;
    protected ObservableList<VariableBindingViewModel> variableBindings;
    protected EntryPointViewModel entryPoint;

    public StateViewModel(long id, String name, String type) {
        super(id, name, type);
        this.confAbstractPlanWrappers =  FXCollections.observableArrayList(new ArrayList<>());
        this.inTransitions =  FXCollections.observableArrayList(new ArrayList<>());
        this.outTransitions = FXCollections.observableArrayList(new ArrayList<>());
        this.variableBindings = FXCollections.observableArrayList(new ArrayList<>());
        this.postCondition = new SimpleObjectProperty<>();

        this.uiPropertyList.clear();
        this.uiPropertyList.addAll(Arrays.asList("name", "id", "comment"));
    }

    public ObservableList<ConfAbstractPlanWrapperViewModel> getConfAbstractPlanWrappers() {
        return confAbstractPlanWrappers;
    }

    public void addConfAbstractPlanWrapper(ConfAbstractPlanWrapperViewModel confAbstractPlanWrapper) {
        if(!this.confAbstractPlanWrappers.contains(confAbstractPlanWrapper)) {
            this.confAbstractPlanWrappers.add(confAbstractPlanWrapper);
        }
    }

    public void removeConfAbstractPlanWrapper(ConfAbstractPlanWrapperViewModel confAbstractPlanWrapper) {
        this.confAbstractPlanWrappers.remove(confAbstractPlanWrapper);
    }

    public ObservableList<TransitionViewModel> getInTransitions() {
        return inTransitions;
    }

    public void setInTransitions(ObservableList<TransitionViewModel> inTransitions) {
        this.inTransitions = inTransitions;
    }

    public ObservableList<TransitionViewModel> getOutTransitions() {
        return outTransitions;
    }

    public void setOutTransitions(ObservableList<TransitionViewModel> outTransitions) {
        this.outTransitions = outTransitions;
    }

    public EntryPointViewModel getEntryPoint() {
        return entryPoint;
    }

    public void setEntryPoint(EntryPointViewModel entryPoint) {
        this.entryPoint = entryPoint;
    }

    public ConditionViewModel getPostConditionViewModel() {
        return postCondition.get();
    }
    public void setPostCondition(ConditionViewModel postCondition) {
        this.postCondition.set(postCondition);
    }
    public ObjectProperty<ConditionViewModel> postConditionProperty() {
        return postCondition;
    }

    public ObservableList getVariableBindings() {
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
