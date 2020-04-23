package de.unikassel.vs.alica.planDesigner.view.repo;

import de.unikassel.vs.alica.planDesigner.controller.ErrorWindowController;
import de.unikassel.vs.alica.planDesigner.controller.MainWindowController;
import de.unikassel.vs.alica.planDesigner.events.GuiEventType;
import de.unikassel.vs.alica.planDesigner.events.GuiModificationEvent;
import de.unikassel.vs.alica.planDesigner.handlerinterfaces.IGuiModificationHandler;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.editor.container.*;
import de.unikassel.vs.alica.planDesigner.view.img.AlicaCursor;
import de.unikassel.vs.alica.planDesigner.view.img.AlicaIcon;
import de.unikassel.vs.alica.planDesigner.view.menu.DeleteElementMenuItem;
import de.unikassel.vs.alica.planDesigner.view.menu.RenameElementMenuItem;
import de.unikassel.vs.alica.planDesigner.view.menu.ShowUsagesMenuItem;
import de.unikassel.vs.alica.planDesigner.view.model.*;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.PickResult;
import javafx.scene.shape.Circle;

public class RepositoryLabel extends Label {

    protected IGuiModificationHandler guiModificationHandler;
    protected ViewModelElement viewModelElement;

    public RepositoryLabel(ViewModelElement viewModelElement, IGuiModificationHandler guiModificationHandler) {
        this.guiModificationHandler = guiModificationHandler;
        this.viewModelElement = viewModelElement;
        setGraphic(this.viewModelElement.getType());
        setText(this.viewModelElement.getName());

        this.viewModelElement.nameProperty().addListener((observable, oldValue, newValue) -> {
            setText(newValue);
        });

        this.viewModelElement.typeProperty().addListener((observable, oldValue, newValue) -> {
            setGraphic(newValue);
        });

        // right click for opening context menu with option to show usage of model element
        setOnContextMenuRequested(e -> {
            RenameElementMenuItem renameFileMenuItem = new RenameElementMenuItem(this.viewModelElement, guiModificationHandler);
            ShowUsagesMenuItem usageMenu = new ShowUsagesMenuItem(this.viewModelElement, guiModificationHandler);
            DeleteElementMenuItem deleteMenu = new DeleteElementMenuItem(this.viewModelElement, guiModificationHandler);
            ContextMenu contextMenu = new ContextMenu(renameFileMenuItem, usageMenu, deleteMenu);
            contextMenu.show(RepositoryLabel.this, e.getScreenX(), e.getScreenY());
            e.consume();
        });

        // double click for open the corresponding file
        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) {
                if (viewModelElement instanceof SerializableViewModel) {
                    MainWindowController.getInstance().openFile((SerializableViewModel) viewModelElement);
                } else if (viewModelElement instanceof TaskViewModel) {
                    TaskViewModel taskViewModel = (TaskViewModel) viewModelElement;
                    MainWindowController.getInstance().openFile(taskViewModel.getTaskRepositoryViewModel());
                } else {
                    throw new RuntimeException("RepositoryLabel: Unknown ViewModelElement type " + viewModelElement.getType() + " for opening tab!");
                }
                e.consume();
            }
        });

        // set the onDragObjectImage to cursor
        setOnDragDetected(e -> {
//            System.out.println("RepositoryLabel: Drag Started Source: " + e.getSource() + " " + " Target: " + e.getTarget());
            getScene().setCursor(new AlicaCursor(viewModelElement.getType(), AlicaIcon.Size.SMALL,8,8));
            e.consume();
        });

        //Drag from RepositoryList to add a AbstractPlan to State or Task to EntryPoint
        setOnMouseReleased(e -> {
//            System.out.println("RepositoryLabel: Drag Release Source: " + e.getSource() + " " + " Target: " + e.getTarget());
            getScene().setCursor(Cursor.DEFAULT);
            PickResult pickResult = e.getPickResult();
            Node pickedElement = pickResult.getIntersectedNode();
            while (!(pickedElement instanceof StateContainer
                    || pickedElement instanceof EntryPointContainer
                    || pickedElement instanceof ConfAbstractPlanWrapperContainer)
                    && pickedElement.getParent() != null) {
                pickedElement = pickedElement.getParent();
            }

            if (!(pickedElement instanceof Container)
                    // It is not allowed to put anything into terminal states.
                    || pickedElement instanceof FailureStateContainer
                    || pickedElement instanceof SuccessStateContainer) {
                return;
            }

            try {
                if (pickedElement instanceof StateContainer && viewModelElement instanceof AbstractPlanViewModel) {
                    StateContainer stateContainer = (StateContainer) pickedElement;
                    GuiModificationEvent guiModificationEvent = new GuiModificationEvent(GuiEventType.ADD_ELEMENT, viewModelElement.getType(), viewModelElement.getName());
                    guiModificationEvent.setElementId(viewModelElement.getId());
                    guiModificationEvent.setParentId(stateContainer.getState().getId());
                    guiModificationHandler.handle(guiModificationEvent);
                } else if (pickedElement instanceof EntryPointContainer && viewModelElement instanceof TaskViewModel) {
                    EntryPointContainer entryPointContainer = (EntryPointContainer) pickedElement;
                    GuiModificationEvent guiModificationEvent = new GuiModificationEvent(GuiEventType.ADD_ELEMENT, viewModelElement.getType(), viewModelElement.getName());
                    guiModificationEvent.setParentId(entryPointContainer.getPlanElementViewModel().getId());
                    guiModificationEvent.setElementId(viewModelElement.getId());
                    guiModificationHandler.handle(guiModificationEvent);
                } else if (pickedElement instanceof ConfAbstractPlanWrapperContainer && viewModelElement instanceof ConfigurationViewModel) {
                    ConfAbstractPlanWrapperContainer confAbstractPlanWrapperContainer = (ConfAbstractPlanWrapperContainer) pickedElement;
                    GuiModificationEvent guiModificationEvent = new GuiModificationEvent(GuiEventType.ADD_ELEMENT, viewModelElement.getType(), viewModelElement.getName());
                    guiModificationEvent.setParentId(confAbstractPlanWrapperContainer.getPlanElementViewModel().getId());
                    guiModificationEvent.setElementId(viewModelElement.getId());
                    guiModificationHandler.handle(guiModificationEvent);
                }
            } catch (RuntimeException excp) {
                // Exception might get thrown, because the element can't be added, because this would cause a loop
                // in the model
                ErrorWindowController.createErrorWindow(excp.getMessage(), null);
            }
            e.consume();
        });
    }

    public void setGraphic(String iconName) {
        this.setGraphic(new ImageView(new AlicaIcon(iconName, AlicaIcon.Size.SMALL)));
    }

    public String getViewModelType() {
        return viewModelElement.getType();
    }

    public long getViewModelId() {
        return viewModelElement.getId();
    }

    public ViewModelElement getViewModelElement() {
        return viewModelElement;
    }

    public String getViewModelName() {
        return viewModelElement.getName();
    }
}