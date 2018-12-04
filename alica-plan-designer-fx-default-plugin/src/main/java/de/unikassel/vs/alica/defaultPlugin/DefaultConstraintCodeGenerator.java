package de.unikassel.vs.alica.defaultPlugin;

import de.unikassel.vs.alica.generator.IConstraintCodeGenerator;
import de.unikassel.vs.alica.planDesigner.alicamodel.Plan;
import de.unikassel.vs.alica.planDesigner.alicamodel.State;

import java.util.Map;

/**
 * Glue Code for calling {@link de.unikassel.vs.alica.defaultPlugin.DefaultTemplate}.
 */
public class DefaultConstraintCodeGenerator implements IConstraintCodeGenerator {
    private de.unikassel.vs.alica.defaultPlugin.DefaultTemplate defaultTemplate;

    public DefaultConstraintCodeGenerator() {
        defaultTemplate = new de.unikassel.vs.alica.defaultPlugin.DefaultTemplate();
    }

    public void setProtectedRegions(Map<String, String> protectedRegions) {
        defaultTemplate.setProtectedRegions(protectedRegions);
    }

    public String constraintPlanCheckingMethods(Plan plan) {
        return defaultTemplate.constraintPlanCheckingMethods(plan);
    }

    public String expressionsPlanCheckingMethods(Plan plan) {
        return defaultTemplate.expressionsPlanCheckingMethods(plan);
    }

    public String constraintStateCheckingMethods(State state) {
        return defaultTemplate.constraintStateCheckingMethods(state);
    }

    public String expressionsStateCheckingMethods(State state) {
        return defaultTemplate.expressionsStateCheckingMethods(state);
    }
}