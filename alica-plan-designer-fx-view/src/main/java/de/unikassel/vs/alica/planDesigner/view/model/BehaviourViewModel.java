package de.unikassel.vs.alica.planDesigner.view.model;

import de.unikassel.vs.alica.planDesigner.handlerinterfaces.IGuiModificationHandler;
import de.unikassel.vs.alica.planDesigner.view.Types;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.Arrays;
import java.util.Map;

public class BehaviourViewModel  extends AbstractPlanViewModel {

    protected SimpleIntegerProperty frequency = new SimpleIntegerProperty(this, "frequency", 0);
    protected SimpleLongProperty deferring = new SimpleLongProperty(this, "deferring", 0);
    protected SimpleBooleanProperty eventDriven = new SimpleBooleanProperty(this, "eventDriven", false);

    protected final SimpleObjectProperty<ConditionViewModel> preCondition = new SimpleObjectProperty<>(this, Types.PRECONDITION, null);
    protected final SimpleObjectProperty<ConditionViewModel> runtimeCondition = new SimpleObjectProperty<>(this, Types.RUNTIMECONDITION, null);
    protected final SimpleObjectProperty<ConditionViewModel> postCondition = new SimpleObjectProperty<>(this, Types.POSTCONDITION, null);

    public BehaviourViewModel(long id, String name, String type) {
        super(id, name, type);

        this.uiPropertyList.clear();
        this.uiPropertyList.addAll(Arrays.asList("name", "id", "comment", "relativeDirectory", "frequency", "deferring", "eventDriven"));
    }

    public void registerListener(IGuiModificationHandler handler) {
        super.registerListener(handler);
        frequency.addListener((observable, oldValue, newValue) -> {
            fireGUIAttributeChangeEvent(handler, newValue, frequency.getClass().getSimpleName(), frequency.getName());
        });
        deferring.addListener((observable, oldValue, newValue) -> {
            fireGUIAttributeChangeEvent(handler, newValue, deferring.getClass().getSimpleName(), deferring.getName());
        });
        eventDriven.addListener((observable, oldValue, newValue) -> {
            fireGUIAttributeChangeEvent(handler, newValue, eventDriven.getClass().getSimpleName(), eventDriven.getName());
        });
    }

    public void setDeferring(long deferring) {
        this.deferring.set(deferring);
    }
    public long getDeferring() {
        return deferring.get();
    }
    public LongProperty deferringProperty() {
        return deferring;
    }

    public void setFrequency(int frequency) {
        this.frequency.set(frequency);
    }
    public int getFrequency() {
        return frequency.get();
    }
    public IntegerProperty frequencyProperty() {
        return frequency;
    }

    public void setEventDriven(boolean eventDriven) {
        this.eventDriven.set(eventDriven);
    }
    public boolean getEventDriven() {
        return eventDriven.get();
    }
    public BooleanProperty eventDrivenProperty() {
        return eventDriven;
    }

    public ObjectProperty<ConditionViewModel> preConditionProperty(){
        return preCondition;
    }
    public ConditionViewModel getPreCondition() {
        return preCondition.get();
    }
    public void setPreCondition(ConditionViewModel conditionViewModel) {
        this.preCondition.set(conditionViewModel);
    }

    public ObjectProperty<ConditionViewModel> runtimeConditionProperty(){
        return runtimeCondition;
    }
    public ConditionViewModel getRuntimeCondition() {
        return runtimeCondition.get();
    }
    public void setRuntimeCondition(ConditionViewModel conditionViewModel) {
        this.runtimeCondition.set(conditionViewModel);
    }

    public ObjectProperty<ConditionViewModel> posConditionProperty(){
        return postCondition;
    }
    public ConditionViewModel getPostCondition() {
        return postCondition.get();
    }
    public void setPostCondition(ConditionViewModel conditionViewModel) {
        this.postCondition.set(conditionViewModel);
    }
}
