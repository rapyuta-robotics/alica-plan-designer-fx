package de.uni_kassel.vs.cn.planDesigner.view.editor.tools;

import de.uni_kassel.vs.cn.planDesigner.view.Types;
import de.uni_kassel.vs.cn.planDesigner.view.editor.container.AbstractPlanElementContainer;
import de.uni_kassel.vs.cn.planDesigner.view.editor.tab.planTab.PlanTab;
import de.uni_kassel.vs.cn.planDesigner.view.model.PlanViewModel;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.StackPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


public class EntryPointTool extends AbstractTool {

    private static final Logger LOG = LogManager.getLogger(EntryPointTool.class);
    private Map<EventType, EventHandler> eventHandlerMap = new HashMap<>();
    private boolean initial = true;
    private Node visualRepresentation;

    public EntryPointTool(TabPane workbench, PlanTab planTab) {
        super(workbench, planTab);
    }

    @Override
    protected void initHandlerMap() {

    }

    @Override
    public DraggableHBox createToolUI() {
        DraggableHBox draggableHBox = new DraggableHBox();
        draggableHBox.setIcon(Types.ENTRYPOINT);
        setDraggableHBox(draggableHBox);
        return draggableHBox;
    }

//    @Override
//    public EntryPoint createNewObject() {
//        return getAlicaFactory().createEntryPoint();
//    }
//
//    @Override
//    public void draw() {
//        ((PlanTab) planEditorTabPane.getSelectionModel().getSelectedItem()).getPlanEditorGroup().setupPlanVisualisation();
//    }

    @Override
    protected Map<EventType, EventHandler> getCustomHandlerMap() {
        if (eventHandlerMap.isEmpty()) {
            eventHandlerMap.put(MouseDragEvent.MOUSE_DRAG_OVER, new EventHandler<MouseDragEvent>() {
                @Override
                public void handle(MouseDragEvent event) {
//                    localCoord = null;
//                    if (updateLocalCoords(event)) return;
//
//                    double x = localCoord.getX();
//                    double y = localCoord.getY();
//                    if (initial) {
//                        visualRepresentation.setLayoutX(x);
//                        visualRepresentation.setLayoutY(y);
//                        initial = false;
//                    }
//
//                    visualRepresentation.setTranslateX(x);
//                    visualRepresentation.setTranslateY(y);
//
//                    if (event.getGestureSource() != planEditorTabPane) {
//                        visualRepresentation.setTranslateX(x);
//                        visualRepresentation.setTranslateY(y);
//                    }
//                    System.out.println("X: " + x + " Y: " + y);
//                    event.consume();
                }
            });
            eventHandlerMap.put(MouseDragEvent.MOUSE_DRAG_ENTERED, new EventHandler<MouseDragEvent>() {
                @Override
                public void handle(MouseDragEvent event) {
//                    updateLocalCoords(event);
//                    if (event.getGestureSource() != planEditorTabPane && visualRepresentation == null) {
//                        visualRepresentation = new Circle(localCoord.getX(),localCoord.getY(), 10, new EntryPointContainer().getVisualisationColor());
//                        ((PlanTab) planEditorTabPane.getSelectionModel().getSelectedItem()).getPlanEditorGroup().getChildren().add(visualRepresentation);
//                    }
                    event.consume();
                }
            });

            eventHandlerMap.put(MouseDragEvent.MOUSE_DRAG_EXITED, new EventHandler<MouseDragEvent>() {
                @Override
                public void handle(MouseDragEvent event) {
//                    ((PlanTab) planEditorTabPane.getSelectionModel().getSelectedItem()).getPlanEditorGroup().getChildren().remove(visualRepresentation);
                    visualRepresentation = null;
                }
            });


            eventHandlerMap.put(MouseDragEvent.MOUSE_DRAG_RELEASED, new EventHandler<MouseDragEvent>() {
                @Override
                public void handle(MouseDragEvent event) {
                    endPhase();
                    if (((Node) event.getTarget()).getParent() instanceof AbstractPlanElementContainer == false &&
                            event.getTarget() instanceof StackPane == false) {
                        event.consume();
                        return;
                    }
//                    ((PlanTab) planEditorTabPane.getSelectionModel().getSelectedItem()).getPlanEditorGroup().getChildren().remove(visualRepresentation);
//                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("entryPointCreatorDialog.fxml"));
//                    try {
//                        Parent rootOfDialog = fxmlLoader.load();
//                        EntryPointCreatorDialogController controller = fxmlLoader.getPlanTypeWindowController();
//                        controller.setX((int) localCoord.getX());
//                        controller.setY((int) localCoord.getY());
//                        Stage stage = new Stage();
//                        stage.setResizable(false);
//                        stage.setTitle(I18NRepo.getInstance().getString("label.choose.task"));
//                        stage.setScene(new Scene(rootOfDialog));
//                        stage.initModality(Modality.WINDOW_MODAL);
//                        stage.initOwner(PlanDesigner.getPrimaryStage());
//                        stage.showAndWait();
//                        draw();
//                        initial = true;
//                    } catch (IOException e) {
//                        // if the helper window is not loadable something is really wrong here
//                        LOG.error("Could not loadFromDisk entry point creator dialog. " +
//                                "Your installation of the plan designer is probably broken!");
//                        ErrorWindowController.createErrorWindow("Could not loadFromDisk entry point window", e);
//                    }
//
                }
            });
        }
        return eventHandlerMap;
    }
}
