package de.unikassel.vs.alica.planDesigner.ViewModelManagement.Factories;

import de.unikassel.vs.alica.planDesigner.ViewModelManagement.ViewModelManager;
import de.unikassel.vs.alica.planDesigner.alicamodel.PlanElement;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelManager;
import de.unikassel.vs.alica.planDesigner.view.model.ViewModelElement;

public abstract class InternalViewModelFactory<T extends ViewModelElement, U extends PlanElement> {

    protected static ViewModelManager viewModelManager = null;
    public static void setViewModelManager(ViewModelManager viewModelManager) {
        if (InternalViewModelFactory.viewModelManager == null) {
            InternalViewModelFactory.viewModelManager = viewModelManager;
        }
    }

    protected static ModelManager modelManager = null;
    public static void setModelManager(ModelManager modelManager) {
        if (InternalViewModelFactory.modelManager == null) {
            InternalViewModelFactory.modelManager = modelManager;
        }
    }

    /**
     * Actually dummy objects that represent references are replaced by
     * the resolveReferences Method in the ModelManager. However, during
     * this process some ViewModel objects are already created, due to the
     * processing of dirty-Flag events.
     *
     * The solution to handle these events, although not all dummy
     * objects are resolved, this method is used in certain places of
     * the ViewModel factories.
     * @param dummy
     * @return
     */
    protected static PlanElement resolveDummy(PlanElement dummy) {
        return modelManager.getPlanElement(dummy.getId());
    }

    abstract T create(U planElement);
}
