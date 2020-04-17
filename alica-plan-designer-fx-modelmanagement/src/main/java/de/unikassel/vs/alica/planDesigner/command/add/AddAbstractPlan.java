package de.unikassel.vs.alica.planDesigner.command.add;

import de.unikassel.vs.alica.planDesigner.alicamodel.AbstractPlan;
import de.unikassel.vs.alica.planDesigner.alicamodel.ConfAbstractPlanWrapper;
import de.unikassel.vs.alica.planDesigner.alicamodel.State;
import de.unikassel.vs.alica.planDesigner.command.Command;
import de.unikassel.vs.alica.planDesigner.events.ModelEventType;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelManager;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelModificationQuery;

public class AddAbstractPlan extends Command {
    protected State state;
    protected AbstractPlan abstractPlan;
    protected ConfAbstractPlanWrapper wrapper;

    public AddAbstractPlan(ModelManager modelManager, ModelModificationQuery mmq) {
        super(modelManager, mmq);
        this.state = (State) modelManager.getPlanElement(mmq.getParentId());
        this.abstractPlan = (AbstractPlan) modelManager.getPlanElement(mmq.getElementId());

        if(modelManager.checkForInclusionLoop(state, abstractPlan)){
            throw new RuntimeException(
                    String.format("AbstractPlan \"%s\" can not be added to State \"%s\" because of loop in model",
                    abstractPlan.getName(), state.getName())
            );
        }

        // wrap abstract plan with configuration remaining null -> no extra configuration
        this.wrapper = new ConfAbstractPlanWrapper();
        this.wrapper.setAbstractPlan(this.abstractPlan);
    }

    @Override
    public void doCommand() {
        this.state.addConfAbstractPlanWrapper(wrapper);
        this.fireEvent(ModelEventType.ELEMENT_ADDED, wrapper);
    }

    @Override
    public void undoCommand() {
        this.state.removeConfAbstractPlanWrapper(wrapper);
        this.fireEvent(ModelEventType.ELEMENT_REMOVED, wrapper);
    }
}
