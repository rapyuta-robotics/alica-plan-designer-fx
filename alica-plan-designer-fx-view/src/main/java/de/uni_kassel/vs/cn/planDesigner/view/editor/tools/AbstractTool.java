package de.uni_kassel.vs.cn.planDesigner.view.editor.tools;

import de.uni_kassel.vs.cn.planDesigner.controller.MainWindowController;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@link AbstractTool} interface provides methods for the tools in the {@link PLDToolBar}.
 * It helps to generalize the usage of these tools for the following workflow:
 * tool is selected (start of the phase) -> Event handler for special actions on the {@link PlanEditorGroup}
 * are registered -> The actions are performed. A new alicamodel object is created.
 * Or the actions are aborted. -> The phase ends. The event handlers will be removed. and the editor is usable as before.
 */
public abstract class AbstractTool {
    protected TabPane planEditorTabPane;
    // Contains Icon and Text and triggers the drag events (start and stop).
    protected DraggableHBox draggableHBox;
    // Shadow Effect set on draggableHBox when dragged
    protected static final DropShadow dropShadowEffect = new DropShadow(10, Color.GREY);
    protected HashMap<EventType, EventHandler> defaultHandlerMap;
    protected HashMap<EventType, EventHandler> customHandlerMap;
    protected Cursor previousCursor;


    private boolean recentlyDone;
    private EventHandler<? super ScrollEvent> onScrollInPlanTab;
    private ScrollPane.ScrollBarPolicy vBarPolicy;
    private ScrollPane.ScrollBarPolicy hBarPolicy;
    private double vmax;
    private double hmax;

    public AbstractTool(TabPane planEditorTabPane) {
        this.planEditorTabPane = planEditorTabPane;
        this.dropShadowEffect.setSpread(0.5);

        // should be done in the derived classes
        this.draggableHBox = new DraggableHBox();
        this.draggableHBox.setOnDragDetected(event -> {
            this.draggableHBox.startFullDrag();
            this.startPhase();
            event.consume();
        });
        this.draggableHBox.setOnDragDone(Event::consume);
    }

    protected abstract void initHandlerMap();

    protected Node getPlanEditorTabPane() {
        return planEditorTabPane;
    }

    protected Map<EventType, EventHandler> defaultHandlers() {
        if (defaultHandlerMap == null) {
            defaultHandlerMap = new HashMap<>();
            // The tool phase is ended, when the courser leaves the scene.
            defaultHandlerMap.put(MouseEvent.MOUSE_DRAGGED, (event) -> {
                MouseEvent e = (MouseEvent) event;
                if (e.getSceneX() + 5 > getPlanEditorTabPane().getScene().getWidth()
                        || e.getSceneY() + 5 > getPlanEditorTabPane().getScene().getHeight()
                        || e.getSceneX() - 5 < 0 || e.getSceneY() - 5 < 0) {
                    endPhase();
                }
            });
        }
        return defaultHandlerMap;
    }

    protected Map<EventType, EventHandler> getCustomHandlerMap() {
        if (customHandlerMap.isEmpty()) {
            this.initHandlerMap();
        }
        return customHandlerMap;
    }

    public void startPhase() {
        draggableHBox.setEffect(dropShadowEffect);
        getCustomHandlerMap()
                .entrySet()
                .forEach(entry -> planEditorTabPane.getScene().addEventFilter(entry.getKey(), entry.getValue()));
        defaultHandlers()
                .entrySet()
                .forEach(entry -> planEditorTabPane.getScene().addEventFilter(entry.getKey(), entry.getValue()));

        // deactivate scrolling, fixes scrolling to infinity when handling a tool
        if (planEditorTabPane.getSelectionModel().getSelectedItem() instanceof PlanTab) {
            PlanTab planTab = (PlanTab) planEditorTabPane.getSelectionModel().getSelectedItem();
            onScrollInPlanTab = planTab.getScrollPane().getOnScroll();
            vBarPolicy = planTab.getScrollPane().getHbarPolicy();
            hBarPolicy = planTab.getScrollPane().getVbarPolicy();
            planTab.getScrollPane().setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            planTab.getScrollPane().setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            vmax = planTab.getSelectedItem()).getScrollPane().getVmax();
            hmax = planTab.getScrollPane().getHmax();

            planTab.getScrollPane().setVmax(0);
            planTab.getScrollPane().setHmax(0);
            planTab.getScrollPane().setOnScroll(Event::consume);
            planTab.getPlanEditorGroup().setAutoSizeChildren(false);
            planTab.getPlanEditorGroup().setManaged(false);
        }

        previousCursor = planEditorTabPane.getScene().getCursor();
        // TODO: should be done in the derived tool classes' start phase methods
        //planEditorTabPane.getScene().setCursor(new ImageCursor(new AlicaIcon("special type of abstract tool")));
    }

    public void endPhase() {
        draggableHBox.setEffect(null);
        getCustomHandlerMap()
                .entrySet()
                .forEach(entry -> getPlanEditorTabPane().getScene().removeEventFilter(entry.getKey(), entry.getValue()));
        defaultHandlers()
                .entrySet()
                .forEach(entry -> getPlanEditorTabPane().getScene().removeEventFilter(entry.getKey(), entry.getValue()));
        if (planEditorTabPane.getSelectionModel().getSelectedItem() instanceof PlanTab) {
            // reactivate scrolling
            PlanTab planTab = (PlanTab) planEditorTabPane.getSelectionModel().getSelectedItem();
            planTab.getScrollPane().setOnScroll(onScrollInPlanTab);
            planTab.getScrollPane().setVbarPolicy(vBarPolicy);
            planTab.getScrollPane().setHbarPolicy(hBarPolicy);
            planTab.getScrollPane().setVmax(vmax);
            planTab.getScrollPane().setHmax(hmax);
            planTab.getPlanEditorGroup().setAutoSizeChildren(true);
            planTab.getPlanEditorGroup().setManaged(true);

        }

        // TODO: fire event to signal successful termination of event
        //draw();
        planEditorTabPane.getScene().setCursor(previousCursor);
        AbstractEditorTab selectedItem = (AbstractEditorTab) MainWindowController.getInstance().getEditorTabPane().getSelectionModel()
                .getSelectedItem();
        if (selectedItem != null) {
            List<Pair<Long, AbstractPlanElementContainer>> noSelection = new ArrayList<>();
            noSelection.add(new Pair<>(null, null));
            selectedItem
                    .getSelectedPlanElements().set(noSelection);
        }
        setRecentlyDone(true);
    }

    public boolean isRecentlyDone() {
        return recentlyDone;
    }

    public void setRecentlyDone(boolean recentlyDone) {
        this.recentlyDone = recentlyDone;
    }
}
