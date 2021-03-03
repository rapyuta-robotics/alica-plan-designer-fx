package de.unikassel.vs.alica.planDesigner.view.editor.tab.configurationTab;

import de.unikassel.vs.alica.planDesigner.view.model.ConfigurationViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.TableView;
import javafx.scene.text.Font;

import java.util.AbstractMap;
import java.util.Map;

public class ConfigurationListener implements ChangeListener<ConfigurationViewModel>, MapChangeListener<String, String> {
    private final TableView<Map.Entry<String, String>> parametersTableView;

    ConfigurationListener(TableView<Map.Entry<String, String>> parametersTableView) {
        this.parametersTableView = parametersTableView;
    }

    @Override
    public void onChanged(Change<? extends String, ? extends String> change) {
        this.updateTable((ObservableMap<String, String>) change.getMap());
    }

    @Override
    public void changed(ObservableValue<? extends ConfigurationViewModel> observable, ConfigurationViewModel oldValue, ConfigurationViewModel newValue) {
        this.updateTable(newValue.getParameters());
    }

    public void updateTable(ObservableMap<String, String> newParameterMap) {
        parametersTableView.getItems().clear();
        parametersTableView.getItems().addAll(newParameterMap.entrySet());
        parametersTableView.getItems().add(new AbstractMap.SimpleEntry<String, String>("", ""));
        resizeTableView(newParameterMap);
    }

    private void resizeTableView(Map<String, String> newParameterMap) {
        double fontSize = Font.getDefault().getSize() * 2;

        // FontSize * (#items + 1 empty row + 1 heading) + 2 for borders
        double size = fontSize * (newParameterMap.size() + 2 ) + 2;
        parametersTableView.setPrefHeight(size);
        parametersTableView.setMinHeight(size);
        parametersTableView.setMaxHeight(size);
        parametersTableView.refresh();
    }
}
