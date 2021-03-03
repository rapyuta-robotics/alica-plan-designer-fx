package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.Characteristic;
import de.unikassel.vs.alica.planDesigner.view.model.CharacteristicViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.RoleViewModel;

public class CharacteristicViewModelFactory extends InternalViewModelFactory<CharacteristicViewModel, Characteristic> {

    @Override
    CharacteristicViewModel create(Characteristic characteristic) {
        CharacteristicViewModel characteristicViewModel = new CharacteristicViewModel(characteristic.getId(), characteristic.getName(), null);
        characteristicViewModel.setComment(characteristic.getComment());
        characteristicViewModel.setParentId(characteristic.getRole().getId());
        characteristicViewModel.setValue(characteristic.getValue());
        characteristicViewModel.setWeight(String.valueOf(characteristic.getWeight()));
        RoleViewModel roleViewModel = (RoleViewModel) viewModelManager.getViewModelElement(characteristic.getRole());

        if(roleViewModel != null)
            roleViewModel.addRoleCharacteristic(characteristicViewModel);
        return characteristicViewModel;
    }
}
