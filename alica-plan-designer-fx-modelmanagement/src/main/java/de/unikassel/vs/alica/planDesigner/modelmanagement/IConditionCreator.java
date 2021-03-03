package de.unikassel.vs.alica.planDesigner.modelmanagement;

import de.unikassel.vs.alica.planDesigner.alicamodel.Condition;

public interface IConditionCreator {
    Condition create(ModelModificationQuery mmq);
}
