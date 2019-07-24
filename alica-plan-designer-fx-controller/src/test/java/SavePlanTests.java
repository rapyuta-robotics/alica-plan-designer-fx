import de.unikassel.vs.alica.planDesigner.PlanDesigner;
import de.unikassel.vs.alica.planDesigner.PlanDesignerApplication;
import de.unikassel.vs.alica.planDesigner.alicamodel.*;
import de.unikassel.vs.alica.planDesigner.configuration.Configuration;
import de.unikassel.vs.alica.planDesigner.configuration.ConfigurationManager;
import de.unikassel.vs.alica.planDesigner.modelmanagement.Extensions;
import de.unikassel.vs.alica.planDesigner.modelmanagement.ModelManager;
import de.unikassel.vs.alica.planDesigner.view.I18NRepo;
import de.unikassel.vs.alica.planDesigner.view.editor.container.StateContainer;
import de.unikassel.vs.alica.planDesigner.view.editor.container.TransitionContainer;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.PointQuery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testfx.service.query.impl.NodeQueryUtils.hasText;

public class SavePlanTests extends ApplicationTest {
    private final String taskRepository = "testfxTaskRepo";
    private final String taskName = "testfxTask";
    private final String planName = "testfxPlan";
    private final String planName2 = "testfxPlan2";
    private final String planTypeName = "testfxPlanType";
    private final String behaviourName = "testfxBehaviour";
    private final String roleSetName = "testfxRoleSet";
    private final String roleName1 = "testfxRole1";
    private final String roleName2 = "testfxRole2";

    private final String taskRepositoryExtension = taskRepository + "." + Extensions.TASKREPOSITORY;
    private final String planNameExtension = planName + "." + Extensions.PLAN;
    private final String planNameExtension2 = planName2 + "." + Extensions.PLAN;
    private final String planTypeNameExtension = planTypeName + "." + Extensions.PLANTYPE;
    private final String behaviourNameExtension = behaviourName + "." + Extensions.BEHAVIOUR;
    private final String roleSetNameExtension = roleSetName + "." + Extensions.ROLESET;

    private final String configName = "testfxConfig";
    private final String rootConfigFolder = ConfigurationManager.getInstance().getPlanDesignerConfigFolder().getPath();
    private final String configFolder = rootConfigFolder + "/testfx";
    private final String configFolderPlans = configFolder + "/plans";
    private final String configFolderRoles = configFolder + "/roles";
    private final String configFolderTasks = configFolder + "/tasks";
    private final String configFolderSrc = configFolder + "/src";
    private final String configFolderPlugin = getPluginsFolder();
    private final String configFile = rootConfigFolder + "/" + configName + ".properties";

    private final String state1Name = "State1";
    private final String state2Name = "State2";
    private final String state3Name = "State3";
    private final String successStateName = "SuccessState";
    private final String precondition1Name = "Precondition1";
    private final String precondition2Name = "Precondition2";
    private final String precondition3Name = "Precondition3";
    private final String precondition4Name = "Precondition4";

    private final String entryPointContainerId = "#EntryPointContainerCircle";
    private final String stateContainerId = "#StateContainer";
    private final String successStateContainerId = "#SuccessStateContainer";
    private final String transitionContainerId = "#TransitionContainer";
    private final String propertySheetId = "#PropertySheet";
    private final String planContentId = "#PlanTabPlanContent";
    private final int EpMinCardinality = 1;
    private final int EpMaxCardinality = 10;

    private I18NRepo i18NRepo = I18NRepo.getInstance();
    private int planElementsCounter = 0;

    private final String defaultName = i18NRepo.getString("label.state.defaultName");


    @Override
    public void start(Stage stage) throws Exception {
        // clean config
        deleteConfig();
        deleteconfigFolder();
        createConfigFolders();

        // process possible taskrepository not exists warning
        Thread thread = new Thread(() -> {
            sleep(2000);
            handleNewTaskRepositoryDialog();
        });
        thread.setDaemon(true);
        thread.start();

        startApplication(stage);
    }

    private void startApplication(Stage stage) throws IOException {
        PlanDesigner.init();
        PlanDesignerApplication planDesignerApplication = new PlanDesignerApplication();
        planDesignerApplication.start(stage);
    }

