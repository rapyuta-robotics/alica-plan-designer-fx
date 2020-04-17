package de.unikassel.vs.alica.planDesigner.view.model;

import de.unikassel.vs.alica.planDesigner.view.Types;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;

import java.util.Arrays;

public class AnnotatedPlanViewModel extends ViewModelElement {

    protected final SimpleBooleanProperty activated = new SimpleBooleanProperty(this, "activated", false);
    protected final SimpleLongProperty planId = new SimpleLongProperty(this, "planId", 0L);

    public AnnotatedPlanViewModel(long id, String name, boolean activated, long planId) {
        super(id, name, Types.ANNOTATEDPLAN);
        this.activated.setValue(activated);
        this.planId.setValue(planId);

        this.uiPropertyList.clear();
        this.uiPropertyList.addAll(Arrays.asList("name", "id", "relativeDirectory", "activated", "planId"));
    }

    // TODO: delete if not used
//    public AnnotatedPlanViewModel(ViewModelElement element, boolean activated) {
//        super(element.getId(), element.getName(), element.getType());
//        this.activated.setValue(activated);
//    }

    public final BooleanProperty activatedProperty() {
        return this.activated;
    }

    public boolean isActivated() {
        return this.activated.get();
    }

    public void setActivated(boolean activated) {
        this.activated.setValue(activated);
    }

    public long getPlanId() {
        return this.planId.getValue();
    }
}
