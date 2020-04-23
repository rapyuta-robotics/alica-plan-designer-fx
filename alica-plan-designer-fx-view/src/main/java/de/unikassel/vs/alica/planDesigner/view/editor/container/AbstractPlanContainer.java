package de.unikassel.vs.alica.planDesigner.view.editor.container;

import de.unikassel.vs.alica.planDesigner.controller.MainWindowController;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.planTab.PlanTab;
import de.unikassel.vs.alica.planDesigner.view.model.AbstractPlanViewModel;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class AbstractPlanContainer extends Container {

    private ConfAbstractPlanWrapperContainer parentWrapperContainer;
    private AbstractPlanViewModel abstractPlanViewModel;
    private Label abstractPlanLabel;

    public AbstractPlanContainer(ConfAbstractPlanWrapperContainer parentWrapperContainer, AbstractPlanViewModel abstractPlanViewModel, PlanTab planTab) {
        super(abstractPlanViewModel, planTab);
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.parentWrapperContainer = parentWrapperContainer;
        this.abstractPlanViewModel = abstractPlanViewModel;
        setupContainer();
    }

    public ConfAbstractPlanWrapperContainer getParentWrapperContainer() {
        return parentWrapperContainer;
    }

    @Override
    public void setupContainer() {
        abstractPlanLabel = new Label();
        abstractPlanLabel.setText(abstractPlanViewModel.getName());
        abstractPlanViewModel.nameProperty().addListener((observable, oldValue, newName) -> {
            abstractPlanLabel.setText(newName);
        });
        abstractPlanLabel.setGraphic(getGraphic(abstractPlanViewModel.getType()));
        abstractPlanViewModel.typeProperty().addListener((observable, oldValue, newType) -> {
            abstractPlanLabel.setGraphic(getGraphic(newType));
        });
        visualRepresentation = abstractPlanLabel;

        this.setPrefWidth(abstractPlanLabel.getPrefWidth());
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
            MainWindowController.getInstance().getEditorTabPane().openTab(abstractPlanViewModel);
            event.consume();
        } else {
            // A single-click selects the Container as usual
            super.handleMouseClickedEvent(event);
        }
    }
}