    @Test
    public void testCreatePlan() {
        // check if configuration is already present
        createConfiguration();

        // init
        createPlan(planName);
        createPlan(planName2);
        createBehaviour();
        createPlanType();

        // create roleset and roles
        createRoleSet();
        openRoleSet();
        createRoles();

        // create task
        createTask();

        // modify plan
        openPlan();
        setMasterPlan();

        placeEntryPoint();
        setCardinality();

        placeState();
        setStateContainerName(defaultName, state1Name);

        placeState();
        setStateContainerName(defaultName, state2Name);

        placeState();
        setStateContainerName(defaultName, state3Name);

        placeSuccessState();
        setSuccessStateContainerName(defaultName, successStateName);

        drawInitTransition(getEntryPointContainer(), getStateContainer1());

        drawTransition(getStateContainer1(), getStateContainer2());
        drawTransition(getStateContainer1(), getStateContainer3());
        drawTransition(getStateContainer3(), getStateContainer2());
        drawTransition(getStateContainer2(), getSuccessStateContainer());

        repositionPlanElement(getEntryPointContainer(), -0.2, -0.5);
        repositionPlanElement(getStateContainer1(), -0.1, -0.5);
        repositionPlanElement(getStateContainer2(), 0, -0.5);
        repositionPlanElement(getStateContainer3(), -0.3, 0);
        repositionPlanElement(getSuccessStateContainer(), 0, -0.5);

        placeBehaviour(getStateContainer1());
        placePlanType(getStateContainer2());
        placePlan(getStateContainer3());

        setPrecondition(getTransitionLine(getStateContainer1(), getStateContainer3()), precondition1Name);
        setPrecondition(getTransitionLine(getStateContainer3(), getStateContainer2()), precondition2Name);
        setPrecondition(getTransitionLine(getStateContainer1(), getStateContainer2()), precondition3Name);
        setPrecondition(getTransitionLine(getStateContainer2(), getSuccessStateContainer()), precondition4Name);

        createBendpoint(getStateContainer3(), getStateContainer2());

        saveCurrentData();

        // check saved data
        checkConfig();

        // check cpp code generation
        generateCppCode();
        checkCppCode();

        // clean
        deletePlan();
        deleteBehaviour();
        deleteRoleSet();
    }

    private Node getStateContainer1() {
        // sometimes the nodes lose their scenes so we can not cache them safely
        return getContainerNode(stateContainerId, state1Name);
    }

    private Node getStateContainer2() {
        return getContainerNode(stateContainerId, state2Name);
    }

    private Node getStateContainer3() {
        return getContainerNode(stateContainerId, state3Name);
    }

    private Node getEntryPointContainer() {
        return getContainerNode(entryPointContainerId);
    }

    private Node getSuccessStateContainer() {
        return getContainerNode(successStateContainerId);
    }

    private void createBendpoint(Node fromState, Node toState) {
        pickTransitionTool();
        clickOn(getTransitionLine(fromState, toState));
        dropElement();
        repositionPlanElement(getTransitionLine(fromState, toState), 0.2, 0);
        dropElement();
    }

    private Node getTransitionLine(Node fromState, Node toState) {
        return lookup(transitionContainerId).queryAll().stream()
                .filter(n -> {
                    TransitionContainer c = (TransitionContainer) n;
                    StateContainer from = c.getFromState();
                    StateContainer to = c.getToState();
                    return from.equals(fromState) && to.equals(toState);
                })
                .findAny()
                .map(n -> ((TransitionContainer) n).getVisualRepresentation())
                .get();
    }

    private void setCardinality() {
        doubleClickOn(entryPointContainerId);
        moveTo("minCardinality");
        clickOn("0");
        write(String.valueOf(EpMinCardinality));
        moveTo("maxCardinality");
        clickOn("0");
        write(String.valueOf(EpMaxCardinality));
    }

    private void setMasterPlan() {
        moveTo("masterPlan");
        clickOn(".check-box");
    }

