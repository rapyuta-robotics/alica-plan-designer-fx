package de.unikassel.vs.alica.planDesigner.alicamodel;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Synchronisation extends PlanElement{

    protected final SimpleIntegerProperty talkTimeout = new SimpleIntegerProperty();
    protected final SimpleIntegerProperty syncTimeout = new SimpleIntegerProperty();
    protected final SimpleBooleanProperty failOnSyncTimeout = new SimpleBooleanProperty();
    protected final SimpleObjectProperty<Plan> plan = new SimpleObjectProperty<>();

    protected final ArrayList<Transition> syncedTransitions = new ArrayList<>();

    private ChangeListenerForDirtyFlag changeListener;

    public Synchronisation(){}
    public Synchronisation(long id) {
        this.id = id;
    }

    public int getTalkTimeout() {
        return talkTimeout.get();
    }
    public void setTalkTimeout(int talkTimeout) {
        this.talkTimeout.set(talkTimeout);
    }
    public SimpleIntegerProperty talkTimeoutProperty() {
        return talkTimeout;
    }

    public int getSyncTimeout() {
        return syncTimeout.get();
    }
    public void setSyncTimeout(int syncTimeout) {
        this.syncTimeout.set(syncTimeout);
    }
    public SimpleIntegerProperty syncTimeoutProperty() {
        return syncTimeout;
    }

    public void setFailOnSyncTimeout(boolean failOnSyncTimeout) {
        this.failOnSyncTimeout.set(failOnSyncTimeout);
    }
    public boolean getFailOnSyncTimeout() {
        return failOnSyncTimeout.get();
    }
    public SimpleBooleanProperty failOnSyncTimeoutProperty() {
        return failOnSyncTimeout;
    }

    public void setPlan(Plan plan) {
        this.plan.set(plan);
    }
    public Plan getPlan() {
        return this.plan.get();
    }
    public SimpleObjectProperty<Plan> planProperty() {
        return this.plan;
    }

    public void addSyncedTransition(Transition transition) {
        syncedTransitions.add(transition);
        changeListener.setDirty();
    }
    public void removeSyncedTransition(Transition transition) {
        syncedTransitions.remove(transition);
        changeListener.setDirty();
    }
    public List<Transition> getSyncedTransitions() {
        return Collections.unmodifiableList(syncedTransitions);
    }

    public void registerDirtyFlag(ChangeListenerForDirtyFlag listener) {
        this.changeListener = listener;

        talkTimeout.addListener(listener);
        syncTimeout.addListener(listener);
        failOnSyncTimeout.addListener(listener);
    }
}
