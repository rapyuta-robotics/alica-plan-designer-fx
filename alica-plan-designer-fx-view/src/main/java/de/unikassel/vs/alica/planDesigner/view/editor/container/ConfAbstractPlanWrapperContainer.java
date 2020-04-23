package de.unikassel.vs.alica.planDesigner.view.editor.container;

import de.unikassel.vs.alica.planDesigner.view.editor.tab.planTab.PlanTab;
import de.unikassel.vs.alica.planDesigner.view.model.ConfAbstractPlanWrapperViewModel;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class ConfAbstractPlanWrapperContainer extends Container {

    private StateContainer parentStateContainer;
    private ConfAbstractPlanWrapperViewModel confAbstractPlanWrapperViewModel;

    public ConfAbstractPlanWrapperContainer(StateContainer parentStateContainer, ConfAbstractPlanWrapperViewModel confAbstractPlanWrapperViewModel, PlanTab planTab) {
        super(confAbstractPlanWrapperViewModel, planTab);
        this.parentStateContainer = parentStateContainer;
        this.confAbstractPlanWrapperViewModel = confAbstractPlanWrapperViewModel;

        createNameListener();
        this.confAbstractPlanWrapperViewModel.configurationProperty().addListener((observableValue, oldConf, newConf) -> {
            Platform.runLater(this::redrawElement);
        });
        this.confAbstractPlanWrapperViewModel.abstractPlanProperty().addListener((observableValue, oldAbstractPlan, newAbstractPlan) -> {
            Platform.runLater(this::redrawElement);
        });
        setupContainer();
    }

    @Override
    public void setupContainer() {
        visualRepresentation = new HBox();
        HBox.setHgrow(visualRepresentation, Priority.ALWAYS);
        getChildren().add(visualRepresentation);

        // need to be set, when the label is actually layouted (before getWidth is 0)
        HBox visualRepHBox = (HBox) visualRepresentation;
        visualRepHBox.widthProperty().addListener((observable, oldValue, newValue) -> {
            // 19px per abstract plan because every line is 16px high and the additional 3px are for spacing between elements
            // 3px offset to not touch state circle with value-box
            relocate(-visualRepHBox.getWidth() / 2, StateContainer.STATE_RADIUS + parentStateContainer.getState().getConfAbstractPlanWrappers().indexOf(this.confAbstractPlanWrapperViewModel) * 19 + 3);
        });

        redrawElement();
    }

    @Override
    public void redrawElement() {
        clearUI();
        HBox visualRepHBox = (HBox) visualRepresentation;
        visualRepHBox.getChildren().add(new AbstractPlanContainer(this, confAbstractPlanWrapperViewModel.getAbstractPlan(), this.planTab));
        if (confAbstractPlanWrapperViewModel.getConfiguration() != null) {
            visualRepHBox.getChildren().add(new Label("("));
            visualRepHBox.getChildren().add(new ConfigurationContainer(this, confAbstractPlanWrapperViewModel.getConfiguration(), this.planTab));
            visualRepHBox.getChildren().add(new Label(")"));
        }

    }

    private void clearUI() {
        HBox visualRepHBox = (HBox) visualRepresentation;
        visualRepHBox.getChildren().clear();
    }

    @Override
    public Color getVisualisationColor() {
        return null;
    }



    public void setEffectToStandard() {
        setEffect(null);
    }
}