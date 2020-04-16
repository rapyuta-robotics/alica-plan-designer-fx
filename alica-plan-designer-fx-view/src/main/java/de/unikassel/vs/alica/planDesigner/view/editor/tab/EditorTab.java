package de.unikassel.vs.alica.planDesigner.view.editor.tab;

import de.unikassel.vs.alica.planDesigner.controller.IsDirtyWindowController;
import de.unikassel.vs.alica.planDesigner.events.GuiEventType;
import de.unikassel.vs.alica.planDesigner.events.GuiModificationEvent;
import de.unikassel.vs.alica.planDesigner.handlerinterfaces.IGuiModificationHandler;
import de.unikassel.vs.alica.planDesigner.view.I18NRepo;
import de.unikassel.vs.alica.planDesigner.view.img.AlicaIcon;
import de.unikassel.vs.alica.planDesigner.view.model.SerializableViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.ViewModelElement;
import de.unikassel.vs.alica.planDesigner.view.properties.ElementInformationPane;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;

public abstract class EditorTab extends Tab {

    protected I18NRepo i18NRepo;

    protected IGuiModificationHandler guiModificationHandler;
    protected SerializableViewModel serializableViewModel;

    protected ElementInformationPane elementInformationPane;
    protected SplitPane splitPane;

    public EditorTab (SerializableViewModel serializableViewModel, IGuiModificationHandler handler) {
        super(serializableViewModel.getName());

        this.serializableViewModel = serializableViewModel;
        this. guiModificationHandler = handler;
        this.i18NRepo = I18NRepo.getInstance();

        // name updates: things can be renamed
        serializableViewModel.nameProperty().addListener((observable, oldValue, newValue) -> {
            if (!getText().contains("*")) {
                this.setText(newValue + "*");
            } else {
                this.setText(newValue);
            }
        });

        // icon updates: The type might change later (Plan -> MasterPlan)
        setGraphic(new ImageView(new AlicaIcon(serializableViewModel.getType(), AlicaIcon.Size.SMALL)));
        serializableViewModel.typeProperty().addListener((observable, oldValue, newValue) ->
                setGraphic(new ImageView(new AlicaIcon(newValue, AlicaIcon.Size.SMALL))));

        // dirty updates:
        if(isDirty()) { // The opened SerializableViewModel may be dirty already
            this.setText(getText() + "*");
        }
        serializableViewModel.dirtyProperty().addListener((observable, oldValue, newValue) -> {
            if (!getText().contains("*") && newValue) {
                this.setText(getText() + "*");
            } else if (getText().contains("*") && !newValue) {
                this.setText(getText().substring(0, getText().length()-1));
            }
        });

        setClosable(true);
        setOnCloseRequest(e -> {
            // popup for trying to close dirty tab
            if (serializableViewModel.isDirty()) {
                IsDirtyWindowController.createIsDirtyWindow(this, e);
            }
        });

        elementInformationPane = new ElementInformationPane(guiModificationHandler);
        elementInformationPane.setViewModelElement(serializableViewModel);
        elementInformationPane.setMaxHeight(20000);

        this.splitPane = new SplitPane(elementInformationPane);
        this.splitPane.setOrientation(Orientation.VERTICAL);

        setContent(splitPane);
    }

    public SerializableViewModel getSerializableViewModel() {
        return serializableViewModel;
    }

    public boolean representsViewModelElement(ViewModelElement viewModelElement) {
        return this.serializableViewModel.equals(viewModelElement) || this.serializableViewModel.getId() == viewModelElement.getParentId();
    }

    public boolean isDirty() {return serializableViewModel.isDirty();}

    public void revertChanges() {
        GuiModificationEvent event = new GuiModificationEvent(GuiEventType.RELOAD_ELEMENT, serializableViewModel.getType(), serializableViewModel.getName());
        event.setElementId(serializableViewModel.getId());
        guiModificationHandler.handle(event);
    }

    public void save(String type) {
        if (isDirty()) {
            GuiModificationEvent event = new GuiModificationEvent(GuiEventType.SAVE_ELEMENT, type, serializableViewModel.getName());
            event.setElementId(serializableViewModel.getId());
            guiModificationHandler.handle(event);
        }
    }

    public abstract void save();
    public abstract GuiModificationEvent handleDelete();
}
