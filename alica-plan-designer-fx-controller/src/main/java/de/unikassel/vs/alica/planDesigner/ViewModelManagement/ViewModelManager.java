package de.unikassel.vs.alica.planDesigner.ViewModelManagement;

import de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories.ViewModelFactory;
import de.unikassel.vs.alica.planDesigner.alicamodel.*;
import de.unikassel.vs.alica.planDesigner.controller.MainWindowController;
import de.unikassel.vs.alica.planDesigner.events.ModelEvent;
import de.unikassel.vs.alica.planDesigner.events.ModelEventType;
import de.unikassel.vs.alica.planDesigner.handlerinterfaces.IGuiModificationHandler;
import de.unikassel.vs.alica.planDesigner.handlerinterfaces.IPluginEventHandler;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelManager;
import de.unikassel.vs.alica.planDesigner.uiextensionmodel.BendPoint;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.model.*;
import de.unikassel.vs.alica.planDesigner.view.repo.RepositoryViewModel;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewModelManager {

    protected ModelManager modelManager;
    protected IGuiModificationHandler guiModificationHandler;
    protected Map<Long, ViewModelElement> viewModelElements;
    private final ViewModelFactory viewModelFactory;

    public ViewModelManager(ModelManager modelManager, IGuiModificationHandler handler) {
        this.modelManager = modelManager;
        this.guiModificationHandler = handler;
        this.viewModelElements = new HashMap<>();
        viewModelFactory = new ViewModelFactory(modelManager, this);
    }

    public RepositoryViewModel createRepositoryViewModel() {
        return new RepositoryViewModel();
    }

    /**
     * Just returns an existing view model object, if it already exists.
     * Otherwise, it will create one according to the given planElement object.
     *
     * @param planElement The model object that corresponds to the wanted view model object.
     * @return the view model object
     */
    public ViewModelElement getViewModelElement(PlanElement planElement) {
        if (planElement == null) {
            throw new NullPointerException("ViewModelElement: getViewModelElement(planElement == null)!");
        }

        ViewModelElement element = this.viewModelElements.get(planElement.getId());
        if (element == null) {
            element = viewModelFactory.create(planElement);
            viewModelElements.put(planElement.getId(), element);
            element.registerListener(guiModificationHandler);
        }
        return element;
    }

    public void putViewModelForAvoidingLoops(ViewModelElement viewModelElement) {
        viewModelElements.put(viewModelElement.getId(), viewModelElement);
    }

    /**
     * Handles the model event for the the view model elements.
     *
     * @param event
     */
    public ViewModelElement updateViewModel(ModelEvent event) {
        ViewModelElement viewModelElement = getViewModelElement(event.getElement());

        switch (event.getEventType()) {
            case ELEMENT_DELETED:
            case ELEMENT_PARSED:
            case ELEMENT_CREATED:
                if (viewModelElement instanceof PlanViewModel) {
                    updatePlansInPlanTypeViewModels((PlanViewModel) viewModelElement, event.getEventType());
                }
                break;
            case ELEMENT_REMOVED:
            case ELEMENT_REMOVED_AND_DELETED:
                removeElement(event.getParentId(), viewModelElement, event.getRelatedObjects());
                break;
            case ELEMENT_CREATED_AND_ADDED:
            case ELEMENT_ADDED:
                addElement(event);
                break;
            case ELEMENT_CONNECTED:
                connectElement(event);
                break;
            case ELEMENT_DISCONNECTED:
                disconnectElement(event);
                break;
            case ELEMENT_CHANGED_POSITION:
                changePosition((PlanElementViewModel) viewModelElement, event);
                break;
            case ELEMENT_ATTRIBUTE_CHANGED:
                changeElementAttribute(viewModelElement, event.getChangedAttribute(), event.getNewValue(), event.getOldValue());
                break;
            default:
                System.out.println("Controller.updateViewModel(): Event type " + event.getEventType() + " is not handled.");
                break;
        }

        if (event.getUiElement() != null && event.getUiElement().getBendPoints().size() != 0) {
            TransitionViewModel transition = (TransitionViewModel) viewModelElement;
            transition.getBendpoints().clear();
            for (BendPoint bendPoint : event.getUiElement().getBendPoints()) {
                BendPointViewModel bendPointViewModel = (BendPointViewModel) getViewModelElement(bendPoint);
                transition.addBendpoint(bendPointViewModel);
            }
            ModelEvent modelEvent = new ModelEvent(ModelEventType.ELEMENT_CREATED, event.getElement(), Types.BENDPOINT);
            updateViewModel(modelEvent);
        }
        return viewModelElement;
    }

    public void removeElement(long parentId, ViewModelElement viewModelElement, Map<String, Long> relatedObjects) {
        switch (viewModelElement.getType()) {
            case Types.TASKREPOSITORY:
                break;
            case Types.TASK:
                ((TaskViewModel) viewModelElement).getTaskRepositoryViewModel().removeTask(viewModelElement.getId());
                break;
            case Types.STATE:
            case Types.SUCCESSSTATE:
            case Types.FAILURESTATE:
                StateViewModel stateViewModel = (StateViewModel) viewModelElement;
                PlanViewModel planViewModel = (PlanViewModel) getViewModelElement(modelManager.getPlanElement(parentId));
                if(stateViewModel.getEntryPoint() != null) {
                    EntryPointViewModel entryPointVM = null;
                    for (EntryPointViewModel entryPointViewModel: planViewModel.getEntryPoints()) {
                        if(stateViewModel.getEntryPoint().getId() == entryPointViewModel.getId()) {
                            entryPointViewModel.setState(null);
                            entryPointVM = entryPointViewModel;
                            stateViewModel.setEntryPoint(null);
                        }
                    }
                    //to update gui
                    planViewModel.getEntryPoints().remove(entryPointVM);
                    planViewModel.getEntryPoints().add(entryPointVM);
                }
                planViewModel.getStates().remove(stateViewModel);
                break;
            case Types.ENTRYPOINT:
                EntryPointViewModel entryPointViewModel = (EntryPointViewModel) viewModelElement;
                planViewModel = (PlanViewModel) getViewModelElement(modelManager.getPlanElement(parentId));

                planViewModel.getEntryPoints().remove(entryPointViewModel);
                if (entryPointViewModel.getState() != null) {
                    StateViewModel entryState = entryPointViewModel.getState();
                    entryState.setEntryPoint(null);
                }
                break;
            case Types.TRANSITION:
                TransitionViewModel transitionViewModel = (TransitionViewModel) viewModelElement;
                if(relatedObjects == null){
                    //Delete Transition
                    if(transitionViewModel.getBendpoints().size() == 0) {
                        planViewModel = (PlanViewModel) getViewModelElement(modelManager.getPlanElement(parentId));
                        planViewModel.getTransitions().remove(transitionViewModel);
                    } else {
                        transitionViewModel.getBendpoints().clear();
                        planViewModel = (PlanViewModel) getViewModelElement(modelManager.getPlanElement(parentId));
                        planViewModel.getTransitions().remove(transitionViewModel);
                    }
                } else {
                    //Delete only BendPoints in Transition
                    for( BendPointViewModel bPoint : transitionViewModel.getBendpoints()){
                        if(bPoint.getId() == relatedObjects.get(Types.BENDPOINT)){
                            transitionViewModel.getBendpoints().remove(bPoint);
                            break;
                        }
                    }
                }
                break;
            case Types.SYNCHRONISATION:
                SynchronisationViewModel synchronisationViewModel = (SynchronisationViewModel) viewModelElement;
                planViewModel = (PlanViewModel) getViewModelElement(modelManager.getPlanElement(parentId));
                planViewModel.getSynchronisations().remove(synchronisationViewModel);
                break;
            case Types.ANNOTATEDPLAN:
                AnnotatedPlanViewModel annotatedPlanViewModel = (AnnotatedPlanViewModel) viewModelElement;
                PlanTypeViewModel planTypeViewModel = (PlanTypeViewModel) getViewModelElement(modelManager.getPlanElement(parentId));
                planTypeViewModel.getPlansInPlanType().remove(annotatedPlanViewModel);

                List<VariableBindingViewModel> variableBindingViewModelList = new ArrayList<>(planTypeViewModel.getVariableBindings());
                for (VariableBindingViewModel variableBindingViewModel: variableBindingViewModelList) {
                    if (variableBindingViewModel.getSubPlan().getId() == annotatedPlanViewModel.getPlanId()) {
                        planTypeViewModel.removeVariableBinding(variableBindingViewModel);
                    }
                }
                break;
            case Types.PLAN:
            case Types.MASTERPLAN:
                updatePlansInPlanTypeViewModels((PlanViewModel) viewModelElement, ModelEventType.ELEMENT_REMOVED);
                break;
            case Types.CONF_ABSTRACTPLAN_WRAPPER:
                ConfAbstractPlanWrapperViewModel confAbstractPlanWrapperViewModel = (ConfAbstractPlanWrapperViewModel) viewModelElement;
                stateViewModel = (StateViewModel) getViewModelElement(modelManager.getPlanElement(parentId));
                stateViewModel.removeConfAbstractPlanWrapper(confAbstractPlanWrapperViewModel);

                // HACK: you have duplicates if don't remove and add
                planViewModel = (PlanViewModel) getViewModelElement(modelManager.getPlanElement(stateViewModel.getParentId()));
                planViewModel.getStates().remove(stateViewModel);
                planViewModel.getStates().add(stateViewModel);
                break;
            case Types.VARIABLE:
                ViewModelElement parentViewModel = getViewModelElement(modelManager.getPlanElement(parentId));
                ((AbstractPlanViewModel) parentViewModel).getVariables().remove(viewModelElement);
                break;
            case Types.VARIABLEBINDING:
                parentViewModel = getViewModelElement(modelManager.getPlanElement(parentId));
                if(parentViewModel instanceof PlanTypeViewModel){
                    ((HasVariableBinding) parentViewModel).getVariableBindings().remove(viewModelElement);
                }
                if(parentViewModel instanceof PlanViewModel) {
                    VariableBindingViewModel var = null;
                    for (StateViewModel stateViewModel1:((PlanViewModel) parentViewModel).getStates()) {
                        for (Object object: stateViewModel1.getVariableBindings()) {
                            if(((VariableBindingViewModel) object).getId() == viewModelElement.getId()){
                                var = (VariableBindingViewModel) object;
                            }
                        }
                        if(var != null) {
                            stateViewModel1.removeVariableBinding(var);
                        }
                    }
                }
                if(parentViewModel instanceof StateViewModel) {
                    ((StateViewModel) parentViewModel).removeVariableBinding((VariableBindingViewModel) viewModelElement);
                }
                break;
            case Types.PRECONDITION:
                parentViewModel = getViewModelElement(modelManager.getPlanElement(parentId));
                switch (parentViewModel.getType()){
                    case Types.PLAN:
                    case Types.MASTERPLAN:
                        ((PlanViewModel) parentViewModel).setPreCondition(null);
                        break;
                    case Types.BEHAVIOUR:
                        ((BehaviourViewModel) parentViewModel).setPreCondition(null);
                        break;
                    case Types.TRANSITION:
                        ((TransitionViewModel) parentViewModel).setPreCondition(null);
                    default:
                }
                break;
            case Types.RUNTIMECONDITION:
                parentViewModel = getViewModelElement(modelManager.getPlanElement(parentId));
                switch (parentViewModel.getType()) {
                    case Types.PLAN:
                    case Types.MASTERPLAN:
                        ((PlanViewModel)parentViewModel).setRuntimeCondition(null);
                        break;
                    case Types.BEHAVIOUR:
                        ((BehaviourViewModel)parentViewModel).setRuntimeCondition(null);
                        break;
                    default:
                }
                break;
            case Types.POSTCONDITION:
                parentViewModel = getViewModelElement(modelManager.getPlanElement(parentId));
                switch (parentViewModel.getType()) {
                    case Types.SUCCESSSTATE:
                    case Types.FAILURESTATE:
                        ((StateViewModel)parentViewModel).setPostCondition(null);
                        break;
                    case Types.BEHAVIOUR:
                        ((BehaviourViewModel)parentViewModel).setPostCondition(null);
                        break;
                    default:
                }

                break;
            case Types.QUANTIFIER:
                parentViewModel = getViewModelElement(modelManager.getPlanElement(parentId));
                switch (parentViewModel.getType()){
                    case Types.PRECONDITION:
                    case Types.RUNTIMECONDITION:
                    case Types.POSTCONDITION:
                        ((ConditionViewModel) parentViewModel).getQuantifiers().remove(viewModelElement);
                        break;
                    default:
                        throw new RuntimeException(getClass().getSimpleName() + ": ParentViewModel has no Quantifiers");
                }
                break;
            default:
                System.err.println("ViewModelManager: Removing elements of type: " + viewModelElement.getType() + " not supported!");
        }

        if(viewModelElement.getType() != Types.TRANSITION && (relatedObjects == null || relatedObjects.isEmpty())) {
            viewModelElements.remove(viewModelElement.getId());
        }
    }

    /**
     * Add the element to its corresponding parent element.
     * @param event
     */
    public void addElement(ModelEvent event) {
        ViewModelElement viewModelElement = getViewModelElement(event.getElement());
        ViewModelElement parentViewModel = null;
        PlanElement parentPlanElement = modelManager.getPlanElement(event.getParentId());
        if(parentPlanElement != null) {
            parentViewModel = getViewModelElement(parentPlanElement);
        }

        if (parentViewModel instanceof PlanViewModel) {
            addToPlan((PlanViewModel) parentViewModel, viewModelElement, event);
            return;
        }

        switch (event.getElementType()) {
            case Types.ANNOTATEDPLAN:
                ((PlanTypeViewModel) parentViewModel).getPlansInPlanType().add((AnnotatedPlanViewModel) viewModelElement);
                break;
            case Types.TASK:
                if (parentViewModel instanceof EntryPointViewModel) {
                    ((EntryPointViewModel) parentViewModel).setTask((TaskViewModel) viewModelElement);
                } else if (parentViewModel instanceof TaskRepositoryViewModel) {
                    ((TaskRepositoryViewModel) parentViewModel).addTask((TaskViewModel) viewModelElement);
                }
                break;
            case Types.CONF_ABSTRACTPLAN_WRAPPER:
                ConfAbstractPlanWrapperViewModel confAbstractPlanWrapperViewModel = (ConfAbstractPlanWrapperViewModel) viewModelElement;
                ((StateViewModel) parentViewModel).addConfAbstractPlanWrapper(confAbstractPlanWrapperViewModel);
                break;
            case Types.VARIABLE:
                if (parentViewModel instanceof AbstractPlanViewModel) {
                    ((AbstractPlanViewModel) parentViewModel).getVariables().add((VariableViewModel) viewModelElement);
                } else if (parentViewModel instanceof ConditionViewModel) {
                    ((ConditionViewModel) parentViewModel).getVariables().add((VariableViewModel) viewModelElement);
                }
                break;
            case Types.VARIABLEBINDING:
                ((HasVariableBinding) parentViewModel).getVariableBindings().add((VariableBindingViewModel) viewModelElement);
                break;
            case Types.PRECONDITION:
                switch (parentViewModel.getType()){
                    case Types.BEHAVIOUR:
                        ((BehaviourViewModel)parentViewModel).setPreCondition((ConditionViewModel) viewModelElement);
                        break;
                    case Types.TRANSITION:
                        ((TransitionViewModel)parentViewModel).setPreCondition((ConditionViewModel) viewModelElement);
                        break;
                    default:
                        System.err.println("ViewModelManager: Add Element not supported for preCondition and " + parentViewModel.getType());
                }
                break;
            case Types.RUNTIMECONDITION:
                switch (parentViewModel.getType()){
                    case Types.BEHAVIOUR:
                        ((BehaviourViewModel)parentViewModel).setRuntimeCondition((ConditionViewModel) viewModelElement);
                        break;
                    default:
                        System.err.println("ViewModelManager: Add Element not supported for runtimeCondition and " + parentViewModel.getType());
                }
                break;
            case Types.POSTCONDITION:
                switch (parentViewModel.getType()){
                    case Types.BEHAVIOUR:
                        ((BehaviourViewModel)parentViewModel).setPostCondition((ConditionViewModel) viewModelElement);
                        break;
                    case Types.SUCCESSSTATE:
                    case Types.FAILURESTATE:
                        ((StateViewModel)parentViewModel).setPostCondition((ConditionViewModel) viewModelElement);
                        break;
                    default:
                        System.err.println("ViewModelManager: Add Element not supported for postCondition and " + parentViewModel.getType());
                }
                break;
            case Types.QUANTIFIER:
                ((ConditionViewModel) parentViewModel).getQuantifiers().add((QuantifierViewModel) viewModelElement);
                break;
            case Types.ROLE_CHARCTERISTIC:
                CharacteristicViewModel characteristicViewModel = (CharacteristicViewModel) viewModelElement;
                if (event.getEventType() == ModelEventType.ELEMENT_CREATED) {
                    RoleViewModel roleViewModel = (RoleViewModel) parentViewModel;
                    characteristicViewModel.setRoleViewModel(roleViewModel);
                }
                break;
            case Types.ROLE:
                RoleViewModel roleViewModel = (RoleViewModel) viewModelElement;
                if (event.getEventType() == ModelEventType.ELEMENT_CREATED) {
                    RoleSetViewModel roleSetViewModel = (RoleSetViewModel) parentViewModel;
                    roleSetViewModel.addRole(roleViewModel);
                }
                break;
            case Types.TASKREPOSITORY:
            case Types.ROLESET:
            case Types.BENDPOINT:
                //No-OP
                break;
            case Types.CONFIGURATION:
                System.err.println("ViewModelManager: Adding configurations not implemented, yet!");
                break;
            default:
                System.err.println("ViewModelManager: Add Element not supported for type: " + viewModelElement.getType());
                //TODO: maybe handle other types
        }
    }

    private void addToPlan(PlanViewModel parentPlan, ViewModelElement element, ModelEvent event) {
        IPluginEventHandler pluginHandler = MainWindowController.getInstance().getConfigWindowController().getPluginEventHandler();
        ConditionViewModel conditionViewModel;
        switch (event.getElementType()) {
            case Types.STATE:
            case Types.SUCCESSSTATE:
            case Types.FAILURESTATE:
                StateViewModel stateViewModel = (StateViewModel) element;
                if(event.getUiElement() != null) {
                    stateViewModel.setXPosition(event.getUiElement().getX());
                    stateViewModel.setYPosition(event.getUiElement().getY());
                } else {
                    stateViewModel.setXPosition(((StateViewModel) element).getXPosition());
                    stateViewModel.setYPosition(((StateViewModel) element).getYPosition());
                }
                parentPlan.getStates().add(stateViewModel);
                break;
            case Types.TRANSITION:
                Transition transition = (Transition) event.getElement();
                TransitionViewModel transitionViewModel = (TransitionViewModel) element;
                transitionViewModel.setInState((StateViewModel) getViewModelElement(transition.getInState()));
                transitionViewModel.setOutState((StateViewModel) getViewModelElement(transition.getOutState()));
                parentPlan.getTransitions().add((TransitionViewModel) element);
                StateViewModel outStateViewModel = transitionViewModel.getOutState();
                StateViewModel inStateViewModel = transitionViewModel.getInState();
                inStateViewModel.getOutTransitions().add(transitionViewModel);
                outStateViewModel.getInTransitions().add(transitionViewModel);

                if(transition.getPreCondition() != null){
                    conditionViewModel = (ConditionViewModel) getViewModelElement(transition.getPreCondition());
                    transitionViewModel.setPreCondition(conditionViewModel);
                    transitionViewModel.getPreCondition().setPluginName(pluginHandler.getAvailablePlugins().get(0));
                    transition.getPreCondition().setPluginName(pluginHandler.getAvailablePlugins().get(0));
                }
                if(transition.getSynchronisation() != null) {
                    SynchronisationViewModel sViewModel = (SynchronisationViewModel) getViewModelElement(transition.getSynchronisation());
                    sViewModel.getTransitions().add(transitionViewModel);
                }
                break;
            case Types.ENTRYPOINT:
                EntryPointViewModel entryPointViewModel = (EntryPointViewModel) element;
                if (event.getUiElement() != null) {
                    entryPointViewModel.setXPosition(event.getUiElement().getX());
                    entryPointViewModel.setYPosition(event.getUiElement().getY());
                } else {
                    entryPointViewModel.setXPosition(((EntryPointViewModel) element).getXPosition());
                    entryPointViewModel.setYPosition(((EntryPointViewModel) element).getYPosition());
                }
                parentPlan.getEntryPoints().add((EntryPointViewModel) element);

                EntryPoint ePoint = (EntryPoint) modelManager.getPlanElement(entryPointViewModel.getId());
                if (ePoint.getState() != null) {
                    stateViewModel = (StateViewModel) getViewModelElement(ePoint.getState());
                    stateViewModel.setEntryPoint(entryPointViewModel);
                    entryPointViewModel.setState(stateViewModel);
                }
                break;
            case Types.BENDPOINT:
                transitionViewModel = (TransitionViewModel) element;
                // remove<->put to fire listeners, to redraw
                parentPlan.getTransitions().remove(transitionViewModel);
                parentPlan.getTransitions().add(transitionViewModel);
                break;
            case Types.SYNCHRONISATION: {
                SynchronisationViewModel syncViewModel = (SynchronisationViewModel) element;
                if(event.getUiElement() != null) {
                    syncViewModel.setXPosition(event.getUiElement().getX());
                    syncViewModel.setYPosition(event.getUiElement().getY());
                } else {
                    syncViewModel.setXPosition(((SynchronisationViewModel) element).getXPosition());
                    syncViewModel.setYPosition(((SynchronisationViewModel) element).getYPosition());
                }
                parentPlan.getSynchronisations().add(syncViewModel);
                Synchronisation synchronisation = (Synchronisation) modelManager.getPlanElement(syncViewModel.getId());
                if(synchronisation.getSyncedTransitions().size() != 0) {
                    for (Transition transition1: synchronisation.getSyncedTransitions()) {
                        TransitionViewModel transitionViewModel1 = (TransitionViewModel) getViewModelElement(transition1);
                        syncViewModel.getTransitions().add(transitionViewModel1);
                    }
                }
            } break;
            case Types.VARIABLE:
                parentPlan.getVariables().add((VariableViewModel)element);
                break;
            case Types.PRECONDITION:
                parentPlan.setPreCondition((ConditionViewModel) element);
                break;
            case Types.RUNTIMECONDITION:
                parentPlan.setRuntimeCondition((ConditionViewModel) element);
                break;
            case Types.VARIABLEBINDING:
                // TODO: REWORK!
                // Return Variable Bindings by undo
                VariableBindingViewModel var = null;
                for (StateViewModel stateViewModel1:parentPlan.getStates()) {
                    State state1 = (State) modelManager.getPlanElement(stateViewModel1.getId());
                    for (Object object: state1.getVariableBindings()) {
                        if(((VariableBinding) object).getId() == element.getId()){
                            var = (VariableBindingViewModel) element;
                        }
                    }
                    if(var != null) {
                        stateViewModel1.addVariableBinding(var);
                    }
                }
                break;
            default:
                System.err.println("ViewModelManager: Add Element to plan not supported for type: " + element.getType());
                break;
        }
    }

    public void connectElement(ModelEvent event) {
        PlanElement parentPlanElement = modelManager.getPlanElement(event.getParentId());
        ViewModelElement parentViewModel = getViewModelElement(parentPlanElement);
        ViewModelElement viewModelElement = getViewModelElement(event.getElement());

        switch (event.getElementType()) {
            case Types.SYNCTRANSITION:
                ((SynchronisationViewModel) parentViewModel).getTransitions().add((TransitionViewModel) viewModelElement);
                break;
            case Types.INITSTATECONNECTION:
                ((EntryPointViewModel) viewModelElement).setState((StateViewModel) parentViewModel);
                ((StateViewModel) parentViewModel).setEntryPoint((EntryPointViewModel) viewModelElement);
                break;
            default:
                System.err.println("ViewModelManager: Connect Element not supported for type: " + event.getElementType());
                break;
        }

    }

    public void disconnectElement(ModelEvent event) {
        PlanElement parentPlanElement = modelManager.getPlanElement(event.getParentId());
        ViewModelElement parentViewModel = getViewModelElement(parentPlanElement);
        ViewModelElement viewModelElement = getViewModelElement(event.getElement());

        switch (event.getElementType()) {
            case Types.SYNCTRANSITION:
                ((SynchronisationViewModel) parentViewModel).getTransitions().remove((TransitionViewModel) viewModelElement);
                break;
            default:
                System.err.println("ViewModelManager: Disconnect Element not supported for type: " + event.getElementType());
                break;
        }
    }

    public void changePosition(PlanElementViewModel planElementViewModel, ModelEvent event) {
        if (event.getElementType().equals(Types.BENDPOINT)){
            for(BendPoint bPoint : event.getUiElement().getBendPoints()){
                if(bPoint.getId() == event.getRelatedObjects().get(Types.BENDPOINT)){
                    BendPointViewModel bendPointViewModel = (BendPointViewModel) this.getViewModelElement(bPoint);

                    bendPointViewModel.setX(event.getUiElement().getX());
                    bendPointViewModel.setY(event.getUiElement().getY());
                }
            }
        } else {
            planElementViewModel.setXPosition(event.getUiElement().getX());
            planElementViewModel.setYPosition(event.getUiElement().getY());
        }
    }

    public void changeElementAttribute(ViewModelElement viewModelElement, String changedAttribute, Object newValue, Object oldValue) {
        try {
            if (newValue instanceof Map.Entry || (viewModelElement instanceof  ConfigurationViewModel && changedAttribute.equals("parameters"))) {
                ConfigurationViewModel configurationViewModel = (ConfigurationViewModel) viewModelElement;
                configurationViewModel.modifyParameter((Map.Entry<String, String>)newValue, (Map.Entry<String, String>)oldValue);
            } else {
                BeanUtils.setProperty(viewModelElement, changedAttribute, newValue);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void updatePlansInPlanTypeViewModels(PlanViewModel planViewModel, ModelEventType type) {
        for(PlanType planType : modelManager.getPlanTypes()) {
            // Just updating the already existing PlanTypeViewModels and not creating new ones
            if(viewModelElements.containsKey(planType.getId())) {
                PlanTypeViewModel planTypeViewModel = (PlanTypeViewModel) viewModelElements.get(planType.getId());
                switch (type) {
                    case ELEMENT_CREATED:
                    case ELEMENT_PARSED:
                        // Prevent double inclusions
                        if (!planTypeViewModel.getAllPlans().contains(planViewModel)) {
                            planTypeViewModel.addPlanToAllPlans(planViewModel);
                        }
                        break;
                    case ELEMENT_DELETED:
                        planTypeViewModel.removePlanFromAllPlans(planViewModel.getId());
                        break;
                }
            }
        }
    }
}
