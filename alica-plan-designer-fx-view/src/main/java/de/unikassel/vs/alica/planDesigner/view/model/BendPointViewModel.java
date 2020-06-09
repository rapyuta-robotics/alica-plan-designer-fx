package de.unikassel.vs.alica.planDesigner.view.model;

import de.unikassel.vs.alica.planDesigner.view.Types;

import java.util.Arrays;

public class BendPointViewModel extends PlanElementViewModel {
    private double x;
    private double y;
    private TransitionViewModel transition;

    public BendPointViewModel(long id, String name) {
        super(id, name, Types.BENDPOINT);

        this.uiPropertyList.clear();
        this.uiPropertyList.addAll(Arrays.asList("name", "id", "comment", "relativeDirectory"));
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public TransitionViewModel getTransition() {
        return transition;
    }

    public void setTransition(TransitionViewModel transition) {
        this.transition = transition;
    }
}
