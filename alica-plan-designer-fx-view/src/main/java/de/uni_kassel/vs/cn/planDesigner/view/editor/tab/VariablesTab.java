package de.uni_kassel.vs.cn.planDesigner.view.editor.tab;

import de.uni_kassel.vs.cn.planDesigner.view.model.VariableViewModel;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.LongStringConverter;

public class VariablesTab extends TableViewTab<VariableViewModel> {

    public VariablesTab() {
        super();
        setText(i18NRepo.getString("label.caption.variables"));

        TableColumn<VariableViewModel, String> nameColumn = makeColumn(i18NRepo.getString("label.column.name"), "nameProperty", new DefaultStringConverter());
        TableColumn<VariableViewModel, Long> idColumn = makeColumn(i18NRepo.getString("label.column.id"), "idProperty", new LongStringConverter());
        TableColumn<VariableViewModel, String> typeColumn = makeColumn(i18NRepo.getString("label.column.elementType"), "variableTypeProperty", new DefaultStringConverter());
        TableColumn<VariableViewModel, String> commentColumn = makeColumn(i18NRepo.getString("label.column.comment"), "commentProperty", new DefaultStringConverter());

        table.getColumns().addAll(nameColumn, idColumn, typeColumn, commentColumn);
        VBox variablesButtonVBox = new VBox();
        HBox addDeleteHBox = new HBox();
        Button addButton = new Button(i18NRepo.getString("action.list.add"));
        addButton.setOnAction(e -> {
            // TODO: Send event for creating new Variable for this Behaviour and adding it accordingly, when the ModelModificationEvent comes back
        });

        Button deleteButton = new Button(i18NRepo.getString("action.list.remove"));
        deleteButton.setOnAction(e -> {
            VariableViewModel selectedItem = table.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                // TODO: Send event for deleting the selected variable from the behaviour and update the table accordingly
            }
        });
        addDeleteHBox.getChildren().addAll(addButton, deleteButton);
        variablesButtonVBox.getChildren().addAll(table, addDeleteHBox);
        setContent(variablesButtonVBox);
    }

}
