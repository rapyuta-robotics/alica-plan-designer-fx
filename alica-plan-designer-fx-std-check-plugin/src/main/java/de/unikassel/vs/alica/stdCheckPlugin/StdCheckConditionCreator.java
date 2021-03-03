package de.unikassel.vs.alica.stdCheckPlugin;

import de.unikassel.vs.alica.planDesigner.alicamodel.Condition;
import de.unikassel.vs.alica.planDesigner.modelmanagement.IConditionCreator;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelModificationQuery;

public class StdCheckConditionCreator implements IConditionCreator {
    @Override
    public Condition create(ModelModificationQuery mmq) {
        System.out.println("StdCheckConditionCreator: " + mmq);

        // TODO create conditions correctly according to mmq -> all needed typo info in mmq?
        StdCheckPreCondition preCondition = new StdCheckPreCondition();

        return preCondition;
    }
}
