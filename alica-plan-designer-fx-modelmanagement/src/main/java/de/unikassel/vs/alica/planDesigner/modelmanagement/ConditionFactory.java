package de.unikassel.vs.alica.planDesigner.modelmanagement;

import de.unikassel.vs.alica.planDesigner.alicamodel.Condition;

import java.util.Dictionary;

public class ConditionFactory {

    Dictionary<String, IConditionCreator> creatorDictionary;

    public Condition create(ModelModificationQuery mmq) {
        return creatorDictionary.get(mmq.getName()).create(mmq);
    };

    void registerConditionCreator(String conditionPluginName, IConditionCreator creator)
    {
        creatorDictionary.put(conditionPluginName, creator);
    }
}
