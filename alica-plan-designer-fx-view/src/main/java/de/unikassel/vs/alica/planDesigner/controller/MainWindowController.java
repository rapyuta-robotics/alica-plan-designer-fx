package de.unikassel.vs.alica.planDesigner.controller;

import de.unikassel.vs.alica.planDesigner.events.GuiEventType;
import de.unikassel.vs.alica.planDesigner.events.GuiModificationEvent;
import de.unikassel.vs.alica.planDesigner.handlerinterfaces.IGuiModificationHandler;
import de.unikassel.vs.alica.planDesigner.handlerinterfaces.IGuiStatusHandler;
import de.unikassel.vs.alica.planDesigner.view.I18NRepo;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.AbstractPlanTab;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.EditorTabPane;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.EditorTab;
import de.unikassel.vs.alica.planDesigner.view.filebrowser.FileTreeView;
import de.unikassel.vs.alica.planDesigner.view.img.AlicaCursor;
import de.unikassel.vs.alica.planDesigner.view.menu.EditMenu;
import de.unikassel.vs.alica.planDesigner.view.menu.FileTreeViewContextMenu;
import de.unikassel.vs.alica.planDesigner.view.model.SerializableViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.ViewModelElement;
import de.unikassel.vs.alica.planDesigner.view.repo.RepositoryTabPane;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    //--------------------------------------------------------------------------------------------
//  SINGLETON INSTANCE
//--------------------------------------------------------------------------------------------
    private static volatile MainWindowController instance;

    public static MainWindowController getInstance() {
        if (instance == null) {
            synchronized (MainWindowController.class) {
                if (instance == null) {
                    instance = new MainWindowController();
                }
            }
        }

        return instance;
    }

    private MainWindowController() {
        this.i18NRepo = I18NRepo.getInstance();
    }

    //--------------------------------------------------------------------------------------------
//  CONSTANTS AND STATICS
//--------------------------------------------------------------------------------------------
    private static final Logger LOG = LogManager.getLogger(MainWindowController.class);
    public static Cursor FORBIDDEN_CURSOR = new AlicaCursor(AlicaCursor.Type.forbidden);

    //--------------------------------------------------------------------------------------------
//  FXML INJECTED
//--------------------------------------------------------------------------------------------
    @FXML
    private FileTreeView fileTreeView;

    @FXML
    private RepositoryTabPane repositoryTabPane;

    @FXML
    private EditorTabPane editorTabPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private Text statusText;

    @FXML
    private HBox statusBox;

    //--------------------------------------------------------------------------------------------
//  FIELDS
//--------------------------------------------------------------------------------------------
    // ---- GUI STUFF ----
    private I18NRepo i18NRepo;
    private Menu fileMenu;
    private Menu codeGenerationMenu;
    private EditMenu editMenu;

    // ---- MODEL STUFF ----
    private String plansPath;
    private String tasksPath;
    private String rolesPath;

    // ---- HANDLE & CONTROLLER ----
    private ConfigurationWindowController configWindowController;
    private AlicaConfWindowController alicaConfWindowController;
    private GlobalsConfWindowController globalsConfWindowController;
    private IGuiStatusHandler guiStatusHandler;
    private IGuiModificationHandler guiModificationHandler;

    //--------------------------------------------------------------------------------------------
