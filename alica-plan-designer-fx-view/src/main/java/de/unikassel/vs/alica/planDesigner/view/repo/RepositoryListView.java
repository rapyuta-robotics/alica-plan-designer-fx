package de.unikassel.vs.alica.planDesigner.view.repo;

import de.unikassel.vs.alica.planDesigner.handlerinterfaces.IGuiModificationHandler;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.img.AlicaCursor;
import de.unikassel.vs.alica.planDesigner.view.img.AlicaIcon;
import de.unikassel.vs.alica.planDesigner.view.model.ViewModelElement;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class RepositoryListView extends ListView<RepositoryLabel> {

    protected Comparator<RepositoryLabel> modelElementComparator;
    protected IGuiModificationHandler guiModificationHandler;

    public RepositoryListView() {
        super();
        setPrefHeight(getItems().size() * 24 + 2);

        modelElementComparator = Comparator.comparing(o -> !o.getViewModelType().equals(Types.MASTERPLAN));
        modelElementComparator = modelElementComparator.thenComparing(o -> o.getViewModelName());

        // forward of event to RepositoryLabel which comprises the actual logic
        setOnDragDetected(e -> {
            getSelectionModel().getSelectedItem().getOnDragDetected().handle(e);
        });
        setOnMouseReleased(e -> {
            getSelectionModel().getSelectedItem().getOnMouseReleased().handle(e);
        });
    }

    public void setGuiModificationHandler(IGuiModificationHandler guiModificationHandler) {
        this.guiModificationHandler = guiModificationHandler;
    }

    public void removeElement(ViewModelElement viewModel) {
        Iterator<RepositoryLabel> iter = getItems().iterator();
        while (iter.hasNext()) {
            RepositoryLabel repositoryLabel = iter.next();
            if (repositoryLabel.getViewModelId() == viewModel.getId()) {
                iter.remove();
            }
        }
        setPrefHeight(getItems().size() * 24 + 2);
        sort();
    }


    public void addElement(ViewModelElement viewModelElement) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getItems().add(new RepositoryLabel(viewModelElement, guiModificationHandler));
                setPrefHeight(getItems().size() * 24 + 2);
                sort();
            }
        });
    }

    public void addElements(List<? extends ViewModelElement> viewModelElements) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < viewModelElements.size(); i++) {
                    ViewModelElement viewModelElement = viewModelElements.get(i);
                    getItems().add(new RepositoryLabel(viewModelElement, guiModificationHandler));
                }
                setPrefHeight(getItems().size() * 24 + 2);
                sort();
            }
        });
    }

    public void clearGuiContent() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getItems().clear();
                setPrefHeight(getItems().size() * 24 + 2);
            }
        });
    }

    public ViewModelElement getSelectedItem() {
        RepositoryLabel repositoryLabel = getSelectionModel().getSelectedItem();
        if (repositoryLabel != null) {
            return repositoryLabel.getViewModelElement();
        } else {
            return null;
        }
    }

    protected void sort() {
        getItems().sort(modelElementComparator);
    }
}
