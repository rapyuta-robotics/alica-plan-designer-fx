package de.unikassel.vs.alica.planDesigner.view.editor.tab.planTypeTab;

import de.unikassel.vs.alica.planDesigner.controller.ErrorWindowController;
import de.unikassel.vs.alica.planDesigner.events.GuiChangeAttributeEvent;
import de.unikassel.vs.alica.planDesigner.events.GuiEventType;
import de.unikassel.vs.alica.planDesigner.events.GuiModificationEvent;
import de.unikassel.vs.alica.planDesigner.view.Types;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.AbstractPlanTab;
import de.unikassel.vs.alica.planDesigner.view.editor.tab.EditorTabPane;
import de.unikassel.vs.alica.planDesigner.view.img.AlicaIcon;
import de.unikassel.vs.alica.planDesigner.view.model.*;
import de.unikassel.vs.alica.planDesigner.view.repo.RepositoryLabel;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.Comparator;

public class PlanTypeTab extends AbstractPlanTab {

    private Button removePlanButton;
    private Button addPlanButton;
    private Button removeAllPlansButton;
    private Button saveButton;

    private TableView<AnnotatedPlanViewModel> planTypeTableView;
    private ListView<RepositoryLabel> planListView;

    private Comparator<RepositoryLabel> repositoryHBoxComparator;
    private Comparator<ViewModelElement> viewModelElementComparator;

    public PlanTypeTab(SerializableViewModel planType, EditorTabPane editorTabPane) {
        super(planType, editorTabPane.getGuiModificationHandler());

        draw();
    }

    private void draw() {
        // instantiate gui objects
        this.repositoryHBoxComparator = Comparator.comparing(planRepositoryLabel -> !planRepositoryLabel.getViewModelType().equals(Types.MASTERPLAN));
        this.repositoryHBoxComparator = repositoryHBoxComparator.thenComparing(planRepositoryLabel -> planRepositoryLabel.getViewModelName());
        this.viewModelElementComparator = Comparator.comparing(annotatedPlan -> !annotatedPlan.getType().equals(Types.MASTERPLAN));
        this.viewModelElementComparator = viewModelElementComparator.thenComparing(annotatedPlan -> annotatedPlan.getName());

        removePlanButton = new Button(i18NRepo.getString("label.plantype.removePlan"));
        removeAllPlansButton = new Button(i18NRepo.getString("label.plantype.removeAllPlans"));
        addPlanButton = new Button(i18NRepo.getString("label.plantype.addPlan"));
        saveButton = new Button(i18NRepo.getString("action.save"));
        this.planTypeTableView = new TableView<>();
        this.planListView = new ListView<>();

        // initialize gui objects
        initButtons();
        initPlansInPlanTypeTable();
        initAllPlansListView();

        Button[] buttons = { addPlanButton, removePlanButton, removeAllPlansButton, saveButton};

        VBox buttonsVBox = new VBox(buttons);
        VBox.setVgrow(buttonsVBox, Priority.ALWAYS);

        buttonsVBox.setSpacing(20.0);
        buttonsVBox.setPadding(new Insets(5,15,5,15));
        buttonsVBox.setAlignment(Pos.CENTER);

        for(Button btn : buttons) {
            btn.setPrefSize(130, 30);
            btn.setMnemonicParsing(false);
        }

        HBox tablesAndButtonsHBox = new HBox(planListView, buttonsVBox, planTypeTableView);

        VBox.setVgrow(addPlanButton, Priority.ALWAYS);
        VBox.setVgrow(removePlanButton, Priority.ALWAYS);
        VBox.setVgrow(removeAllPlansButton, Priority.ALWAYS);
        VBox.setVgrow(saveButton, Priority.ALWAYS);
        VBox.setVgrow(tablesAndButtonsHBox, Priority.ALWAYS);

        HBox.setHgrow(tablesAndButtonsHBox, Priority.ALWAYS);
        HBox.setHgrow(planTypeTableView, Priority.ALWAYS);
        HBox.setHgrow(planListView, Priority.ALWAYS);

        tablesAndButtonsHBox.setSpacing(15.0);
        tablesAndButtonsHBox.setAlignment(Pos.CENTER);

        planListView.setPrefHeight(200.0);
        planListView.setPrefWidth(200.0);
        splitPane.getItems().add(0, tablesAndButtonsHBox);
    }