//  GETTER & SETTER
//--------------------------------------------------------------------------------------------
    // ---- GETTER ----
    public String getPlansPath() {
        return plansPath;
    }

    public String getTasksPath() {
        return tasksPath;
    }

    public String getRolesPath() {
        return rolesPath;
    }

    public SplitPane getMainSplitPane() {
        return mainSplitPane;
    }

    public FileTreeView getFileTreeView() {
        return fileTreeView;
    }

    public IGuiModificationHandler getGuiModificationHandler() {
        return guiModificationHandler;
    }

    public EditorTabPane getEditorTabPane() {
        return editorTabPane;
    }

    public RepositoryTabPane getRepositoryTabPane() {
        return repositoryTabPane;
    }

    public ConfigurationWindowController getConfigWindowController() {
        return configWindowController;
    }

    public AlicaConfWindowController getAlicaConfWindowController() { return alicaConfWindowController; }

    public GlobalsConfWindowController getGlobalsConfWindowController() {
        return globalsConfWindowController;
    }

    // ---- SETTER ----

    public void setGlobalsConfWindowController(GlobalsConfWindowController globalsConfWindowController) {
        this.globalsConfWindowController = globalsConfWindowController;
    }

    public void setAlicaConfWindowController(AlicaConfWindowController alicaConfWindowController){
        this.alicaConfWindowController = alicaConfWindowController;
    }

    public void setConfigWindowController(ConfigurationWindowController configWindowController) {
        this.configWindowController = configWindowController;
    }

    public void setGuiStatusHandler(IGuiStatusHandler guiStatusHandler) {
        this.guiStatusHandler = guiStatusHandler;
    }

    public void setGuiModificationHandler(IGuiModificationHandler creationHandler) {
        this.guiModificationHandler = creationHandler;
    }


    public Text getStatusText() {
        return statusText;
    }

    //--------------------------------------------------------------------------------------------
//  INTERFACE IMPLEMENTATIONS
//--------------------------------------------------------------------------------------------
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileTreeView.setController(this);

        if (configWindowController == null) {
            throw new RuntimeException("The member configWindowController need to be set through the public setter, before calling initialize()!");
        }

        if (alicaConfWindowController == null) {
            throw new RuntimeException("The member alicaConfWindowController need to be set through the public setter, before calling initialize()!");
        }
        if (globalsConfWindowController == null) {
            throw new RuntimeException("The member globalsConfWindowController need to be set through the public setter, before calling initialize()!");
        }

        // clear
        editorTabPane.getTabs().clear();

        repositoryTabPane.setGuiModificationHandler(guiModificationHandler);
        editorTabPane.setGuiModificationHandler(guiModificationHandler);

        // propertyAndStatusTabPane.init(editorTabPane);

        statusText.setVisible(false);
        menuBar.getMenus().addAll(createMenus());
        guiStatusHandler.handleGuiInitialized();
        setUpCodeGenerationProgressIndicator();

    }

    //--------------------------------------------------------------------------------------------
