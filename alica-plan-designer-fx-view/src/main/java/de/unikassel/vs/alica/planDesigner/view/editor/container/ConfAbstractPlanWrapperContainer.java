package de.unikassel.vs.alica.planDesigner.view.editor.container;

import de.unikassel.vs.alica.planDesigner.controller.MainWindowController;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.planTab.PlanTab;
import de.unikassel.vs.alica.planDesigner.view.img.AlicaIcon;
import de.unikassel.vs.alica.planDesigner.view.model.ConfAbstractPlanWrapperViewModel;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ConfAbstractPlanWrapperContainer extends Container {

    private StateContainer parentStateContainer;
    private ConfAbstractPlanWrapperViewModel confAbstractPlanWrapperViewModel;

    public ConfAbstractPlanWrapperContainer(StateContainer parentStateContainer, ConfAbstractPlanWrapperViewModel confAbstractPlanWrapperViewModel, PlanTab planTab) {
        super(confAbstractPlanWrapperViewModel, planTab);
        this.confAbstractPlanWrapperViewModel = confAbstractPlanWrapperViewModel;
        this.parentStateContainer = parentStateContainer;
        this.createNameListener();
        this.setupContainer();
    }

    public StateContainer getParentStateContainer() {
        return parentStateContainer;
    }

    @Override
    protected void handleMouseClickedEvent(MouseEvent event) {
        if (event.getClickCount() == 2) {
            // A double-click opens the contained AbstractPlan in its own Tab
            // TODO: check whether the abstract plan or the configuration was clicked
            MainWindowController.getInstance().getEditorTabPane().openTab(((ConfAbstractPlanWrapperViewModel) planElementViewModel).getAbstractPlan());
            event.consume();
        } else {
            // A single-click selects the Container as usual
            super.handleMouseClickedEvent(event);
        }
    }

    @Override
    public void setupContainer() {
        HBox abstractPlanConfBox = new HBox();

        Label abstractPlanLabel = new Label();
        abstractPlanLabel.setText(confAbstractPlanWrapperViewModel.getAbstractPlan().getName());
        confAbstractPlanWrapperViewModel.getAbstractPlan().nameProperty().addListener((observable, oldValue, newName) -> {
            abstractPlanLabel.setText(newName);
        });
        abstractPlanLabel.setGraphic(getGraphic(confAbstractPlanWrapperViewModel.getAbstractPlan().getType()));
        confAbstractPlanWrapperViewModel.getAbstractPlan().typeProperty().addListener((observable, oldValue, newType) -> {
            abstractPlanLabel.setGraphic(getGraphic(newType));
        });
        // need to be set, when the label is actually layouted (before getWidth is 0)
        abstractPlanLabel.widthProperty().addListener((observable, oldValue, newValue) -> {
            // 19px per abstract plan because every line is 16px high and the additional 3px are for spacing between elements
            // 3px offset to not touch state circle with value-box
            relocate(-abstractPlanLabel.getWidth() / 2, StateContainer.STATE_RADIUS + parentStateContainer.getState().getConfAbstractPlanWrappers().indexOf(this.confAbstractPlanWrapperViewModel) * 19 + 3);
        });
        abstractPlanConfBox.getChildren().add(abstractPlanLabel);

        if (confAbstractPlanWrapperViewModel.getConfiguration() != null) {
            Label configurationLabel = new Label();
            configurationLabel.setText(confAbstractPlanWrapperViewModel.getConfiguration().getName());
            confAbstractPlanWrapperViewModel.getConfiguration().nameProperty().addListener((observableValue, oldName, newName) -> {
                configurationLabel.setText(newName);
            });
            configurationLabel.setGraphic(getGraphic(confAbstractPlanWrapperViewModel.getConfiguration().getType()));
            confAbstractPlanWrapperViewModel.getConfiguration().typeProperty().addListener((observableValue, oldType, newType) -> {
                configurationLabel.setGraphic(getGraphic(newType));
            });
            abstractPlanConfBox.getChildren().add(new Label("("));
            abstractPlanConfBox.getChildren().add(abstractPlanLabel);
            abstractPlanConfBox.getChildren().add(new Label(")"));
        }

        visualRepresentation = abstractPlanConfBox;
        getChildren().add(visualRepresentation);
    }

    @Override
    public Color getVisualisationColor() {
        return null;
    }

    public ImageView getGraphic(String iconName) {
        return new ImageView(new AlicaIcon(iconName, AlicaIcon.Size.SMALL));
    }

    @Override
    public void setEffectToStandard() {
        this.setEffect(null);
    }
}