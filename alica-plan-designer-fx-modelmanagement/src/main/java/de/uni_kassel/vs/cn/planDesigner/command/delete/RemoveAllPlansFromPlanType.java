package de.uni_kassel.vs.cn.planDesigner.command.delete;

import de.uni_kassel.vs.cn.planDesigner.alicamodel.Plan;
import de.uni_kassel.vs.cn.planDesigner.alicamodel.PlanType;
import de.uni_kassel.vs.cn.planDesigner.command.AbstractCommand;
import de.uni_kassel.vs.cn.planDesigner.modelmanagement.ModelManager;

import java.util.ArrayList;
import java.util.List;

public class RemoveAllPlansFromPlanType extends AbstractCommand {

    private List<Plan> backupPlans;
    private PlanType planType;

    public RemoveAllPlansFromPlanType(ModelManager manager, PlanType planType) {
        super(manager);
        this.planType = planType;
    }

    @Override
    public void doCommand() {
        if (backupPlans == null) {
            backupPlans = new ArrayList<>();
        }
        backupPlans.addAll(planType.getPlans());
        planType.getPlans().clear();

    }

    @Override
    public void undoCommand() {
        planType.getPlans().addAll(backupPlans);
    }

    @Override
    public String getCommandString() {
        return "Remove all plans from plantype " + planType.getName();
    }
}