//  SETUP
//--------------------------------------------------------------------------------------------

    private void setUpCodeGenerationProgressIndicator() {
        double menuItemsWidth = 250;
        statusBox.setLayoutX(menuItemsWidth + 100);
    }

    private List<Menu> createMenus() {
        List<Menu> menus = new ArrayList<>();

        // ---- FILE MENU ----
        fileMenu = new Menu(i18NRepo.getString("label.menu.file"));
        fileMenu.getItems().add(((FileTreeViewContextMenu) fileTreeView.getContextMenu()).getNewResourceMenu());

        // -- SAVE MENU --
        MenuItem saveItem = new MenuItem(i18NRepo.getString("label.menu.file.save"));
        saveItem.setOnAction(event -> {
            if (editorTabPane.getSelectionModel().getSelectedItem() == null) {
                return;
            }
            ((EditorTab) editorTabPane.getSelectionModel().getSelectedItem()).save();
        });
        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        fileMenu.getItems().add(saveItem);
        fileMenu.setDisable(true);
        menus.add(fileMenu);

        // ---- EDIT MENU ----
        editMenu = new EditMenu(fileTreeView, editorTabPane, repositoryTabPane, configWindowController, alicaConfWindowController, globalsConfWindowController);
        editMenu.setId("editMenu");
        editMenu.setGuiModificationHandler(this.guiModificationHandler);
        menus.add(editMenu);

        // ---- CODE GENERATION MENU ----
        codeGenerationMenu = new Menu(i18NRepo.getString("label.menu.generation"));
        codeGenerationMenu.setId("codeGenerationMenu");
        MenuItem generateItem = new MenuItem(i18NRepo.getString("label.menu.generation.generate"));
        generateItem.setId("generateItem");
        MenuItem generateCurrentFile = new MenuItem(i18NRepo.getString("label.menu.generation.file"));
        generateCurrentFile.setDisable(true);
        editorTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                if (newValue != null) {
                    String type = ((EditorTab) newValue).getSerializableViewModel().getType();
                    if (type.equals(Types.BEHAVIOUR) ||
                            type.equals(Types.PLAN) ||
                            type.equals(Types.MASTERPLAN) ||
                            type.equals(Types.PLANTYPE) ||
                            type.equals(Types.TASKREPOSITORY)) {
                        generateCurrentFile.setDisable(false);
                    }
                } else {
                    generateCurrentFile.setDisable(true);
                }
            }
        });

        generateCurrentFile.setOnAction(e -> {
            ViewModelElement modelElement = ((AbstractPlanTab) editorTabPane
                    .getSelectionModel().getSelectedItem()).getSerializableViewModel();
            try {
                GuiModificationEvent event = new GuiModificationEvent(GuiEventType.GENERATE_ELEMENT, modelElement.getType(), modelElement.getName());
                event.setElementId(modelElement.getId());
                waitOnProgressLabel(() -> this.guiModificationHandler.generateCode(event, statusText));
            } catch (RuntimeException ex) {
                LOG.error("MainWindowController: Error while generating code", ex);
                ErrorWindowController.createErrorWindow(i18NRepo.getString("label.error.codegen"), null);
            }
        });
        generateItem.setOnAction(e -> {
            try {
                GuiModificationEvent event = new GuiModificationEvent(GuiEventType.GENERATE_ALL_ELEMENTS, "", "");
                waitOnProgressLabel(() -> this.guiModificationHandler.generateCode(event, statusText));
            } catch (RuntimeException ex) {
                LOG.error("MainWindowController: Error while generating code for all elements", ex);
                ErrorWindowController.createErrorWindow(i18NRepo.getString("label.error.codegen"), null);
            }

        });

        codeGenerationMenu.getItems().addAll(generateCurrentFile, generateItem);
        menus.add(codeGenerationMenu);

        return menus;
    }

    public void setUpFileTreeView(String plansPath, String rolesPath, String tasksPath) {
        fileTreeView.getRoot().getChildren().clear();
        this.plansPath = plansPath;
        fileTreeView.setupPlansPath(this.plansPath);
        this.rolesPath = rolesPath;
        fileTreeView.setupRolesPath(this.rolesPath);
        this.tasksPath = tasksPath;
        fileTreeView.setupTaskPath(this.tasksPath);
    }

//--------------------------------------------------------------------------------------------
//  MENU STUFF
//--------------------------------------------------------------------------------------------

    public void enableMenuBar() {
        codeGenerationMenu.setDisable(false);
        fileMenu.setDisable(false);
    }

    public void setDeleteDisabled(boolean disabled) {
        editMenu.setDeleteItemDisabled(disabled);
    }

    public void disableRedo(boolean disable) {
        this.editMenu.setRedoItemDisabled(disable);
    }

    public void disableUndo(boolean disable) {
        this.editMenu.setUndoDisabled(disable);
    }

    /**
     * delegate to { EditorTabPane#openTab(java.nio.file.Path)}
     *
     * @param toOpen file that should be opened
     */
    public void openFile(SerializableViewModel toOpen) {
        editorTabPane.openTab(toOpen);
    }

    public void waitOnProgressLabel(Runnable toWaitOn) {
        new Thread(() -> {
            // Ping
            Platform.runLater(() -> statusText.setVisible(true));
            // Run generation
            toWaitOn.run();
            // Show Message
            Platform.runLater(() -> statusText.setVisible(false));
        }).start();
    }

    //	public void closeTabIfOpen (long modelElementId) {
    //        Optional<AbstractPlanTab> tabOptional = editorTabPane
    //                .getTabs()
    //                .stream()
    //                .map(e -> (AbstractPlanTab) e)
    //                .filter(e -> e.getEditable().equals(modelElementId))
    //                .findFirst();
    //        tabOptional.ifPresent(abstractEditorTab -> editorTabPane.getTabs().remove(abstractEditorTab));
    //    }

    //    public void closePropertyAndStatusTabIfOpen() {
    //        if(propertyAndStatusTabPane != null) {
    //            propertyAndStatusTabPane.getTabs().clear();
    //        }
    //    }


}