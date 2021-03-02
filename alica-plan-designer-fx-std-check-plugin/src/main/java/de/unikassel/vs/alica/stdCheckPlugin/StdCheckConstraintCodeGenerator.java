package de.unikassel.vs.alica.stdCheckPlugin;

import de.unikassel.vs.alica.generator.IConstraintCodeGenerator;
import de.unikassel.vs.alica.planDesigner.alicamodel.Plan;
import de.unikassel.vs.alica.planDesigner.alicamodel.Behaviour;
import de.unikassel.vs.alica.planDesigner.alicamodel.State;
import de.unikassel.vs.alica.defaultPlugin.StdCheckTemplate;
/**
 * IF the following line is not import de.unikassel.vs.alica.defaultPlugin.DefaultTemplate;
 * you messed it up ... great ... you made the plandesigner great again ... huge ...
 * INSERT IT
 */

import java.util.Map;

/**
     * Glue Code for calling {@link StdCheckTemplate}.
 */
public class StdCheckConstraintCodeGenerator implements IConstraintCodeGenerator {
    private StdCheckTemplate stdCheckTemplate;

    public StdCheckConstraintCodeGenerator() {
        stdCheckTemplate = new StdCheckTemplate();
    }

    public void setProtectedRegions(Map<String, String> protectedRegions) {
        stdCheckTemplate.setProtectedRegions(protectedRegions);
    }

    public String constraintPlanCheckingMethods(Plan plan) {
        //return "test1";
        return stdCheckTemplate.constraintPlanCheckingMethods(plan);
    }

    public String constraintBehaviourCheckingMethods(Behaviour behaviour) {
        //return "test2";
        return stdCheckTemplate.constraintBehaviourCheckingMethods(behaviour);
    }

    public String expressionsPlanCheckingMethods(Plan plan) {
       // return "test3";
        return stdCheckTemplate.expressionsPlanCheckingMethods(plan);
    }

    public String expressionsBehaviourCheckingMethods(Behaviour behaviour) {
       // return "test4";
        return stdCheckTemplate.expressionsBehaviourCheckingMethods(behaviour);
    }

    public String constraintStateCheckingMethods(State state) {
        //return "test5";
        return stdCheckTemplate.constraintStateCheckingMethods(state);
    }

    public String expressionsStateCheckingMethods(State state) {
        //return "test6";
        return stdCheckTemplate.expressionsStateCheckingMethods(state);
    }
}
