package de.unikassel.vs.alica.stdCheckPlugin;

import de.unikassel.vs.alica.planDesigner.alicamodel.Configuration;
import de.unikassel.vs.alica.planDesigner.events.GuiChangeAttributeEvent;
import de.unikassel.vs.alica.planDesigner.events.GuiEventType;
import de.unikassel.vs.alica.planDesigner.events.GuiModificationEvent;
import de.unikassel.vs.alica.planDesigner.events.ModelQueryType;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelManager;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelModificationQuery;
import de.unikassel.vs.alica.planDesigner.modelmanagement.Types;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
        stdCheckPluginFunctionComboBox.getItems().add("NONE");

        //stdCheckPluginFunctionComboBox.selectionModelProperty().addListener();

        stdCheckPluginFunctionComboBox.setOnAction(event -> {
            String newValue = stdCheckPluginFunctionComboBox.getValue();
            updateModelOnChange(newValue);
            //updateGuiOnChange(newValue);
        });
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

    private void updateModelOnChange(String newValue) {
        GuiModificationEvent event;
        ModelModificationQuery mmq;
        if (newValue != null && !newValue.equals("NONE")) {
            event = new GuiChangeAttributeEvent(GuiEventType.CHANGE_ELEMENT, "PluginInformation", newValue);
        } else {
            throw new RuntimeException("StdCheckPluginController: No reason for calling updateModelOnChange() happened!");
        }

        StdCheckPluginInformation stdCheckPluginInformation = new StdCheckPluginInformation();

        mmq = new ModelModificationQuery(ModelQueryType.CHANGE_ELEMENT);
        mmq.setElementType(event.getElementType());
        mmq.setParentId(event.getParentId());
        mmq.setElementId(event.getElementId());
        mmq.setRelatedObjects(event.getRelatedObjects());
        if (event instanceof GuiChangeAttributeEvent) {
            GuiChangeAttributeEvent guiChangeAttributeEvent = (GuiChangeAttributeEvent) event;
            mmq.setAttributeType(guiChangeAttributeEvent.getAttributeType());
            mmq.setAttributeName(guiChangeAttributeEvent.getAttributeName());
            mmq.setNewValue(guiChangeAttributeEvent.getNewValue());
            mmq.setOldValue(guiChangeAttributeEvent.getOldValue());
        }
        System.out.println("value of AT: " + mmq.getAttributeType());
        System.out.println("value of ET: " + mmq.getElementType());
        System.out.println("value of EID: " + mmq.getElementId());
        System.out.println("value of PID: " + mmq.getParentId());
        System.out.println("value of OV: " + mmq.getOldValue());

        this.modelManager.storePlanElement("stdCheckPluginInformation", stdCheckPluginInformation, false);
        this.modelManager.handleModelModificationQuery(mmq);
    }
}
