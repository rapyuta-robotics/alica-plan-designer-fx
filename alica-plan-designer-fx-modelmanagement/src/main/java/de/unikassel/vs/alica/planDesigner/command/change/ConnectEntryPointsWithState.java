package de.unikassel.vs.alica.planDesigner.command.change;

import de.unikassel.vs.alica.planDesigner.alicamodel.EntryPoint;
import de.unikassel.vs.alica.planDesigner.alicamodel.Plan;
import de.unikassel.vs.alica.planDesigner.alicamodel.State;
import de.unikassel.vs.alica.planDesigner.command.Command;
import de.unikassel.vs.alica.planDesigner.events.ModelEventType;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelManager;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelModificationQuery;
import de.unikassel.vs.alica.planDesigner.modelmanagement.Types;

public class ConnectEntryPointsWithState extends Command {

    protected EntryPoint entryPoint;
    protected State newState;
    protected State oldState;
    protected Plan plan;

    public ConnectEntryPointsWithState(ModelManager manager, ModelModificationQuery mmq) {
        super(manager, mmq);
        entryPoint = (EntryPoint) manager.getPlanElement((mmq.getRelatedObjects().get(Types.ENTRYPOINT)));
        oldState = entryPoint.getState();
        newState = (State) manager.getPlanElement(mmq.getRelatedObjects().get(Types.STATE));
        plan = (Plan) manager.getPlanElement(mmq.getParentId());
    }


    @Override
    public void doCommand() {
        if (newState.getEntryPoint() != null) {
            return;
        }

        entryPoint.setState(newState);
        newState.setEntryPoint(entryPoint);

        if (oldState != null) {
            oldState.setEntryPoint(null);
        }

        fireEvent(ModelEventType.ELEMENT_CONNECTED, entryPoint);
    }

    @Override
    public void undoCommand() {
        entryPoint.setState(oldState);
    }
}
