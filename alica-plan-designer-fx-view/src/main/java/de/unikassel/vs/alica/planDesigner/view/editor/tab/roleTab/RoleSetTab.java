package de.unikassel.vs.alica.planDesigner.view.editor.tab.roleTab;

import com.google.common.collect.ImmutableMap;
import de.unikassel.vs.alica.planDesigner.controller.UsagesWindowController;
import de.unikassel.vs.alica.planDesigner.events.GuiEventType;
import de.unikassel.vs.alica.planDesigner.events.GuiModificationEvent;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.EditorTab;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.EditorTabPane;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.roleTab.characteristics.CharacteristicsTableElement;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.roleTab.characteristics.CharacteristicsTableView;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.roleTab.roles.RoleTableElement;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.roleTab.roles.RoleTableView;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.roleTab.taskpriorities.TaskPriorityTableElement;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.roleTab.taskpriorities.TaskPriorityTableView;
import de.unikassel.vs.alica.planDesigner.view.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;

import java.util.ArrayList;

public class RoleSetTab extends EditorTab {

    protected RoleSetViewModel          roleSetViewModel;
    protected RoleTableView             roleTableView;
    protected TaskPriorityTableView     taskTableView;
    protected CharacteristicsTableView  characteristicsTableView;

    private ObservableList<TaskPriorityTableElement> taskPriorities = FXCollections.observableArrayList();
    private ObservableList<CharacteristicsTableElement> characteristics = FXCollections.observableArrayList();

    public RoleSetTab(SerializableViewModel serializableViewModel, EditorTabPane editorTabPane) {
        super(serializableViewModel, editorTabPane.getGuiModificationHandler());
        editorTabPane.getSelectionModel().selectedItemProperty().addListener((observable, selectedTabBefore, selectedTab) -> {

            if (this == selectedTab) {
                this.elementInformationPane.setViewModelElement(roleSetViewModel);
            }
        });
        editorTabPane.focusedProperty().addListener((observable, focusedBefore, focused) -> {

            if (focused && editorTabPane.getSelectionModel().getSelectedItem() == this) {
                this.elementInformationPane.setViewModelElement(roleSetViewModel);
            }
        });
        roleSetViewModel = (RoleSetViewModel) serializableViewModel;
        roleSetViewModel.getRoleViewModels().addListener((ListChangeListener<RoleViewModel>) c -> {

            while(c.next()) {

                if (c.wasAdded()) {

                    for (RoleViewModel role : c.getAddedSubList()) {
                        roleTableView.addElement(role);
                    }
                }

                if (c.wasRemoved()) {

                    for (RoleViewModel role : c.getRemoved()) {
                        roleTableView.removeElement(role);
                    }
                }
            }
        });

        roleTableView = new RoleTableView(roleSetViewModel);
        roleTableView.setGuiModificationHandler(editorTabPane.getGuiModificationHandler());
        roleTableView.addElements(roleSetViewModel.getRoleViewModels());

        roleTableView.addSelectionListener(evt -> {

            if (evt.getNewValue()!= null) {
                elementInformationPane.setViewModelElement(((RoleTableElement)evt.getNewValue()).getViewModelElement());
                taskTableView.updateSelectedRole(roleTableView.getSelectedItem());
                characteristicsTableView.updateSelectedRole(roleTableView.getSelectedItem());
            }
        });
        createVisuals();
    }

    private void createVisuals() {
        VBox roleTableVisual = createRoleTableVisual();
        TaskPriorityTableView taskPriorityTableView = createTaskPriorityTableVisual();
        CharacteristicsTableView characteristicsTableView = createCharacteristicsTableVisual();

        VBox splacer1 = new VBox();
        splacer1.setPadding(new Insets(5,35,5,25));
        VBox splacer2 = new VBox();
        splacer2.setPadding(new Insets(5,35,5,25));

        HBox roleTaskPriorityListVisual = new HBox(roleTableVisual, splacer1, taskPriorityTableView, splacer2, characteristicsTableView );
//        VBox roleSetVisual = new VBox(roleTaskPriorityListVisual, createRoleVisual);
        VBox roleSetVisual = new VBox(roleTaskPriorityListVisual);
        HBox.setHgrow(roleTaskPriorityListVisual, Priority.ALWAYS);
        HBox.setHgrow(roleSetVisual, Priority.ALWAYS);
        roleSetVisual.setPrefHeight(Double.MAX_VALUE);
        roleTableView.setFocus();
        splitPane.getItems().add(0, roleSetVisual);
    }


