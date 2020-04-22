package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.ViewModelManagement.ViewModelManager;
import de.unikassel.vs.alica.planDesigner.alicamodel.*;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelManager;
import de.unikassel.vs.alica.planDesigner.uiextensionmodel.BendPoint;
import de.unikassel.vs.alica.planDesigner.view.model.ViewModelElement;

public class ViewModelFactory {
    private final BehaviourViewModelFactory behaviourViewModelFactory = new BehaviourViewModelFactory();
    private final ConfAbstractPlanWrapperViewModelFactory confAbstractPlanWrapperViewModelFactory = new ConfAbstractPlanWrapperViewModelFactory();
    private final TaskViewModelFactory taskViewModelFactory = new TaskViewModelFactory();
    private final ConfigurationViewModelFactory configurationViewModelFactory = new ConfigurationViewModelFactory();
    private final TaskRepositoryViewModelFactory taskRepositoryViewModelFactory = new TaskRepositoryViewModelFactory();
    private final RoleSetViewModelFactory roleSetViewModelFactory = new RoleSetViewModelFactory();
    private final RoleViewModelFactory roleViewModelFactory = new RoleViewModelFactory();
    private final CharacteristicViewModelFactory characteristicViewModelFactory = new CharacteristicViewModelFactory();
    private final PlanViewModelFactory planViewModelFactory = new PlanViewModelFactory();
    private final PlanTypeViewModelFactory planTypeViewModelFactory = new PlanTypeViewModelFactory();
    private final StateViewModelFactory stateViewModelFactory = new StateViewModelFactory();
    private final AnnotatedPlanViewModelFactory annotatedPlanViewModelFactory = new AnnotatedPlanViewModelFactory();
    private final EntryPointViewModelFactory entryPointViewModelFactory = new EntryPointViewModelFactory();
    private final VariableViewModelFactory variableViewModelFactory = new VariableViewModelFactory();
    private final VariableBindingViewModelFactory variableBindingViewModelFactory = new VariableBindingViewModelFactory();
    private final TransitionViewModelFactory transitionViewModelFactory = new TransitionViewModelFactory();
    private final SynchronizationViewModelFactory synchronizationViewModelFactory = new SynchronizationViewModelFactory();
    private final QuantifierViewModelFactory quantifierViewModelFactory = new QuantifierViewModelFactory();
    private final ConditionViewModelFactory conditionViewModelFactory = new ConditionViewModelFactory();
    private final BendPointViewModelFactory bendPointViewModelFactory = new BendPointViewModelFactory();

    public ViewModelFactory(ModelManager modelManager, ViewModelManager viewModelManager) {
        InternalViewModelFactory.setModelManager(modelManager);
        InternalViewModelFactory.setViewModelManager(viewModelManager);
    }

    public ViewModelElement create (PlanElement planElement) {
        if (planElement instanceof Behaviour) {
            return behaviourViewModelFactory.create((Behaviour) planElement);
        } else if (planElement instanceof Task) {
            return taskViewModelFactory.create((Task) planElement);
        } else if (planElement instanceof TaskRepository) {
            return taskRepositoryViewModelFactory.create((TaskRepository) planElement);
        } else if (planElement instanceof RoleSet) {
            return roleSetViewModelFactory.create((RoleSet) planElement);
        } else if (planElement instanceof Role) {
            return roleViewModelFactory.create((Role) planElement);
        } else if (planElement instanceof Characteristic) {
            return characteristicViewModelFactory.create((Characteristic) planElement);
        } else if (planElement instanceof Plan) {
            return planViewModelFactory.create((Plan) planElement);
        } else if (planElement instanceof PlanType) {
            return planTypeViewModelFactory.create((PlanType) planElement);
        } else if (planElement instanceof State) {
            return stateViewModelFactory.create((State) planElement);
        } else if (planElement instanceof AnnotatedPlan) {
            return annotatedPlanViewModelFactory.create((AnnotatedPlan) planElement);
        } else if (planElement instanceof EntryPoint) {
            return entryPointViewModelFactory.create((EntryPoint) planElement);
        } else if (planElement instanceof Variable) {
            return variableViewModelFactory.create((Variable) planElement);
        } else if (planElement instanceof VariableBinding) {
            return variableBindingViewModelFactory.create((VariableBinding) planElement);
        } else if (planElement instanceof Transition) {
            return transitionViewModelFactory.create((Transition) planElement);
        } else if (planElement instanceof Synchronisation) {
            return synchronizationViewModelFactory.create((Synchronisation) planElement);
        } else if (planElement instanceof Quantifier) {
            return quantifierViewModelFactory.create((Quantifier) planElement);
        } else if (planElement instanceof Condition) {
            return conditionViewModelFactory.create((Condition) planElement);
        } else if (planElement instanceof BendPoint) {
            return bendPointViewModelFactory.create((BendPoint) planElement);
        }  else if (planElement instanceof Configuration) {
            return configurationViewModelFactory.create((Configuration) planElement);
        }  else if (planElement instanceof ConfAbstractPlanWrapper) {
            return confAbstractPlanWrapperViewModelFactory.create((ConfAbstractPlanWrapper) planElement);
        } else {
            throw new RuntimeException("ViewModelFactory: getViewModelElement for type " + planElement.getClass().toString() + " not implemented!");
        }
    }
}