    protected void initButtons() {
        // init button text

        // init button callbacks
        saveButton.setOnAction(e -> {
            if (isDirty()) {
                fireModificationEvent(GuiEventType.SAVE_ELEMENT, serializableViewModel);
            }
        });

        addPlanButton.setOnAction(e -> {
            if (!planListView.getSelectionModel().isEmpty()) {
                try {
                    fireModificationEvent(GuiEventType.ADD_ELEMENT, planListView.getSelectionModel().getSelectedItem().getViewModelElement());
                }catch (RuntimeException excp){
                    // Exception might be thrown, because the selected element can't be added, because this would cause
                    // a loop in the model
                    ErrorWindowController.createErrorWindow(excp.getMessage(), null);
                }
            }
        });

        removePlanButton.setOnAction(e -> {
            if (!planTypeTableView.getSelectionModel().isEmpty()) {
                fireModificationEvent(GuiEventType.REMOVE_ELEMENT, planTypeTableView.getSelectionModel().getSelectedItem());
            }
        });

        removeAllPlansButton.setOnAction(e -> {
            if(planTypeTableView.getItems().size() > 0) {
                fireModificationEvent(GuiEventType.REMOVE_ALL_ELEMENTS, planTypeTableView.getItems().get(0));
            }
            e.consume();
        });
    }

    private void initPlansInPlanTypeTable() {
        PlanTypeViewModel planTypeViewModel = (PlanTypeViewModel) serializableViewModel;
        planTypeTableView.setItems(planTypeViewModel.getPlansInPlanType());
        planTypeTableView.getItems().sort(viewModelElementComparator);
        planTypeViewModel.getPlansInPlanType().addListener(new ListChangeListener<AnnotatedPlanViewModel>() {
            @Override
            public void onChanged(Change<? extends AnnotatedPlanViewModel> c) {
                c.next();
                if (c.wasAdded()) {
                    for (AnnotatedPlanViewModel element : c.getAddedSubList()) {
                        planTypeViewModel.removePlanFromAllPlans(element.getPlanId());
                    }
                } else if (c.wasRemoved()) {
                    for (AnnotatedPlanViewModel element : c.getRemoved()) {
                        planTypeViewModel.addPlanToAllPlans((PlanViewModel) guiModificationHandler.getViewModelElement(element.getPlanId()));
                        for (AnnotatedPlanViewModel view : planTypeTableView.getItems()) {
                            if (view.getId() == element.getId()) {
                                planTypeTableView.getItems().remove(view);
                                break;
                            }
                        }
                    }
                }
                planTypeTableView.refresh();
            }
        });

        planTypeTableView.getColumns().add(createActiveColumn());
        planTypeTableView.getColumns().add(createNameColumn());

        //set auto columns size
        planTypeTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        planTypeTableView.setRowFactory(tv -> {
            TableRow<AnnotatedPlanViewModel> annotatedPlanTableRow = new TableRow<>();
            annotatedPlanTableRow.setOnMouseClicked(e -> {
                AnnotatedPlanViewModel item = annotatedPlanTableRow.getItem();
                if (e.getClickCount() == 2 && item != null) {
                    item.setActivated(!item.isActivated());
                    planTypeTableView.refresh();
                    GuiChangeAttributeEvent event = new GuiChangeAttributeEvent(GuiEventType.CHANGE_ELEMENT, Types.ANNOTATEDPLAN, item.getName());
                    event.setParentId(serializableViewModel.getId());
                    event.setElementId(item.getId());
                    event.setAttributeName("activated");
                    event.setAttributeType(Boolean.class.getSimpleName());
                    event.setNewValue(String.valueOf(item.isActivated()));
                    guiModificationHandler.handle(event);
                }
            });
            return annotatedPlanTableRow;
        });
    }

