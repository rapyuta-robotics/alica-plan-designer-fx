package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.Role;
import de.unikassel.vs.alica.planDesigner.alicamodel.RoleSet;
import de.unikassel.vs.alica.planDesigner.view.model.RoleSetViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.RoleViewModel;

public class RoleSetViewModelFactory extends InternalViewModelFactory<RoleSetViewModel, RoleSet> {

    @Override
    RoleSetViewModel create(RoleSet roleSet) {
        RoleSetViewModel roleSetViewModel = new RoleSetViewModel(roleSet.getId(), roleSet.getName(), roleSet.getDefaultPriority(), roleSet.getDefaultRoleSet());
        roleSetViewModel.setComment(roleSet.getComment());
        roleSetViewModel.setRelativeDirectory(roleSetViewModel.getRelativeDirectory());
        // we need to put the RoleSet before creating roles, in order to avoid circles (RoleSet <-> Role)
        viewModelManager.putViewModelForAvoidingLoops(roleSetViewModel);

        for (Role role : roleSet.getRoles()) {
            RoleViewModel roleViewModel = (RoleViewModel) viewModelManager.getViewModelElement(role);
            roleViewModel.setParentId(roleSet.getId());
            roleSetViewModel.addRole(roleViewModel);
        }

        return roleSetViewModel;
    }
}
