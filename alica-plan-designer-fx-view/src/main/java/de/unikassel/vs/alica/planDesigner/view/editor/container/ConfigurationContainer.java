package de.unikassel.vs.alica.planDesigner.view.editor.container;

import de.unikassel.vs.alica.planDesigner.controller.MainWindowController;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.planTab.PlanTab;
import de.unikassel.vs.alica.planDesigner.view.model.ConfigurationViewModel;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ConfigurationContainer extends Container {

    private ConfAbstractPlanWrapperContainer parentWrapperContainer;
    private ConfigurationViewModel configurationViewModel;
    private Label configurationLabel;

    public ConfigurationContainer(ConfAbstractPlanWrapperContainer parentWrapperContainer, ConfigurationViewModel configurationViewModel, PlanTab planTab) {
        super(configurationViewModel, planTab);
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.parentWrapperContainer = parentWrapperContainer;
        this.configurationViewModel = configurationViewModel;
        setupContainer();
    }

    public ConfAbstractPlanWrapperContainer getParentWrapperContainer() {
        return parentWrapperContainer;
    }

    @Override
    public void setupContainer() {
        configurationLabel = new Label();
        configurationLabel.setText(configurationViewModel.getName());
        configurationViewModel.nameProperty().addListener((observableValue, oldName, newName) -> {
            configurationLabel.setText(newName);
        });
        configurationLabel.setGraphic(getGraphic(configurationViewModel.getType()));
        configurationViewModel.typeProperty().addListener((observableValue, oldType, newType) -> {
            configurationLabel.setGraphic(getGraphic(newType));
        });
        visualRepresentation = configurationLabel;
        this.setPrefWidth(configurationLabel.getPrefWidth());
        this.getChildren().add(visualRepresentation);
    }

    @Override
    public Color getVisualisationColor() {
        return null;
    }

    @Override
    public void setEffectToStandard() {
        setEffect(null);
    }

    @Override
    protected void handleMouseClickedEvent(MouseEvent event) {
        if (event.getClickCount() == 2) {
            // A double-click opens the contained AbstractPlan or Configuration in its own Tab
            MainWindowController.getInstance().getEditorTabPane().openTab(configurationViewModel);
            event.consume();
        } else {
            // A single-click selects the Container as usual
            super.handleMouseClickedEvent(event);
        }
    }
}
