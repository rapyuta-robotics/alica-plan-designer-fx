package de.unikassel.vs.alica.planDesigner.view.model;

import javafx.beans.property.SimpleObjectProperty;

public class EntryPointViewModel extends PlanElementViewModel {
    protected StateViewModel state;

    protected final SimpleObjectProperty<TaskViewModel> task = new SimpleObjectProperty<>();

    public EntryPointViewModel(long id, String name, String type) {
        super(id, name, type);
    }

    public StateViewModel getState() {
        return state;
    }

    public void setState(StateViewModel state) {
        this.state = state;
    }

    public TaskViewModel getTask() { return task.get(); }

    public SimpleObjectProperty<TaskViewModel> taskProperty() { return task; }

    public void setTask(TaskViewModel task) { this.task.set(task); }
}
