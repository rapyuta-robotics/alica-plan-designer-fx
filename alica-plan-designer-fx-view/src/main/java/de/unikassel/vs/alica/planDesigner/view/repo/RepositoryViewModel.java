package de.unikassel.vs.alica.planDesigner.view.repo;

import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.model.RoleSetViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.TaskRepositoryViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.ViewModelElement;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * This class functions as backend for the repository view.
 * <p>
 * This class containsPlan Lists of all Plans, PlanTypes, Behaviours and Tasks as ViewModelElement
 */
public final class RepositoryViewModel {

    private ObservableList<ViewModelElement> plans;
    private ObservableList<ViewModelElement> planTypes;
    private ObservableList<ViewModelElement> behaviours;
    private ObservableList<ViewModelElement> tasks;
    private ObservableList<ViewModelElement> configurations;

    private RepositoryTabPane repositoryTabPane;

    public RepositoryViewModel() {
        plans = FXCollections.observableArrayList(new ArrayList<>());
        planTypes = FXCollections.observableArrayList(new ArrayList<>());
        behaviours = FXCollections.observableArrayList(new ArrayList<>());
        tasks = FXCollections.observableArrayList(new ArrayList<>());
        configurations = FXCollections.observableArrayList(new ArrayList<>());
    }

    public void initGuiContent() {
        if (repositoryTabPane == null) {
            return;
        }

        repositoryTabPane.clearGuiContent();
        repositoryTabPane.addPlans(plans);
        repositoryTabPane.addPlanTypes(planTypes);
        repositoryTabPane.addTasks(tasks);
        repositoryTabPane.addBehaviours(behaviours);
        repositoryTabPane.addConfigurations(configurations);
        initListeners();
    }

    public void clear() {
        plans.clear();
        planTypes.clear();
        behaviours.clear();
        tasks.clear();
        configurations.clear();
    }

    public void initListeners() {
        plans.addListener(new ListChangeListener<ViewModelElement>() {
            @Override
            public void onChanged(Change<? extends ViewModelElement> c) {
                repositoryTabPane.clearPlansTab();
                repositoryTabPane.addPlans(plans);
            }
        });
        planTypes.addListener(new ListChangeListener<ViewModelElement>() {
            @Override
            public void onChanged(Change<? extends ViewModelElement> c) {
                repositoryTabPane.clearPlanTypesTab();
                repositoryTabPane.addPlanTypes(planTypes);
            }
        });
        behaviours.addListener(new ListChangeListener<ViewModelElement>() {
            @Override
            public void onChanged(Change<? extends ViewModelElement> c) {
                repositoryTabPane.clearBehavioursTab();
                repositoryTabPane.addBehaviours(behaviours);
            }
        });
        tasks.addListener(new ListChangeListener<ViewModelElement>() {
            @Override
            public void onChanged(Change<? extends ViewModelElement> c) {
                repositoryTabPane.clearTasksTab();
                repositoryTabPane.addTasks(tasks);
            }
        });
        configurations.addListener(new ListChangeListener<ViewModelElement>() {
            @Override
            public void onChanged(Change<? extends ViewModelElement> c) {
                repositoryTabPane.clearConfigurationsTab();
                repositoryTabPane.addConfigurations(configurations);
            }
        });
    }

    public void setRepositoryTabPane(RepositoryTabPane repositoryTabPane) {
        this.repositoryTabPane = repositoryTabPane;
    }

    public ObservableList<ViewModelElement> getPlans() {
        return plans;
    }
    public ObservableList<ViewModelElement> getTasks(){
        return tasks;
    }
    public ObservableList<ViewModelElement> getBehaviours() {return behaviours; }
    public ObservableList<ViewModelElement> getPlanTypes() {return planTypes; }
    public ObservableList<ViewModelElement> getConfigurations() {return configurations; }

    public void addElement(ViewModelElement viewModelElement) {
        switch (viewModelElement.getType()) {
            case Types.MASTERPLAN:
            case Types.PLAN:
                this.plans.add(viewModelElement);
                break;
            case Types.BEHAVIOUR:
                this.behaviours.add(viewModelElement);
                break;
            case Types.PLANTYPE:
                this.planTypes.add(viewModelElement);
                break;
            case Types.TASK:
                this.tasks.add(viewModelElement);
                break;
            case Types.TASKREPOSITORY:
                this.tasks.clear();
                for (ViewModelElement task : ((TaskRepositoryViewModel) viewModelElement).getTaskViewModels()) {
                    this.tasks.add(task);
                }
                break;
            case Types.CONFIGURATION:
                this.configurations.add(viewModelElement);
                break;
        }
    }

    public void removeElement(ViewModelElement viewModelElement) {
        switch (viewModelElement.getType()) {
            case Types.MASTERPLAN:
            case Types.PLAN:
                this.plans.remove(viewModelElement);
                break;
            case Types.BEHAVIOUR:
                this.behaviours.remove(viewModelElement);
                break;
            case Types.PLANTYPE:
                this.planTypes.remove(viewModelElement);
                break;
            case Types.TASK:
                this.tasks.remove(viewModelElement);
                break;
            case Types.TASKREPOSITORY:
                this.tasks.clear();
                break;
            case Types.CONFIGURATION:
                this.configurations.remove(viewModelElement);
                break;
        }
    }
}