    private void repositionPlanElement(Node container, double factX, double factY) {
        Node planContent = lookup("#PlanTabPlanContent").queryFirst();
        Bounds planBounds = planContent.getBoundsInLocal();
        double planWidth = planBounds.getWidth();
        double planHeight = planBounds.getHeight();

        Bounds containerBounds = container.localToScreen(container.getBoundsInLocal());
        double origX = containerBounds.getMinX() + containerBounds.getWidth() / 2;
        double origY = containerBounds.getMinY() + containerBounds.getHeight() / 2;

        double resX = origX + (planWidth * factX);
        double resY = origY + (planHeight * factY);

        drag(container).dropTo(resX, resY);
    }

    private void setPrecondition(Node transition, String name) {
        doubleClickOn(transition);

        moveTo(propertySheetId);
        clickOn(i18NRepo.getString("label.caption.preCondtions"));

        clickOn("NONE");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        moveTo("enabled");
        clickOn(".check-box");

        TransitionContainer transitionContainer = (TransitionContainer) transition.getParent();
        long transitionId = transitionContainer.getContainedElement().getPreCondition().getId();

        Node nameField = lookup(n -> {
            if (!(n instanceof TextField) || n.isDisabled()) {
                return false;
            }
            TextField textField = (TextField) n;
            if (textField.getText() == null) {
                return false;
            }
            return textField.getText().equals(String.valueOf(transitionId));
        }).queryFirst();
        clickOn(nameField);
        write(name);
    }

    private void deleteConfig() {
        // delete from ConfigurationManager
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        configurationManager.removeConfiguration(configName);
        configurationManager.writeToDisk();
    }

    private void createConfiguration() {
        // open configuration dialog
        clickOn("#editMenu");
        clickOn("Configure");
        sleep(1000);

        // click on first free config field
        Node firstListElement = lookup("#availableWorkspacesListView").lookup("").selectAt(0).queryFirst();
        clickOn(firstListElement);

        // enter config name
        type(KeyCode.ENTER);
        write(configName);
        type(KeyCode.ENTER);
        sleep(1000);

        // enter folders
        for(String folder: Arrays.asList(configFolderPlans, configFolderRoles, configFolderTasks, configFolderSrc, configFolderPlugin)) {
            type(KeyCode.TAB);
            write(folder);
            type(KeyCode.TAB);
        }

        // plugins folder selector have to be opened to reload the default plugin drop down menu
        clickOn("#pluginsFolderFileButton");
        type(KeyCode.ESCAPE);

        // activate default plugin
        clickOn("#defaultPluginComboBox");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        // save and activate
        clickOn("#saveButton");
        clickOn("#activeButton");

        // create TaskRepo
        handleNewTaskRepositoryDialog();

        FxAssert.verifyThat("#activeConfLabel", hasText(configName));

        // exit
        clickOn("#saveAndExitButton");
    }

    private void handleNewTaskRepositoryDialog() {
        String warningMessage = i18NRepo.getString("label.error.missing.taskrepository");
        Node warning = lookup(warningMessage).queryFirst();
        if (warning != null) {
            clickOn(i18NRepo.getString("action.create.taskrepository"));
            sleep(1000);
            clickOn("#nameTextField");
            write(taskRepository);
            clickOn("#createButton");
            sleep(1000);
        }
    }

    private String getPluginsFolder() {
        Path currentRelativePath = Paths.get("");
        String projectRoot = currentRelativePath.toAbsolutePath().getParent().toString();
        String pluginsFolder = projectRoot + "/alica-plan-designer-fx-default-plugin";
        return pluginsFolder;
    }

    private void createConfigFolders() {
        for (String folder: Arrays.asList(configFolder, configFolderPlans, configFolderRoles, configFolderTasks, configFolderSrc)) {
            new File(folder).mkdir();
        }
    }

    private void deleteconfigFolder() {
        // deletes an old config folder to start clean
        Path path = Paths.get(configFolder);
        try {
            Files.walk(path)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            System.out.println("Config folder can not be deleted");
        }
    }

    private void createTask() {
        // open taskrepo
        openTasksView();
        doubleClickOn(taskRepositoryExtension);
        sleep(1000);

        // create task
        clickOn("#newTaskNameTextField");
        write(taskName);
        clickOn("#createTaskButton");

        saveCurrentData();
        closeFileThreeElements();
    }

    private void placeBehaviour(Node destination) {
        placeTabElementHelper("#behavioursTab", behaviourName, destination);
    }

