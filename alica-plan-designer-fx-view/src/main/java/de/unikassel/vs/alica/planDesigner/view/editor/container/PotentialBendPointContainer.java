package de.unikassel.vs.alica.planDesigner.view.editor.container;

import de.unikassel.vs.alica.planDesigner.view.editor.tab.planTab.PlanTab;
import de.unikassel.vs.alica.planDesigner.view.model.BendPointViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.PlanElementViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.PlanViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.ViewModelElement;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class PotentialBendPointContainer extends BendpointContainer {

    private final TransitionContainer transitionContainer;

    public PotentialBendPointContainer(BendPointViewModel bendPointViewModel, PlanViewModel parent, PlanTab planTab,
                                       TransitionContainer transitionContainer) {
        super(bendPointViewModel, parent, planTab, transitionContainer);
        this.transitionContainer = transitionContainer;
    }

    @Override
    public Color getVisualisationColor() {
        return Color.GREY;
    }

    private class OnClickCreateHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            removeEventHandler(MouseEvent.MOUSE_PRESSED, OnClickCreateHandler.this);
            transitionContainer.redrawElement();
        }
    }
}
