package de.unikassel.vs.alica.planDesigner.view.model;

import de.unikassel.vs.alica.planDesigner.view.Types;

import java.util.Arrays;

public class TaskViewModel extends PlanElementViewModel {
    protected TaskRepositoryViewModel taskRepositoryViewModel;
    public TaskViewModel (long id, String name) {
        super(id, name, Types.TASK);

        this.uiPropertyList.clear();
        this.uiPropertyList.addAll(Arrays.asList("name", "id", "comment"));
    }

    public TaskRepositoryViewModel getTaskRepositoryViewModel() {
        return taskRepositoryViewModel;
    }

    public void setTaskRepositoryViewModel(TaskRepositoryViewModel taskRepositoryViewModel) {
        this.taskRepositoryViewModel = taskRepositoryViewModel;
    }
}
