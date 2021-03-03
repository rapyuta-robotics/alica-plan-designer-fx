package de.unikassel.vs.alica.stdCheckPlugin;

import javafx.beans.property.SimpleStringProperty;

public interface IStdCheckCondition {
    public String getFunctionName();
    public void setFunctionName(String functionName);
    public SimpleStringProperty functionNameProperty();

    public String getParameter1();
    public void setParameter1(String parameter1);
    public SimpleStringProperty parameter1Property();

    public String getParameter2();
    public void setParameter2(String parameter2);
    public SimpleStringProperty parameter2Property();

    public String getParameter3();
    public void setParameter3(String parameter3);
    public SimpleStringProperty parameter3Property();
}