    private void initAllPlansListView() {
        PlanTypeViewModel planTypeViewModel = (PlanTypeViewModel) serializableViewModel;
        for (ViewModelElement plan : planTypeViewModel.getAllPlans()) {
            if (planTypeViewModel.containsPlan(plan.getId())) {
                continue;
            }
            RepositoryLabel planRepositoryLabel = new RepositoryLabel(plan, guiModificationHandler);
            planListView.getItems().add(planRepositoryLabel);
            planListView.getItems().sort(repositoryHBoxComparator);
        }
        planTypeViewModel.getAllPlans().addListener(new ListChangeListener<ViewModelElement>() {
            @Override
            public void onChanged(Change<? extends ViewModelElement> c) {
                c.next();
                if (c.wasAdded()) {
                    for (ViewModelElement element : c.getAddedSubList()) {
                        if (planTypeViewModel.containsPlan(element.getId())) {
                            continue;
                        }

                        RepositoryLabel planRepositoryLabel = new RepositoryLabel(element, guiModificationHandler);
                        Platform.runLater(() -> {
                            planListView.getItems().add(planRepositoryLabel);
                            planListView.getItems().sort(repositoryHBoxComparator);
                        });
                    }
                } else if (c.wasRemoved()) {
                    for (ViewModelElement element : c.getRemoved()) {
                        for (RepositoryLabel plan : planListView.getItems()) {
                            if (plan.getViewModelId() == element.getId()) {
                                Platform.runLater(() -> planListView.getItems().remove(plan));
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private TableColumn<AnnotatedPlanViewModel, String> createNameColumn() {
        TableColumn<AnnotatedPlanViewModel, String> planNameColumn = new TableColumn<>(i18NRepo.getString("label.column.planName"));
        planNameColumn.setMinWidth(120);
        planNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        planNameColumn.setCellFactory(new Callback<TableColumn<AnnotatedPlanViewModel, String>, TableCell<AnnotatedPlanViewModel, String>>() {
            @Override
            public TableCell<AnnotatedPlanViewModel, String> call(TableColumn<AnnotatedPlanViewModel, String> param) {
                TableCell<AnnotatedPlanViewModel, String> planNameTableCell = new TableCell<AnnotatedPlanViewModel, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty == false) {
                            setText(item);
                        }
                    }
                };
                return planNameTableCell;
            }
        });
        return planNameColumn;
    }

    private TableColumn<AnnotatedPlanViewModel, Boolean> createActiveColumn() {
        TableColumn<AnnotatedPlanViewModel, Boolean> activeColumn = new TableColumn<>(i18NRepo.getString("label.column.active"));
        activeColumn.setMaxWidth(2500);
        activeColumn.setCellValueFactory(new PropertyValueFactory<>(i18NRepo.getString("label.column.activated")));
        activeColumn.setCellFactory(new Callback<TableColumn<AnnotatedPlanViewModel, Boolean>, TableCell<AnnotatedPlanViewModel, Boolean>>() {
            @Override
            public TableCell<AnnotatedPlanViewModel, Boolean> call(TableColumn<AnnotatedPlanViewModel, Boolean> param) {
                TableCell<AnnotatedPlanViewModel, Boolean> annotatedPlanBooleanTableCell = new TableCell<AnnotatedPlanViewModel, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty == false) {
                            if (Boolean.TRUE.equals(item)) {
                                setGraphic(new ImageView(new AlicaIcon(Types.SUCCESSSTATE, AlicaIcon.Size.SMALL)));
                            } else {
                                setGraphic(new ImageView(new AlicaIcon(Types.FAILURESTATE, AlicaIcon.Size.SMALL)));
                            }
                            setText("");
                        }
                    }
                };
                annotatedPlanBooleanTableCell.setStyle("-fx-alignment: CENTER;");
                return annotatedPlanBooleanTableCell;
            }
        });
        return activeColumn;
    }

    /**
     * Called for adding, removing plans from plantype...
     * @param type
     * @param element
     */
    private void fireModificationEvent(GuiEventType type, ViewModelElement element) {
        if (element == null) {
            return;
        }
        GuiModificationEvent event = new GuiModificationEvent(type, element.getType(), element.getName());
        event.setElementId(element.getId());
        event.setParentId(this.serializableViewModel.getId());
        guiModificationHandler.handle(event);
    }

    @Override
    public GuiModificationEvent handleDelete() {
        System.err.println("PlanTypeTab: Not implemented!");
        return null;
    }

    @Override
    public void save() {
        if (isDirty()) {
            GuiModificationEvent event = new GuiModificationEvent(GuiEventType.SAVE_ELEMENT, Types.PLANTYPE, serializableViewModel.getName());
            event.setElementId(serializableViewModel.getId());
            guiModificationHandler.handle(event);
        }
    }
}