    private void placePlanType(Node destination) {
        placeTabElementHelper("#planTypesTab", planTypeName, destination);
    }

    private void placePlan(Node destination) {
        placeTabElementHelper("#plansTab", planName2, destination);
    }

    private void placeTabElementHelper(String tabQuery, String elemenQuery, Node destination) {
        clickOn(tabQuery);
        type(KeyCode.TAB);
        for (int i = 0; i < 5; i++) {
            type(KeyCode.PAGE_DOWN);
        }
        drag(elemenQuery).dropTo(destination);
    }

    private void saveCurrentData() {
        sleep(1000);
        press(KeyCode.CONTROL, KeyCode.S);
        release(KeyCode.CONTROL, KeyCode.S);
    }

    private void drawInitTransition(Node from, Node to) {
        clickOn("#InitTransitionToolButton");
        drawTransitionHelper(from, to);
    }


    private void drawTransition(Node from, Node to) {
        pickTransitionTool();
        drawTransitionHelper(from, to);
    }

    private void pickTransitionTool() {
        clickOn("#TransitionToolButton");
    }

    private void drawTransitionHelper(Node from, Node to) {
        clickOn(from);
        clickOn(to);
        dropElement();
    }

    private void placeEntryPoint() {
        // place entryPoint
        clickOn("#EntryPointToolButton");
        clickOn(freePlanContentPos());

        // choose created task
        clickOn("#taskComboBox");
        clickOn(taskName);
        clickOn("#confirmTaskChoiceButton");

        dropElement();
    }

    private PointQuery freePlanContentPos() {
        return offset(planContentId, ++planElementsCounter * 80, 0);
    }

    private void placeState() {
        clickOn("#StateToolButton");
        clickOn(freePlanContentPos());
        dropElement();
    }

    private void setContainerName(String containerId, String oldName, String newName) {
        Node container = getContainerNode(containerId, oldName);
        doubleClickOn(container);
        moveTo(propertySheetId);
        Node nameField = lookup(oldName).queryAll().stream()
                .filter(n -> n instanceof TextField)
                .findAny()
                .get();
        clickOn(nameField);
        write(newName);
    }

    private Node getContainerNode(String containerId) {
        return lookup(containerId).queryAll().stream()
                .findAny()
                .get();
    }

    private Node getContainerNode(String containerId, String name) {
        return lookup(containerId).queryAll().stream()
                .filter(n -> {
                    StateContainer st = (StateContainer) n;
                    String stName = st.getState().getName();
                    return stName.equals(name);
                })
                .findAny()
                .get();
    }

    private void setStateContainerName(String oldName, String newName) {
        setContainerName(stateContainerId, oldName, newName);
    }

    private void setSuccessStateContainerName(String oldName, String newName) {
        setContainerName(successStateContainerId, oldName, newName);
    }

    private void placeSuccessState() {
        clickOn("#SuccessStateToolButton");
        clickOn(freePlanContentPos());
        dropElement();
    }

    private void dropElement() {
        type(KeyCode.ESCAPE);
    }

    private void createPlan(String planName) {
        createElemtentHelper("plans", "Plan", planName, planNameExtension);
    }

    private void createPlanType() {
        createElemtentHelper("plans", "PlanType", planTypeName, planTypeNameExtension);
    }

    private void createBehaviour() {
        createElemtentHelper("plans", "Behaviour", behaviourName, behaviourNameExtension);
    }

    private void createRoleSet() {
        createElemtentHelper("roles", "RoleSet", roleSetName, roleSetNameExtension);
    }

    private void createElemtentHelper(String rootElementName, String type, String name, String nameExtension) {
        rightClickOn(rootElementName);
        clickOn("New");
        moveTo("Plan");  // avoid closing menu if mouse is outside of menu dialog
        clickOn(type);
        clickOn("#nameTextField");
        write(name);
        clickOn("#createButton");

        if (rootElementName.equals("plans")) {
            openPlansView();
        } else {
            openRolesView();
        }
        assertExists(nameExtension);
        closeFileThreeElements();
    }

    private void openRoleSet() {
        openRolesView();
        doubleClickOn(roleSetNameExtension);
    }

