package de.unikassel.vs.alica.planDesigner.command.delete;

import de.unikassel.vs.alica.planDesigner.alicamodel.State;
import de.unikassel.vs.alica.planDesigner.alicamodel.Transition;
import de.unikassel.vs.alica.planDesigner.command.UiPositionCommand;
import de.unikassel.vs.alica.planDesigner.events.ModelEventType;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelManager;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelModificationQuery;
import de.unikassel.vs.alica.planDesigner.modelmanagement.Types;
import de.unikassel.vs.alica.planDesigner.uiextensionmodel.UiElement;
import de.unikassel.vs.alica.planDesigner.uiextensionmodel.UiExtension;

public class DeleteTransitionInPlan extends UiPositionCommand {

    private final UiExtension parentOfElement;
    private UiElement uiElement;
    private State inState;
    private State outState;
    private Transition transition;

    public DeleteTransitionInPlan(ModelManager manager, ModelModificationQuery mmq) {
        super(manager, mmq);
        this.parentOfElement = this.modelManager.getPlanUIExtensionPair(mmq.getParentId());
        this.transition = (Transition) this.modelManager.getPlanElement(mmq.getElementId());
    }

    private void saveForLaterRetrieval() {
        uiElement = parentOfElement.getUiElement(transition.getId());
        outState = transition.getOutState();
        inState = transition.getInState();
    }

    @Override
    public void doCommand() {
        saveForLaterRetrieval();
        parentOfElement.remove(transition.getId());
        parentOfElement.getPlan().removeTransition(transition);
        for (State state: parentOfElement.getPlan().getStates()) {
            if(state.getId() == inState.getId()) {
                state.removeOutTransition(transition);
            }
            if(state.getId() == outState.getId()){
                state.removeInTransition(transition);
            }
        }
        transition.setPreCondition(null);
        transition.setInState(null);
        transition.setOutState(null);
        this.modelManager.dropPlanElement(Types.TRANSITION, transition, true);
        this.fireEvent(ModelEventType.ELEMENT_DELETED, transition);
    }

    @Override
    public void undoCommand() {
        parentOfElement.getPlan().getTransitions().add(transition);
        this.uiElement = parentOfElement.getUiElement(this.transition.getId());
        this.uiElement.setX(this.x);
        this.uiElement.setY(this.y);
        transition.setInState(inState);
        transition.setOutState(outState);
    }
}
