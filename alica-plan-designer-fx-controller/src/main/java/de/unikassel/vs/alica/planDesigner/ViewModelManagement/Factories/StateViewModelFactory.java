package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.*;
import de.unikassel.vs.alica.planDesigner.uiextensionmodel.UiElement;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.model.*;

public class StateViewModelFactory extends InternalViewModelFactory<StateViewModel, State> {
    @Override
    StateViewModel create(State state) {
        StateViewModel stateViewModel;
        if (state instanceof TerminalState) {
            TerminalState terminalState = (TerminalState) state;
            stateViewModel = new StateViewModel(state.getId(), state.getName(), terminalState.isSuccess() ? Types.SUCCESSSTATE : Types.FAILURESTATE);
            PostCondition postCondition = terminalState.getPostCondition();
            if (postCondition != null) {
                stateViewModel.setPostCondition((ConditionViewModel) viewModelManager.getViewModelElement(postCondition));
            }
        } else {
            stateViewModel = new StateViewModel(state.getId(), state.getName(), Types.STATE);
        }

        stateViewModel.setComment(state.getComment());
        stateViewModel.setParentId(state.getParentPlan().getId());
        UiElement uiElement = modelManager.getPlanUIExtensionPair(state.getParentPlan().getId()).getUiElement(state.getId());
        stateViewModel.setXPosition(uiElement.getX());
        stateViewModel.setYPosition(uiElement.getY());

        for (ConfAbstractPlanWrapper confAbstractPlanWrapper : state.getConfAbstractPlanWrappers()) {
            stateViewModel.addConfAbstractPlanWrapper((ConfAbstractPlanWrapperViewModel) viewModelManager.getViewModelElement(confAbstractPlanWrapper));
        }
        if (state.getEntryPoint() != null) {
            stateViewModel.setEntryPoint((EntryPointViewModel) viewModelManager.getViewModelElement(state.getEntryPoint()));
        }
        if(state instanceof TerminalState && ((TerminalState) state).getPostCondition() != null) {
            stateViewModel.setPostCondition((ConditionViewModel) viewModelManager.getViewModelElement(((TerminalState) state).getPostCondition()));
        }
        for (VariableBinding param: state.getVariableBindings()) {
            stateViewModel.addVariableBinding((VariableBindingViewModel) viewModelManager.getViewModelElement(param));
        }

        return stateViewModel;
    }
}
