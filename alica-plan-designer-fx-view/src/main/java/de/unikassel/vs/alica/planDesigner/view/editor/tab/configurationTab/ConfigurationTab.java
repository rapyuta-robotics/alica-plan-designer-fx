package de.unikassel.vs.alica.planDesigner.view.editor.tab.configurationTab;

import de.unikassel.vs.alica.planDesigner.controller.MainWindowController;
import de.unikassel.vs.alica.planDesigner.events.GuiChangeAttributeEvent;
import de.unikassel.vs.alica.planDesigner.events.GuiEventType;
import de.unikassel.vs.alica.planDesigner.events.GuiModificationEvent;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.EditorTab;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.EditorTabPane;
import de.unikassel.vs.alica.planDesigner.view.model.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.AbstractMap;
import java.util.Map;

public class ConfigurationTab extends EditorTab {

    protected ConfigurationViewModel configurationViewModel;
    private TableView<Map.Entry<String, String>> parameterTableView;
    private ConfigurationListener configurationListener;

    public ConfigurationTab(SerializableViewModel serializableViewModel, EditorTabPane editorTabPane) {
        super(serializableViewModel, editorTabPane.getGuiModificationHandler());

        editorTabPane.getSelectionModel().selectedItemProperty().addListener((observable, selectedTabBefore, selectedTab) -> {
            if (this == selectedTab) {
                this.elementInformationPane.setViewModelElement(configurationViewModel);
            }
        });
        editorTabPane.focusedProperty().addListener((observable, focusedBefore, focused) -> {
            if (focused && editorTabPane.getSelectionModel().getSelectedItem() == this) {
                this.elementInformationPane.setViewModelElement(configurationViewModel);
            }
        });

        configurationViewModel = (ConfigurationViewModel) serializableViewModel;

        draw();
    }

    private void draw() {
        // Table
        ObservableList<Map.Entry<String, String>> items = FXCollections.observableArrayList();
        parameterTableView = new TableView<>(items);
        parameterTableView.setFixedCellSize(ConfigurationListener.CELL_SIZE);
        parameterTableView.setPlaceholder(new Text());
        parameterTableView.setEditable(true);

        // Key Column (for edit key)
        TableColumn<Map.Entry<String, String>, String> keyColumn = new TableColumn<>("Key");
        keyColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getKey()));
        keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        keyColumn.setEditable(true);
        keyColumn.setOnEditCommit(event -> {
            System.out.println("ConfigurationTab: Key RowValue: " + event.getRowValue() + " OldValue: " + event.getOldValue() + " NewValue: " + event.getNewValue());
            if (!fireEvent(new AbstractMap.SimpleEntry<String,String>(event.getNewValue(), event.getRowValue().getValue()), event.getRowValue())) {
                parameterTableView.refresh();
            }
            event.consume();
        });
        parameterTableView.getColumns().add(keyColumn);

        // Value Column (for edit value)
        TableColumn<Map.Entry<String, String>, String> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getValue()));
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setEditable(true);
        valueColumn.setOnEditCommit(event -> {
            System.out.println("ConfigurationTab: Value RowValue: " + event.getRowValue() + " OldValue: " + event.getOldValue() + " NewValue: " + event.getNewValue());
            if (!fireEvent(new AbstractMap.SimpleEntry<String,String>(event.getRowValue().getKey(), event.getNewValue()), event.getRowValue())) {
                parameterTableView.refresh();
            }
            event.consume();
        });
        parameterTableView.getColumns().add(valueColumn);

        // Row Factory (for delete)
        parameterTableView.setRowFactory(new Callback<TableView<Map.Entry<String, String>>, TableRow<Map.Entry<String, String>>>() {
            @Override
            public TableRow<Map.Entry<String, String>> call(TableView<Map.Entry<String, String>> param) {
                final TableRow<Map.Entry<String, String>> row = new TableRow<>();
                final ContextMenu rowMenu = new ContextMenu();
                MenuItem removeItem = new MenuItem("Delete");
                removeItem.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        System.out.println("ConfigurationTab: Delete Key: " + row.getItem().getKey() + " Value: " + row.getItem().getValue());
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                fireEvent(null, row.getItem());
                            }
                        });
                    }
                });
                rowMenu.getItems().addAll(removeItem);

                // only display context menu for non-null items:
                row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu).otherwise((ContextMenu)null));
                return row;
            }
        });

        this.parameterTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox vbox = new VBox();
        vbox.setFillWidth(true);
        vbox.getChildren().add(this.parameterTableView);

        splitPane.getItems().add(0, vbox);


        // listener object for updating tableview
        this.configurationListener = new ConfigurationListener(this.parameterTableView);
    }

    public void save() {
        save(Types.CONFIGURATION);
    }

    public GuiModificationEvent handleDelete() {
        System.err.println("ConfigurationTab: handleDelete() called, but not implemented!");
        return null;
    }

    public void setParentViewModel(ViewModelElement parentViewModel) {
        if (this.configurationViewModel != null) {
            this.configurationViewModel.getParameters().removeListener(this.configurationListener);
        }
        this.configurationViewModel = (ConfigurationViewModel) parentViewModel;
        this.configurationViewModel.getParameters().addListener(this.configurationListener);
    }

    private boolean fireEvent(Map.Entry<String, String> newValue, Map.Entry<String, String> oldValue) {
        GuiChangeAttributeEvent addKeyValueEvent = new GuiChangeAttributeEvent(GuiEventType.CHANGE_ELEMENT, Types.CONFIGURATION, configurationViewModel.getName());
        addKeyValueEvent.setElementId(configurationViewModel.getId());
        addKeyValueEvent.setAttributeType(Map.Entry.class.getSimpleName());
        addKeyValueEvent.setAttributeName("parameters");
        if (!newValue.getKey().equals("")) {
            addKeyValueEvent.setNewValue(newValue);
        }
        if (!oldValue.getKey().equals ("")) {
            addKeyValueEvent.setOldValue(oldValue);
        }
        if (addKeyValueEvent.getNewValue() == null && addKeyValueEvent.getOldValue() == null)
        {
            return false;
        }

        MainWindowController.getInstance().getGuiModificationHandler().handle(addKeyValueEvent);
        return true;
    }
}

