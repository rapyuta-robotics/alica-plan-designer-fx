package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.Task;
import de.unikassel.vs.alica.planDesigner.alicamodel.TaskRepository;
import de.unikassel.vs.alica.planDesigner.view.model.TaskRepositoryViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.TaskViewModel;

public class TaskRepositoryViewModelFactory extends InternalViewModelFactory<TaskRepositoryViewModel, TaskRepository> {
    @Override
    TaskRepositoryViewModel create(TaskRepository taskRepository) {
        TaskRepositoryViewModel taskRepositoryViewModel = new TaskRepositoryViewModel(taskRepository.getId(), taskRepository.getName());
        taskRepositoryViewModel.setComment(taskRepository.getComment());
        taskRepositoryViewModel.setRelativeDirectory(taskRepository.getRelativeDirectory());
        // we need to put the repo before creating tasks, in order to avoid circles (Task <-> Repo)
        viewModelManager.putViewModelForAvoidingLoops(taskRepositoryViewModel);
        for (Task task : taskRepository.getTasks()) {
            TaskViewModel taskViewModel = (TaskViewModel) viewModelManager.getViewModelElement(task);
            taskViewModel.setParentId(taskRepository.getId());
            taskRepositoryViewModel.addTask(taskViewModel);
        }
        return taskRepositoryViewModel;
    }
}