    private VBox createRoleTableVisual() {
        roleTableView.addColumn(i18NRepo.getString("label.caption.roles"), "name", new DefaultStringConverter(), true);
        roleTableView.getColumns().get(roleTableView.getColumns().size()-1).setMinWidth(100.0);
        roleTableView.getColumns().get(roleTableView.getColumns().size()-1).setSortable(false);
        roleTableView.prefHeightProperty().bind(splitPane.heightProperty());
        roleTableView.setMinWidth(121.0);
        roleTableView.setEditable(true);
        roleTableView.requestFocus();
        roleTableView.setGuiModificationHandler(this.guiModificationHandler);
        roleTableView.initTable(roleSetViewModel);
        roleTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox roleSetVisual = new VBox(roleTableView);
        return roleSetVisual;
    }

    private TaskPriorityTableView createTaskPriorityTableVisual() {
        taskTableView = new TaskPriorityTableView(roleSetViewModel.getDefaultPriority());
        taskTableView.addColumn(i18NRepo.getString("label.caption.tasks"), "taskName",new DefaultStringConverter(), false);
        taskTableView.getColumns().get(taskTableView.getColumns().size()-1).setMinWidth(150.0);
        taskTableView.getColumns().get(taskTableView.getColumns().size()-1).setSortable(false);
        taskTableView.addColumn(i18NRepo.getString("label.caption.priorities"), "priority",new DefaultStringConverter(), true);
        taskTableView.getColumns().get(taskTableView.getColumns().size()-1).setStyle("-fx-alignment: CENTER;");
        taskTableView.getColumns().get(taskTableView.getColumns().size()-1).setMaxWidth(150.0);
        taskTableView.getColumns().get(taskTableView.getColumns().size()-1).setMinWidth(100.0);
        taskTableView.getColumns().get(taskTableView.getColumns().size()-1).setSortable(false);
        taskTableView.prefHeightProperty().bind(splitPane.heightProperty());
        taskTableView.prefWidthProperty().bind(splitPane.widthProperty());
        taskTableView.setEditable(true);

        taskTableView.addListener(evt -> {
            String id = String.valueOf(evt.getOldValue());
            String value = (String) evt.getNewValue();
            RoleViewModel roleViewModel = (RoleViewModel)evt.getSource();
            GuiModificationEvent event = new GuiModificationEvent(GuiEventType.CHANGE_ELEMENT, Types.ROLE_TASK_PROPERTY, "taskPriority");
            event.setRelatedObjects(ImmutableMap.<String, Long>of( value, Long.parseLong(id)));
            event.setElementId(roleViewModel.getId());
            guiModificationHandler.handle(event);
        });

        TaskRepositoryViewModel taskRepositoryViewModel = (TaskRepositoryViewModel) guiModificationHandler.getViewModelElement(guiModificationHandler.getTaskRepositoryID());
        if (taskRepositoryViewModel != null)
            taskTableView.addTasks(taskRepositoryViewModel.getTaskViewModels());
        taskTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return taskTableView;
    }

