package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.alicamodel.Quantifier;
import de.unikassel.vs.alica.planDesigner.view.model.QuantifierViewModel;

public class QuantifierViewModelFactory extends InternalViewModelFactory<QuantifierViewModel, Quantifier> {


    @Override
    QuantifierViewModel create(Quantifier quantifier) {
        QuantifierViewModel viewModel = new QuantifierViewModel(quantifier.getId(), quantifier.getName());
        if(quantifier.getScope() != null){
            viewModel.setScope(quantifier.getScope().getId());
        }
        viewModel.setQuantifierType(quantifier.getQuantifierType());
        viewModel.setComment(quantifier.getComment());
        if(quantifier.getSorts() != null) {
            viewModel.setSorts(String.join(" ", quantifier.getSorts()));
        }
        return  viewModel;
    }
}
