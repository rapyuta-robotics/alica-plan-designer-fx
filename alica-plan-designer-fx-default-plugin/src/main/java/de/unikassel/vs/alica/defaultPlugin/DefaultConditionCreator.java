package de.unikassel.vs.alica.defaultPlugin;

import de.unikassel.vs.alica.planDesigner.alicamodel.Condition;
import de.unikassel.vs.alica.planDesigner.alicamodel.PostCondition;
import de.unikassel.vs.alica.planDesigner.alicamodel.PreCondition;
import de.unikassel.vs.alica.planDesigner.alicamodel.RuntimeCondition;
import de.unikassel.vs.alica.planDesigner.modelmanagement.IConditionCreator;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelModificationQuery;
import de.unikassel.vs.alica.planDesigner.modelmanagement.Types;

public class DefaultConditionCreator implements IConditionCreator {

    @Override
    public Condition create(ModelModificationQuery mmq) {
        Condition condition;
        switch (mmq.getElementType()) {
            case Types.PRECONDITION:
                condition = new PreCondition();
                break;
            case Types.RUNTIMECONDITION:
                condition = new RuntimeCondition();
                break;
            case Types.POSTCONDITION:
                condition = new PostCondition();
                break;
            default:
                throw new RuntimeException("DefaultConditionCreator: Condition type " + mmq.getElementType() + " is not supported!");
        }
        condition.setPluginName(mmq.getName());
        return condition;
    }
}
