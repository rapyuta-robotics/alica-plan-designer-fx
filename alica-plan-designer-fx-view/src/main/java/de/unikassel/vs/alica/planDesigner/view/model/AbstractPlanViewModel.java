package de.unikassel.vs.alica.planDesigner.view.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;

public class AbstractPlanViewModel extends SerializableViewModel {

    protected final ObservableList<VariableViewModel> variables = FXCollections.observableArrayList();

    public AbstractPlanViewModel(long id, String name, String type) {
        super(id, name, type);

        this.uiPropertyList.clear();
        this.uiPropertyList.addAll(Arrays.asList("name", "id", "comment", "relativeDirectory"));
    }

    public ObservableList<VariableViewModel> getVariables() {
        return variables;
    }
}
