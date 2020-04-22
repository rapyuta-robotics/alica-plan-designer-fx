package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.Configuration;
import de.unikassel.vs.alica.planDesigner.view.model.ConfigurationViewModel;

import java.util.Map;

public class ConfigurationViewModelFactory extends InternalViewModelFactory<ConfigurationViewModel, Configuration> {

    @Override
    ConfigurationViewModel create(Configuration configuration) {
        ConfigurationViewModel configurationViewModel = new ConfigurationViewModel(configuration.getId(), configuration.getName());
        configurationViewModel.setComment(configuration.getComment());
        configurationViewModel.setRelativeDirectory(configuration.getRelativeDirectory());
        for (Map.Entry<String, String> keyValuePair : configuration.getParameters().entrySet()) {
            configurationViewModel.modifyParameter(keyValuePair, null);
        }
        return configurationViewModel;
    }
}
