package de.unikassel.vs.alica.defaultPlugin;

import de.unikassel.vs.alica.generator.IConstraintCodeGenerator;
import de.unikassel.vs.alica.planDesigner.alicamodel.Plan;
import de.unikassel.vs.alica.planDesigner.alicamodel.Behaviour;
import de.unikassel.vs.alica.planDesigner.alicamodel.State;
/**
 * IF the following line is not import de.unikassel.vs.alica.defaultPlugin.DefaultTemplate;
 * you messed it up ... great ... you made the plandesigner great again ... huge ...
 * INSERT IT
 */

import java.util.Map;

/**
 * Glue Code for calling {@link DefaultTemplate}.
 */
public class DefaultConstraintCodeGenerator implements IConstraintCodeGenerator {
    private DefaultTemplate defaultTemplate;

    public DefaultConstraintCodeGenerator() {
        defaultTemplate = new DefaultTemplate();
    }

    public void setProtectedRegions(Map<String, String> protectedRegions) {
        defaultTemplate.setProtectedRegions(protectedRegions);
    }

    public String constraintPlanCheckingMethods(Plan plan) {
        //return "test1";
        return defaultTemplate.constraintPlanCheckingMethods(plan);
    }

    public String constraintBehaviourCheckingMethods(Behaviour behaviour) {
        //return "test2";
        return defaultTemplate.constraintBehaviourCheckingMethods(behaviour);
    }

    public String expressionsPlanCheckingMethods(Plan plan) {
       // return "test3";
        return defaultTemplate.expressionsPlanCheckingMethods(plan);
    }

    public String expressionsBehaviourCheckingMethods(Behaviour behaviour) {
       // return "test4";
        return defaultTemplate.expressionsBehaviourCheckingMethods(behaviour);
    }

    public String constraintStateCheckingMethods(State state) {
        //return "test5";
        return defaultTemplate.constraintStateCheckingMethods(state);
    }

    public String expressionsStateCheckingMethods(State state) {
        //return "test6";
        return defaultTemplate.expressionsStateCheckingMethods(state);
    }
}
