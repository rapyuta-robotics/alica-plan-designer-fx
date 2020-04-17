package de.unikassel.vs.alica.planDesigner.view.model;

import de.unikassel.vs.alica.planDesigner.view.Types;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Arrays;

public class ConfAbstractPlanWrapperViewModel extends PlanElementViewModel {
    protected final SimpleObjectProperty<AbstractPlanViewModel> abstractPlan = new SimpleObjectProperty<>(null, "abstractPlan", null);
    protected final SimpleObjectProperty<ConfigurationViewModel> configuration = new SimpleObjectProperty<>(null, "configuration", null);

    public ConfAbstractPlanWrapperViewModel(long id, String name) {
        super(id, name, Types.CONF_ABSTRACTPLAN_WRAPPER);

        this.uiPropertyList.clear();
        // TODO: adapt to actual show abstract plan and reference to configuration
        this.uiPropertyList.addAll(Arrays.asList("name", "id", "comment"));
    }

    public final SimpleObjectProperty<AbstractPlanViewModel> abstractPlanProperty(){
        return abstractPlan;
    }
    public void setAbstractPlan(AbstractPlanViewModel abstractPlan){
        this.abstractPlan.set(abstractPlan);
    }
    public AbstractPlanViewModel getAbstractPlan(){
        return this.abstractPlan.get();
    }

    public final SimpleObjectProperty<ConfigurationViewModel> configurationProperty(){
        return configuration;
    }
    public void setConfiguration(ConfigurationViewModel configuration){
        this.configuration.set(configuration);
    }
    public ConfigurationViewModel getConfiguration(){
        return this.configuration.get();
    }
}
