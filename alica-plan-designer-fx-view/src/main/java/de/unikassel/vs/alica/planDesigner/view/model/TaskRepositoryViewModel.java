package de.unikassel.vs.alica.planDesigner.view.model;

import de.unikassel.vs.alica.planDesigner.view.Types;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;

public class TaskRepositoryViewModel extends SerializableViewModel {

    private ObservableList<TaskViewModel> tasks;

    public TaskRepositoryViewModel(long id, String name) {
        super(id, name, Types.TASKREPOSITORY);
        tasks = FXCollections.observableArrayList(new ArrayList<>());

        this.uiPropertyList.clear();
        this.uiPropertyList.addAll(Arrays.asList("name", "id", "comment"));
    }

    public void addTask(TaskViewModel task) {
        if (!this.tasks.contains(task)) {
            this.tasks.add(task);
        }
    }

    public void removeTask(long id) {
        for(ViewModelElement task : tasks) {
            if(task.getId() == id) {
                this.tasks.remove(task);
                break;
            }
        }
    }

    public ObservableList<TaskViewModel> getTaskViewModels() {
        return tasks;
    }
}
