package de.unikassel.vs.alica.planDesigner.view.editor.tab;

import de.unikassel.vs.alica.planDesigner.events.GuiModificationEvent;
import de.unikassel.vs.alica.planDesigner.handlerinterfaces.IGuiModificationHandler;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.behaviourTab.BehaviourTab;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.configurationTab.ConfigurationTab;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.planTab.PlanTab;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.planTypeTab.PlanTypeTab;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.roleTab.RoleSetTab;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.taskRepoTab.TaskRepositoryTab;
import de.unikassel.vs.alica.planDesigner.view.model.SerializableViewModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class EditorTabPane extends TabPane {

    private IGuiModificationHandler guiModificationHandler;

    public EditorTabPane() {
        super();
    }

    public void openTab(SerializableViewModel serializableViewModel) {
        // find tab if it already opened
        Tab openedTab = null;
        for (Tab tab : getTabs()) {
            if (((EditorTab) tab).representsViewModelElement(serializableViewModel)) {
                openedTab = tab;
            }
        }

        // create new tab if not already opened
        if (openedTab == null) {
            openedTab = createNewTab(serializableViewModel);
            getTabs().add(openedTab);
        }

        // make it the selected tab
        getSelectionModel().select(openedTab);
        this.requestFocus();
        this.autosize();
    }

    private Tab createNewTab(SerializableViewModel serializableViewModel) {
        switch (serializableViewModel.getType()) {
            case Types.MASTERPLAN:
            case Types.PLAN:
               return new PlanTab(serializableViewModel, this);
            case Types.TASKREPOSITORY:
                return new TaskRepositoryTab(serializableViewModel, this);
            case Types.TASK:
                return new TaskRepositoryTab((SerializableViewModel) guiModificationHandler.getViewModelElement(serializableViewModel.getParentId()), this);
            case Types.ROLESET:
                return new RoleSetTab(serializableViewModel, this);
            case Types.BEHAVIOUR:
                return new BehaviourTab(serializableViewModel, this);
            case Types.PLANTYPE:
                return new PlanTypeTab(serializableViewModel, this);
            case Types.CONFIGURATION:
                return new ConfigurationTab(serializableViewModel, this);
            default:
                throw new RuntimeException("EditorTabPane: Opening tab of elementType " + serializableViewModel.getType() + " not implemented!");
        }
    }

    public void setGuiModificationHandler(IGuiModificationHandler handler) {
        this.guiModificationHandler = handler;
    }

    public IGuiModificationHandler getGuiModificationHandler() {
        return this.guiModificationHandler;
    }

    public GuiModificationEvent handleDelete() {
        Tab selectedTab = getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            return null;
        }

        return ((EditorTab) selectedTab).handleDelete();
    }
}
