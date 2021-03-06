package de.unikassel.vs.alica.planDesigner.command.copy;

import de.unikassel.vs.alica.planDesigner.alicamodel.*;
import de.unikassel.vs.alica.planDesigner.command.Command;
import de.unikassel.vs.alica.planDesigner.events.ModelEventType;
import de.unikassel.vs.alica.planDesigner.modelmanagement.Extensions;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelManager;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelModificationQuery;
import de.unikassel.vs.alica.planDesigner.modelmanagement.Types;

public class CopyBehaviour extends Command {

    private Behaviour behaviour;
    private Behaviour copyBehaviour = new Behaviour();

    public CopyBehaviour(ModelManager modelManager, ModelModificationQuery mmq) {
        super(modelManager, mmq);
        this.behaviour = (Behaviour) modelManager.getPlanElement(mmq.getElementId());
    }

    @Override
    public void doCommand() {
        copyBehaviour.setName(behaviour.getName() + "Copy");
        copyBehaviour.setComment(behaviour.getComment());
        copyBehaviour.setDeferring(behaviour.getDeferring());
        copyBehaviour.setFrequency(behaviour.getFrequency());
        copyBehaviour.setRelativeDirectory(modelManager.makeRelativeDirectory(behaviour.getRelativeDirectory(), copyBehaviour.getName()+ "." + Extensions.BEHAVIOUR));
        //Set Variable
        for (Variable variable: behaviour.getVariables()) {
            Variable newVariable = new Variable();
            newVariable.setVariableType(variable.getVariableType());
            newVariable.setName(variable.getName());
            newVariable.setComment(variable.getComment());
            copyBehaviour.addVariable(newVariable);
        }
        //Set PreCondition
        if(behaviour.getPreCondition() != null) {
            PreCondition newPreCondition = new PreCondition();
            newPreCondition.copyPreCondition(behaviour.getPreCondition(), behaviour, copyBehaviour);
            copyBehaviour.setPreCondition(newPreCondition);
        }
        //Set RuntimeCondition
        if(behaviour.getRuntimeCondition() != null) {
            RuntimeCondition newRuntimeCondition = new RuntimeCondition();
            newRuntimeCondition.copyRuntimeCondition(behaviour.getRuntimeCondition(), behaviour, copyBehaviour);
            copyBehaviour.setRuntimeCondition(newRuntimeCondition);
        }
        //Set PostCondition
        if(behaviour.getPostCondition() != null) {
            PostCondition newPostCondition = new PostCondition();
            newPostCondition.copyPostCondition(behaviour.getPostCondition(), behaviour, copyBehaviour);
            copyBehaviour.setPostCondition(newPostCondition);
        }

        this.modelManager.storePlanElement(Types.BEHAVIOUR, copyBehaviour,true);
        this.fireEvent(ModelEventType.ELEMENT_CREATED, copyBehaviour);
        this.modelManager.generateAutoGeneratedFilesForAbstractPlan(copyBehaviour);
    }

    @Override
    public void undoCommand() {
        modelManager.dropPlanElement(Types.BEHAVIOUR, copyBehaviour, true);
        this.fireEvent(ModelEventType.ELEMENT_DELETED, copyBehaviour);
    }
}