    private CharacteristicsTableView createCharacteristicsTableVisual() {
        characteristicsTableView = new CharacteristicsTableView(roleSetViewModel, roleTableView);
        characteristicsTableView.addColumn(i18NRepo.getString("label.caption.characteristics"), "name",new DefaultStringConverter(), true);
        characteristicsTableView.getColumns().get(characteristicsTableView.getColumns().size()-1).setMinWidth(200.0);
        characteristicsTableView.getColumns().get(characteristicsTableView.getColumns().size()-1).setSortable(false);
        characteristicsTableView.addColumn(i18NRepo.getString("label.caption.value"), "value",new DefaultStringConverter(), true);
        characteristicsTableView.getColumns().get(characteristicsTableView.getColumns().size()-1).setMinWidth(100.0);
        characteristicsTableView.getColumns().get(characteristicsTableView.getColumns().size()-1).setSortable(false);
        characteristicsTableView.addColumn(i18NRepo.getString("label.caption.weight"), "weight",new DefaultStringConverter(), true);
        characteristicsTableView.getColumns().get(characteristicsTableView.getColumns().size()-1).setStyle("-fx-alignment: CENTER;");
        characteristicsTableView.getColumns().get(characteristicsTableView.getColumns().size()-1).setMaxWidth(250.0);
        characteristicsTableView.getColumns().get(characteristicsTableView.getColumns().size()-1).setMinWidth(100.0);
        characteristicsTableView.getColumns().get(characteristicsTableView.getColumns().size()-1).setSortable(false);
        characteristicsTableView.prefHeightProperty().bind(splitPane.heightProperty());
        characteristicsTableView.prefWidthProperty().bind(splitPane.widthProperty());
        characteristicsTableView.setEditable(true);
        characteristicsTableView.setGuiModificationHandler(this.guiModificationHandler);

        characteristicsTableView.addListener(evt -> {
            String id = String.valueOf(evt.getOldValue());
            String value = (String) evt.getNewValue();
            System.out.println("RST: listener " + id + " " + value);
            RoleViewModel roleViewModel = (RoleViewModel)evt.getSource();
            GuiModificationEvent event = new GuiModificationEvent(GuiEventType.CHANGE_ELEMENT, Types.ROLE_CHARCTERISTIC, "characteristics");
            event.setRelatedObjects(ImmutableMap.<String, Long>of( value, Long.parseLong(id)));
            event.setElementId(roleViewModel.getId());
            guiModificationHandler.handle(event);
            characteristicsTableView.updatePlaceholder();
        });

        characteristicsTableView.initTable(roleTableView, roleSetViewModel);
        characteristicsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return characteristicsTableView;
    }

    private HBox createRoleButtonVisual() {
        HBox createRoleHBox = new HBox();

        Button createButton = new Button();
        createButton.setText(i18NRepo.getString("action.create.role"));

        TextField textField = new TextField();
        createButton.setOnAction(e -> {

            if (textField.getText() != null && !textField.getText().isEmpty()) {
                GuiModificationEvent event = new GuiModificationEvent(GuiEventType.CREATE_ELEMENT, Types.ROLE, textField.getText());
                event.setParentId(this.roleSetViewModel.getId());
                guiModificationHandler.handle(event);
            }
        });
        createRoleHBox.getChildren().addAll(textField, createButton);
        return createRoleHBox;
    }

    public void save() {
        save(Types.ROLESET);
    }

    public GuiModificationEvent handleDelete() {
        ViewModelElement elementToDelete = roleTableView.getSelectedItem();
        if (elementToDelete == null) {
            return null;
        }

        if (!isRoleUsed(elementToDelete)) {
            GuiModificationEvent event = new GuiModificationEvent(GuiEventType.DELETE_ELEMENT, Types.ROLE, elementToDelete.getName());
            event.setElementId(elementToDelete.getId());
            event.setParentId(roleSetViewModel.getId());
            return event;
        } else {
            return null;
        }
    }

    private boolean isRoleUsed(ViewModelElement roleToBeDeleted) {
        ArrayList<ViewModelElement> usages = guiModificationHandler.getUsages(roleToBeDeleted);
        if (usages.isEmpty()) {
            return false;
        }
        UsagesWindowController.createUsagesWindow(usages, i18NRepo.getString("label.usage.nodelete"), guiModificationHandler);
        return true;
    }
}
