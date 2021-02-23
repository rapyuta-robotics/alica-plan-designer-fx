package de.unikassel.vs.alica.defaultPlugin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class StdCheckPluginController implements Initializable {

    @FXML
    private Label stdCheckPluginFunctionLabel;
    @FXML
    private ComboBox<String> stdCheckPluginFunctionComboBox;
    @FXML
    private Label stdCheckPluginFunctionParameterLabel;
    @FXML
    private TextField stdCheckPluginFunctionParameter1;
    @FXML
    private TextField stdCheckPluginFunctionParameter2;
    @FXML
    private TextField stdCheckPluginFunctionParameter3;

    public void setStdCheckPluginFunctions() {

        stdCheckPluginFunctionComboBox.getItems().add("isAnyChildStatus");
        stdCheckPluginFunctionComboBox.getItems().add("areAllChildrenStatus");
        stdCheckPluginFunctionComboBox.getItems().add("isAnyChildTaskSuccessful");
        stdCheckPluginFunctionComboBox.getItems().add("amISuccessful");
        stdCheckPluginFunctionComboBox.getItems().add("amISuccessfulInAnyChild");
        stdCheckPluginFunctionComboBox.getItems().add("isStateTimedOut");
        stdCheckPluginFunctionComboBox.getItems().add("isTimeOut");

        //stdCheckPluginFunctionComboBox.selectionModelProperty().addListener();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setStdCheckPluginFunctions();
        initLabelTexts();
    }

    private void initLabelTexts() {
        stdCheckPluginFunctionLabel.setText("Function: ");
        stdCheckPluginFunctionParameterLabel.setText("Parameters: ");
    }
}