    private void createRoles() {
        Node firstListElement = lookup("#RoleTableView").lookup("").selectAt(1).queryFirst();

        // create first role
        clickOn(firstListElement);
        write(roleName1);
        type(KeyCode.ENTER);

        // create second role
        type(KeyCode.ENTER);
        write(roleName2);
        type(KeyCode.ENTER);

        assertExists(roleName1);
        assertExists(roleName2);

        saveCurrentData();
        closeFileThreeElements();
    }

    private void openPlan() {
        openPlansView();
        doubleClickOn(planNameExtension);
    }

    private void deletePlan() {
        openPlansView();
        clickOn(planNameExtension);
        type(KeyCode.DELETE);

        assertNotExists(planNameExtension);
    }

    private void deleteBehaviour() {
        openPlansView();
        clickOn(behaviourNameExtension);
        type(KeyCode.DELETE);

        assertNotExists(behaviourNameExtension);
    }

    private void deleteRoleSet() {
        openRolesView();
        clickOn(roleSetNameExtension);
        type(KeyCode.DELETE);

        assertNotExists(roleSetNameExtension);
    }

    private void openPlansView() {
        clickOn("#fileTreeView");
        type(KeyCode.PAGE_UP);
        type(KeyCode.RIGHT);
    }

    private void closeFileThreeElements() {
        clickOn("#fileTreeView");
        type(KeyCode.PAGE_UP);
        for (int i = 0; i < 5; i++) {
            type(KeyCode.LEFT);
            type(KeyCode.DOWN);
        }
    }

    private void openRolesView() {
        clickOn("#fileTreeView");
        type(KeyCode.PAGE_UP);
        type(KeyCode.DOWN);
        type(KeyCode.RIGHT);
    }

    private void openTasksView() {
        clickOn("#fileTreeView");
        type(KeyCode.PAGE_DOWN);
        type(KeyCode.RIGHT);
    }

    private void assertExists(String query) {
        sleep(1000);
        Assert.assertEquals(1, lookup(query).queryAll().size());
    }

    private void assertNotExists(String query) {
        sleep(1000);
        Assert.assertEquals(0, lookup(query).queryAll().size());
    }

    private void generateCppCode() {
        clickOn("#codeGenerationMenu");
        clickOn("#generateItem");
        sleep(6000);  // wait for cpp code generation
    }

