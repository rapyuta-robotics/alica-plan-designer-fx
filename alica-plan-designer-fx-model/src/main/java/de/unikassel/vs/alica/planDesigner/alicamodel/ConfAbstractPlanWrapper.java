package de.unikassel.vs.alica.planDesigner.alicamodel;

import javafx.beans.property.SimpleObjectProperty;

public class ConfAbstractPlanWrapper extends PlanElement {
    private final SimpleObjectProperty<AbstractPlan> abstractPlan = new SimpleObjectProperty<>(this, "abstractPlan", null);
    private final SimpleObjectProperty<Configuration> configuration = new SimpleObjectProperty<>(this, "configuration", null);

    public AbstractPlan getAbstractPlan() {
        return abstractPlan.get();
    }
    public void setAbstractPlan(AbstractPlan abstractPlan) {
        this.abstractPlan.set(abstractPlan);
    }
    public SimpleObjectProperty<AbstractPlan> abstractPlanProperty(){
        return abstractPlan;
    }

    public Configuration getConfiguration() {
        return configuration.get();
    }
    public void setConfiguration(Configuration configuration) {
        this.configuration.set(configuration);
    }
    public SimpleObjectProperty<Configuration> configurationProperty(){
        return configuration;
    }
}
