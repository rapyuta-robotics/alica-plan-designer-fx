package de.unikassel.vs.alica.stdCheckPlugin;

import de.unikassel.vs.alica.planDesigner.alicamodel.ChangeListenerForDirtyFlag;
import de.unikassel.vs.alica.planDesigner.alicamodel.PreCondition;
import javafx.beans.property.SimpleStringProperty;

public class PreStdCheckCondition extends PreCondition implements IStdCheckCondition {
    protected final SimpleStringProperty functionName = new SimpleStringProperty(this, "functionName", "");
    protected final SimpleStringProperty parameter1 = new SimpleStringProperty(this, "parameter1", "");
    protected final SimpleStringProperty parameter2 = new SimpleStringProperty(this, "parameter2", "");
    protected final SimpleStringProperty parameter3 = new SimpleStringProperty(this, "parameter3", "");
    private ChangeListenerForDirtyFlag changeListenerForDirtyFlag;

    public String getFunctionName() {
        return functionName.get();
    }
    public void setFunctionName(String functionName) {
        this.functionName.set(functionName);
    }
    public SimpleStringProperty functionNameProperty() {
        return functionName;
    }

    public String getParameter1() {
        return parameter1.get();
    }
    public void setParameter1(String parameter1) {
        this.parameter1.set(parameter1);
    }
    public SimpleStringProperty parameter1Property() {
        return parameter1;
    }

    public String getParameter2() {
        return parameter2.get();
    }
    public void setParameter2(String parameter2) {
        this.parameter2.set(parameter2);
    }
    public SimpleStringProperty parameter2Property() {
        return parameter2;
    }

    public String getParameter3() {
        return parameter3.get();
    }
    public void setParameter3(String parameter3) {
        this.parameter3.set(parameter3);
    }
    public SimpleStringProperty parameter3Property() {
        return parameter3;
    }

    public void registerDirtyFlag(ChangeListenerForDirtyFlag listener) {
        if (listener == null) {
            return;
        }
        this.changeListenerForDirtyFlag = listener;
        this.functionName.addListener(listener);
        this.parameter1.addListener(listener);
        this.parameter2.addListener(listener);
        this.parameter3.addListener(listener);
    }
}
