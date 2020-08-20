package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.Task;
import de.unikassel.vs.alica.planDesigner.view.model.TaskRepositoryViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.TaskViewModel;

public class TaskViewModelFactory extends InternalViewModelFactory<TaskViewModel, Task>{

    TaskViewModel create(Task task) {
        TaskViewModel taskViewModel = new TaskViewModel(task.getId(), task.getName());
        taskViewModel.setComment(task.getComment());
        taskViewModel.setTaskRepositoryViewModel((TaskRepositoryViewModel) viewModelManager.getViewModelElement(task.getTaskRepository()));
        taskViewModel.getTaskRepositoryViewModel().addTask(taskViewModel);
        taskViewModel.setParentId(task.getTaskRepository().getId());
        return taskViewModel;
    }
}
