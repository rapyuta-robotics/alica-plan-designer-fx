package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.Characteristic;
import de.unikassel.vs.alica.planDesigner.alicamodel.Role;
import de.unikassel.vs.alica.planDesigner.alicamodel.Task;
import de.unikassel.vs.alica.planDesigner.view.model.CharacteristicViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.RoleSetViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.RoleViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.TaskViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class RoleViewModelFactory extends InternalViewModelFactory<RoleViewModel, Role> {
    @Override
    RoleViewModel create(Role role) {
        RoleViewModel roleViewModel = new RoleViewModel(role.getId(), role.getName());
        ObservableMap<TaskViewModel, Float> taskPriorities = FXCollections.observableHashMap();

        for (Task task: role.getTaskPriorities().keySet()) {
            Task resolvedTask = (Task) resolveDummy(task);
            TaskViewModel taskViewModel = (TaskViewModel)viewModelManager.getViewModelElement(resolvedTask);
            taskPriorities.put( taskViewModel, role.getTaskPriorities().get(resolvedTask));
        }

        for (Characteristic characteristic : role.getCharacteristics()) {
            CharacteristicViewModel characteristicViewModel = (CharacteristicViewModel)viewModelManager.getViewModelElement(characteristic);
            characteristicViewModel.setRoleViewModel(roleViewModel);
            characteristicViewModel.getRoleViewModel().addRoleCharacteristic(characteristicViewModel);
        }

        roleViewModel.setTaskPrioritieViewModels(taskPriorities);
        roleViewModel.setRoleSetViewModel((RoleSetViewModel) viewModelManager.getViewModelElement(role.getRoleSet()));
        return roleViewModel;
    }
}