    private void checkCppCode() {
        ArrayList<String> files = new ArrayList<>();
        Path path = Paths.get(configFolderSrc);

        try {
            Files.walk(path)
                    .filter(Files::isRegularFile)
                    .forEach(f -> {
                        Path relativePath = path.relativize(f);
                        files.add(relativePath.toString());
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        Plan plan = getPlanByName(planName);
        String planNameId = plan.getName() + plan.getId();
        Plan plan2 = getPlanByName(planName2);
        String plan2NameId = plan2.getName() + plan2.getId();

        List<String> expectedFiles = Arrays.asList(
                "include/constraints/" + planNameId + "Constraints.h",
                "include/constraints/" + plan2NameId + "Constraints.h",
                "include/DomainBehaviour.h",
                "include/DomainCondition.h",
                "include/UtilityFunctionCreator.h",
                "include/BehaviourCreator.h",
                "include/ConditionCreator.h",
                "include/ConstraintCreator.h",
                "include/" + planNameId + ".h",
                "include/" + plan2NameId + ".h",
                "include/" + behaviourName + ".h",
                "src/constraints/" + planNameId + "Constraints.cpp",
                "src/constraints/" + plan2NameId + "Constraints.cpp",
                "src/DomainBehaviour.cpp",
                "src/DomainCondition.cpp",
                "src/UtilityFunctionCreator.cpp",
                "src/BehaviourCreator.cpp",
                "src/ConditionCreator.cpp",
                "src/ConstraintCreator.cpp",
                "src/" + planNameId + ".cpp",
                "src/" + plan2NameId + ".cpp",
                "src/" + behaviourName + ".cpp"
        );

        Assert.assertEquals(expectedFiles.size(), files.size());

        for (String file: expectedFiles) {
            Assert.assertTrue(files.contains(file));
        }
    }

    private ModelManager getModelManager() {
        ModelManager modelManager = new ModelManager();
        Configuration activeConfiguration = ConfigurationManager.getInstance().getActiveConfiguration();
        String plansPath = activeConfiguration.getPlansPath();
        String tasksPath = activeConfiguration.getTasksPath();
        String rolesPath = activeConfiguration.getRolesPath();
        modelManager.setPlansPath(plansPath);
        modelManager.setTasksPath(tasksPath);
        modelManager.setRolesPath(rolesPath);
        modelManager.loadModelFromDisk();
        return modelManager;
    }

    private Plan getPlanByName(String name) {
        ModelManager modelManager = getModelManager();
        ArrayList<Plan> plans = modelManager.getPlans();
        return plans.stream()
                .filter(p -> p.getName().equals(name))
                .findAny()
                .get();
    }

    private void checkConfig() {
        ModelManager modelManager = getModelManager();

        // get root elements
        Plan plan = getPlanByName(planName);
        Plan plan2 = getPlanByName(planName2);

        ArrayList<PlanType> planTypes = modelManager.getPlanTypes();
        PlanType planType = planTypes.stream()
                .filter(p -> p.getName().equals(planTypeName))
                .findAny()
                .get();

        List<EntryPoint> entryPoints = plan.getEntryPoints();
        EntryPoint entryPoint = entryPoints.get(0);

        List<State> states = plan.getStates();
        State successState = states.stream()
                .filter(s -> s.getName().equals(successStateName))
                .findAny()
                .get();
        State state1 = states.stream()
                .filter(s -> s.getName().equals(state1Name))
                .findAny()
                .get();
        State state2 = states.stream()
                .filter(s -> s.getName().equals(state2Name))
                .findAny()
                .get();
        State state3 = states.stream()
                .filter(s -> s.getName().equals(state3Name))
                .findAny()
                .get();

        List<Transition> transitions = plan.getTransitions();
        Transition transitionState1ToState2 = transitions.stream()
                .filter(t -> t.getInState().equals(state1))
                .filter(t -> t.getOutState().equals(state2))
                .findAny()
                .get();
        Transition transitionState1ToState3 = transitions.stream()
                .filter(t -> t.getInState().equals(state1))
                .filter(t -> t.getOutState().equals(state3))
                .findAny()
                .get();
        Transition transitionState3ToState2 = transitions.stream()
                .filter(t -> t.getInState().equals(state3))
                .filter(t -> t.getOutState().equals(state2))
                .findAny()
                .get();
        Transition transitionState2ToSuccessState = transitions.stream()
                .filter(t -> t.getInState().equals(state2))
                .filter(t -> t.getOutState().equals(successState))
                .findAny()
                .get();

        ArrayList<Condition> conditions = modelManager.getConditions();
        PreCondition precondition1 = (PreCondition) conditions.stream()
                .filter(c -> c.getName().equals(precondition1Name))
                .findAny()
                .get();
        PreCondition precondition2 = (PreCondition) conditions.stream()
                .filter(c -> c.getName().equals(precondition2Name))
                .findAny()
                .get();
        PreCondition precondition3 = (PreCondition) conditions.stream()
                .filter(c -> c.getName().equals(precondition3Name))
                .findAny()
                .get();
        PreCondition precondition4 = (PreCondition) conditions.stream()
                .filter(c -> c.getName().equals(precondition4Name))
                .findAny()
                .get();

        ArrayList<Behaviour> behaviours = modelManager.getBehaviours();
        Behaviour behaviour = behaviours.stream()
                .filter(b -> b.getName().equals(behaviourName))
                .findAny()
                .get();

        List<de.unikassel.vs.alica.planDesigner.alicamodel.Configuration> configurations = behaviour.getConfigurations();
        de.unikassel.vs.alica.planDesigner.alicamodel.Configuration behaviourConfiguration = configurations.get(0);

        List<Task> tasks = modelManager.getTasks();
        Task task = tasks.stream()
                .filter(t -> t.getName().equals(taskName))
                .findAny()
                .get();

        // test plan
        Assert.assertNotNull(plan.getId());
        Assert.assertEquals(planName, plan.getName());
        Assert.assertEquals("", plan.getComment());
        Assert.assertEquals("", plan.getRelativeDirectory());
        Assert.assertEquals(0, plan.getVariables().size());
        Assert.assertTrue(plan.getMasterPlan());
        Assert.assertEquals(0.0, plan.getUtilityThreshold(), 0.0);
        Assert.assertNull(plan.getPreCondition());
        Assert.assertNull(plan.getRuntimeCondition());

        // test entrypoint
        Assert.assertEquals(1, entryPoints.size());
        Assert.assertNotNull(entryPoint.getId());
        Assert.assertEquals(String.valueOf(entryPoint.getId()), entryPoint.getName());
        Assert.assertEquals("", entryPoint.getComment());
        Assert.assertFalse(entryPoint.getSuccessRequired());
        Assert.assertEquals(EpMinCardinality, entryPoint.getMinCardinality());
        Assert.assertEquals(EpMaxCardinality, entryPoint.getMaxCardinality());
        Assert.assertEquals(task.getId(), entryPoint.getTask().getId());
        Assert.assertEquals(state1.getId(), entryPoint.getState().getId());
        Assert.assertEquals(plan.getId(), entryPoint.getPlan().getId());

        // test state1
        // missing: type
        Assert.assertNotNull(state1.getId());
        Assert.assertEquals(state1Name, state1.getName());
        Assert.assertEquals("", state1.getComment());
        Assert.assertEquals(entryPoint.getId(), state1.getEntryPoint().getId());
        Assert.assertEquals(plan.getId(), state1.getParentPlan().getId());
        Assert.assertEquals(1, state1.getAbstractPlans().size());
        Assert.assertEquals(behaviourConfiguration.getId(), state1.getAbstractPlans().get(0).getId());
        Assert.assertEquals(0, state1.getVariableBindings().size());
        Assert.assertEquals(2, state1.getOutTransitions().size());
        Assert.assertTrue(state1.getOutTransitions().contains(transitionState1ToState2));
        Assert.assertTrue(state1.getOutTransitions().contains(transitionState1ToState3));
        Assert.assertEquals(0, state1.getInTransitions().size());

        // test state2
        // missing: type
        Assert.assertNotNull(state2.getId());
        Assert.assertEquals(state2Name, state2.getName());
        Assert.assertEquals("", state2.getComment());
        Assert.assertNull(state2.getEntryPoint());
        Assert.assertEquals(plan.getId(), state2.getParentPlan().getId());
        Assert.assertEquals(1, state2.getAbstractPlans().size());
        Assert.assertEquals(planType.getId(), state2.getAbstractPlans().get(0).getId());
        Assert.assertEquals(0, state2.getVariableBindings().size());
        Assert.assertEquals(1, state2.getOutTransitions().size());
        Assert.assertTrue(state2.getOutTransitions().contains(transitionState2ToSuccessState));
        Assert.assertEquals(2, state2.getInTransitions().size());
        Assert.assertTrue(state2.getInTransitions().contains(transitionState1ToState2));
        Assert.assertTrue(state2.getInTransitions().contains(transitionState3ToState2));

        // test state3
        // missing: type
        Assert.assertNotNull(state3.getId());
        Assert.assertEquals(state3Name, state3.getName());
        Assert.assertEquals("", state3.getComment());
        Assert.assertNull(state3.getEntryPoint());
        Assert.assertEquals(plan.getId(), state3.getParentPlan().getId());
        Assert.assertEquals(1, state3.getAbstractPlans().size());
        Assert.assertEquals(plan2.getId(), state3.getAbstractPlans().get(0).getId());
        Assert.assertEquals(0, state3.getVariableBindings().size());
        Assert.assertEquals(1, state3.getOutTransitions().size());
        Assert.assertTrue(state3.getOutTransitions().contains(transitionState3ToState2));
        Assert.assertEquals(1, state3.getInTransitions().size());
        Assert.assertTrue(state3.getInTransitions().contains(transitionState1ToState3));

        // test successState
        // missing: type
        Assert.assertNotNull(successState.getId());
        Assert.assertEquals(successStateName, successState.getName());
        Assert.assertEquals("", successState.getComment());
        Assert.assertNull(successState.getEntryPoint());
        Assert.assertEquals(plan.getId(), successState.getParentPlan().getId());
        Assert.assertEquals(0, successState.getAbstractPlans().size());
        Assert.assertEquals(0, successState.getVariableBindings().size());
        Assert.assertEquals(0, successState.getOutTransitions().size());
        Assert.assertEquals(transitionState2ToSuccessState.getId(), successState.getInTransitions().get(0).getId());
        // missing: success
        // missing: postCondition

        // test transitionState1ToState2
        Assert.assertNotNull(transitionState1ToState2.getId());
        Assert.assertEquals("From" + state1.getName() + "To" + state2.getName(), transitionState1ToState2.getName());
        Assert.assertEquals("MISSING_COMMENT", transitionState1ToState2.getComment());
        Assert.assertEquals(state1.getId(), transitionState1ToState2.getInState().getId());
        Assert.assertEquals(state2.getId(), transitionState1ToState2.getOutState().getId());
        Assert.assertEquals(precondition3, transitionState1ToState2.getPreCondition());
        Assert.assertNull(transitionState1ToState2.getSynchronisation());

        // test transitionState1ToState3
        Assert.assertNotNull(transitionState1ToState3.getId());
        Assert.assertEquals("From" + state1.getName() + "To" + state3.getName(), transitionState1ToState3.getName());
        Assert.assertEquals("MISSING_COMMENT", transitionState1ToState3.getComment());
        Assert.assertEquals(state1.getId(), transitionState1ToState3.getInState().getId());
        Assert.assertEquals(state3.getId(), transitionState1ToState3.getOutState().getId());
        Assert.assertEquals(precondition1, transitionState1ToState3.getPreCondition());
        Assert.assertNull(transitionState1ToState3.getSynchronisation());

        // test transitionState2ToSuccessState
        Assert.assertNotNull(transitionState2ToSuccessState.getId());
        Assert.assertEquals("From" + state2.getName() + "To" + successState.getName(), transitionState2ToSuccessState.getName());
        Assert.assertEquals("MISSING_COMMENT", transitionState2ToSuccessState.getComment());
        Assert.assertEquals(state2.getId(), transitionState2ToSuccessState.getInState().getId());
        Assert.assertEquals(successState.getId(), transitionState2ToSuccessState.getOutState().getId());
        Assert.assertEquals(precondition4, transitionState2ToSuccessState.getPreCondition());
        Assert.assertNull(transitionState2ToSuccessState.getSynchronisation());

        // test transitionState3ToState2
        Assert.assertNotNull(transitionState3ToState2.getId());
        Assert.assertEquals("From" + state3.getName() + "To" + state2.getName(), transitionState3ToState2.getName());
        Assert.assertEquals("MISSING_COMMENT", transitionState3ToState2.getComment());
        Assert.assertEquals(state3.getId(), transitionState3ToState2.getInState().getId());
        Assert.assertEquals(state2.getId(), transitionState3ToState2.getOutState().getId());
        Assert.assertEquals(precondition2, transitionState3ToState2.getPreCondition());
        Assert.assertNull(transitionState3ToState2.getSynchronisation());

        // test synchronisations
        Assert.assertEquals(0, plan.getSynchronisations().size());

        // test behaviour
        Assert.assertNotNull(behaviour.getId());
        Assert.assertEquals(behaviourName, behaviour.getName());
        Assert.assertEquals("", behaviour.getComment());
        Assert.assertEquals("", behaviour.getRelativeDirectory());
        Assert.assertEquals(0, behaviour.getVariables().size());
        Assert.assertEquals(0, behaviour.getFrequency());
        Assert.assertEquals(0, behaviour.getDeferring());
        Assert.assertNull(behaviour.getPreCondition());
        Assert.assertNull(behaviour.getRuntimeCondition());
        Assert.assertNull(behaviour.getPostCondition());

        Assert.assertNotNull(behaviourConfiguration.getId());
        Assert.assertEquals("default", behaviourConfiguration.getName());
        Assert.assertEquals("", behaviourConfiguration.getComment());
        Assert.assertEquals("", behaviourConfiguration.getRelativeDirectory());
        Assert.assertEquals(0, behaviourConfiguration.getVariables().size());
        Assert.assertEquals(behaviour.getId(), behaviourConfiguration.getBehaviour().getId());
        Assert.assertEquals(0, behaviourConfiguration.getKeyValuePairs().size());
    }
}
