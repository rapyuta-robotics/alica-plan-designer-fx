package de.uni_kassel.vs.cn.planDesigner.view.editor.tab;

import de.uni_kassel.vs.cn.planDesigner.view.Types;
import de.uni_kassel.vs.cn.planDesigner.view.filebrowser.TreeViewModelElement;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class EditorTabPane extends TabPane {

    private ITabEventHandler tabEventHandler;

    public void openTab(TreeViewModelElement treeViewModelElement) {
        // find tab if it already opened
        Tab openedTab = null;
        for (Tab tab : getTabs()) {
            if (((AbstractEditorTab) tab).getTreeViewModelElement().equals(treeViewModelElement)) {
                openedTab = tab;
            }
        }

        // create new tab if not already opened
        if (openedTab == null) {
            openedTab = createNewTab(treeViewModelElement);
            getTabs().add(openedTab);
            // TODO: send event that new task repository tab was opened
        }

        // make it the selected tab
        getSelectionModel().select(openedTab);
        this.requestFocus();
    }

    private Tab createNewTab(TreeViewModelElement treeViewModelElement) {
        switch (treeViewModelElement.getType()) {
            case Types.MASTERPLAN:
            case Types.PLAN:
                PlanTab planTab = new PlanTab(treeViewModelElement);
                tabEventHandler.handleTabOpenedEvent(planTab);
                return planTab;
            case Types.TASKREPOSITORY:
                TaskRepositoryTab taskTab = new TaskRepositoryTab(treeViewModelElement);
                tabEventHandler.handleTabOpenedEvent(taskTab);
                return taskTab;
            case Types.BEHAVIOUR:
                BehaviourTab behaviourTab = new BehaviourTab(treeViewModelElement);
                tabEventHandler.handleTabOpenedEvent(behaviourTab);
                return behaviourTab;
            default:
                System.err.println("EditorTabPane: Opening tab of type " + treeViewModelElement.getType() + " not implemented!");
                return null;
        }
    }

    public void setTabEventHandler (ITabEventHandler handler) {
        this.tabEventHandler = handler;
    }
}
