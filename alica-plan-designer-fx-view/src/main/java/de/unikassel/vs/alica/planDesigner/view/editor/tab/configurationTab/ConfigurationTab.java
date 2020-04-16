package de.unikassel.vs.alica.planDesigner.view.editor.tab.configurationTab;

import de.unikassel.vs.alica.planDesigner.events.GuiChangeAttributeEvent;
import de.unikassel.vs.alica.planDesigner.events.GuiEventType;
import de.unikassel.vs.alica.planDesigner.events.GuiModificationEvent;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.EditorTab;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.EditorTabPane;
import de.unikassel.vs.alica.planDesigner.view.model.ConfigurationViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.SerializableViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;

import java.util.AbstractMap;
import java.util.Map;

public class ConfigurationTab extends EditorTab {

    protected ConfigurationViewModel configurationViewModel;
    private ConfigurationListener configurationListener;

    private TableView<Map.Entry<String, String>> parameterTableView;

    public ConfigurationTab(SerializableViewModel serializableViewModel, EditorTabPane editorTabPane) {
        super(serializableViewModel, editorTabPane.getGuiModificationHandler());

        // update elementInformationPane, depending on focus and selection of tab
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

        setupUI();

        configurationViewModel = (ConfigurationViewModel) serializableViewModel;
        configurationListener = new ConfigurationListener(parameterTableView);
        configurationViewModel.getParameters().addListener(configurationListener);

        // initial population of table
        configurationListener.updateTable(configurationViewModel.getParameters());
    }

    private void setupUI() {
        // Table
        parameterTableView = new TableView<>();
        parameterTableView.setEditable(true);

        // Key Column (for edit key)
        TableColumn<Map.Entry<String, String>, String> keyColumn = new TableColumn<>("Key");
        keyColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getKey()));
        keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        keyColumn.setEditable(true);
        keyColumn.setOnEditCommit(event -> {
//            System.out.println("ConfigurationTab: Key RowValue: '" + event.getRowValue() + "' OldValue: '" + event.getOldValue() + "' NewValue: '" + event.getNewValue() + "'");
            GuiChangeAttributeEvent guiChangeAttributeEvent = createEvent(new AbstractMap.SimpleEntry<String, String>(event.getNewValue(), event.getRowValue().getValue()), event.getRowValue());
            if (guiChangeAttributeEvent != null) {
                fireEvent(guiChangeAttributeEvent);
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
//            System.out.println("ConfigurationTab: Row RowValue: '" + event.getRowValue() + "' OldValue: '" + event.getOldValue() + "' NewValue: '" + event.getNewValue() + "'");
            GuiChangeAttributeEvent guiChangeAttributeEvent = createEvent(new AbstractMap.SimpleEntry<String, String>(event.getRowValue().getKey(), event.getNewValue()), event.getRowValue());
            if (guiChangeAttributeEvent != null) {
                fireEvent(guiChangeAttributeEvent);
                parameterTableView.refresh();
            }
            event.consume();
        });
        parameterTableView.getColumns().add(valueColumn);

        this.parameterTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox vbox = new VBox();
        vbox.setFillWidth(true);
        vbox.getChildren().add(this.parameterTableView);

        splitPane.getItems().add(0, vbox);
    }

    public void save() {
        save(Types.CONFIGURATION);
    }

    public GuiModificationEvent handleDelete() {
        return createEvent(new AbstractMap.SimpleEntry<String, String>("", ""), this.parameterTableView.getSelectionModel().getSelectedItem());
    }

    private GuiChangeAttributeEvent createEvent(Map.Entry<String, String> newValue, Map.Entry<String, String> oldValue) {
        GuiChangeAttributeEvent addKeyValueEvent = new GuiChangeAttributeEvent(GuiEventType.CHANGE_ELEMENT, Types.CONFIGURATION, configurationViewModel.getName());
        addKeyValueEvent.setElementId(configurationViewModel.getId());
        addKeyValueEvent.setAttributeType(Map.Entry.class.getSimpleName());
        addKeyValueEvent.setAttributeName("parameters");
        if (!newValue.getKey().equals("")) {
            addKeyValueEvent.setNewValue(newValue);
        }
        if (!oldValue.getKey().equals("")) {
            addKeyValueEvent.setOldValue(oldValue);
        }
        if (addKeyValueEvent.getNewValue() == null && addKeyValueEvent.getOldValue() == null) {
            return null;
        }
        return addKeyValueEvent;
    }

    private void fireEvent(GuiChangeAttributeEvent event) {
        guiModificationHandler.handle(event);
    }
}

