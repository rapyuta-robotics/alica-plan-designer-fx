//package de.unikassel.vs.alica.planDesigner.view.editor.container;
//
//
//import de.unikassel.vs.alica.planDesigner.view.editor.tab.planTab.PlanEditorGroup;
//import de.unikassel.vs.alica.planDesigner.view.editor.tab.planTab.PlanTab;
//import de.unikassel.vs.alica.planDesigner.view.editor.tools.AbstractTool;
//import de.unikassel.vs.alica.planDesigner.view.img.AlicaIcon;
//import de.unikassel.vs.alica.planDesigner.view.menu.ShowGeneratedSourcesMenuItem;
//import de.unikassel.vs.alica.planDesigner.view.model.*;
//import javafx.event.EventHandler;
//import javafx.scene.Node;
//import javafx.scene.control.ContextMenu;
//import javafx.scene.control.Label;
//import javafx.scene.effect.BlurType;
//import javafx.scene.effect.DropShadow;
//import javafx.scene.effect.Effect;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.ContextMenuEvent;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.Background;
//import javafx.scene.paint.Color;
//
//public abstract class ContainerLabel extends Label {
//
//    protected static final Effect standardEffect = new DropShadow(BlurType.THREE_PASS_BOX,
//            new Color(0, 0, 0, 0.8), 10, 0, 0, 0);
//
//    protected PlanElementViewModel planElementViewModel;
//    protected PlanTab planTab;
//
//    /**
//     * @param planElementViewModel
//     * @param planTab
//     */
//    public ContainerLabel(PlanElementViewModel planElementViewModel, PlanTab planTab) {
//        this.planElementViewModel = planElementViewModel;
//        this.planTab = planTab;
//        setBackground(Background.EMPTY);
//        setPickOnBounds(false);
//        addEventFilter(MouseEvent.MOUSE_CLICKED, getMouseClickedEventHandler());
//        setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
//            @Override
//            public void handle(ContextMenuEvent e) {
//                if (planElementViewModel instanceof StateViewModel || planElementViewModel instanceof PlanTypeViewModel
//                        || planElementViewModel instanceof SynchronisationViewModel) {
//                    return;
//                }
//
//                ContextMenu contextMenu;
//                if (planElementViewModel instanceof BehaviourViewModel || planElementViewModel instanceof PlanViewModel) {
//                    contextMenu = new ContextMenu(new ShowGeneratedSourcesMenuItem(planElementViewModel.getId()));
//                } else {
//                    contextMenu = new ContextMenu(new ShowGeneratedSourcesMenuItem(planElementViewModel.getParentId()));
//                }
//                contextMenu.show(ContainerLabel.this, e.getScreenX(), e.getScreenY());
//            }
//        });
//        // prohibit containers from growing indefinitely (especially transition containers)
//        setMaxSize(1, 1);
//    }
//
//    public PlanEditorGroup getPlanEditorGroup() {
//        return planTab.getPlanEditorGroup();
//    }
//
//    /**
//     * Sets the selection flag for the editor when modelElementId is clicked.
//     * Unless the last click was performed as part of a tool phase.
//     *
//     * @return
//     */
//    private EventHandler<MouseEvent> getMouseClickedEventHandler() {
//        return event -> {
//            // Was the last click performed in the context of a tool?
//            AbstractTool recentlyDoneTool = Container.this.planTab.getEditorToolBar().getRecentlyDoneTool();
//            if (recentlyDoneTool != null) {
//                recentlyDoneTool.setRecentlyDone(false);
//                event.consume();
//            } else {
//                // Find the first Container in the hierarchy above the targeted Node
//                Node targetNode = event.getPickResult().getIntersectedNode();
//                while (targetNode != null && !(targetNode instanceof Container)) {
//                    targetNode = targetNode.getParent();
//                }
//                // If the targeted Container is this, select this and consume the event
//                if (targetNode == this) {
//                    handleMouseClickedEvent(event);
//                }
//                // If the targeted Container is not this (meaning it's a child of this) don't consume the event to
//                // allow the targeted Container to be selected
//            }
//        };
//    }
//
//    protected void handleMouseClickedEvent(MouseEvent event) {
//        planTab.setSelectedContainer(this);
//        event.consume();
//    }
//
//    public Node getVisualRepresentation() {
//        return visualRepresentation;
//    }
//
//    @Override
//    public PlanElementViewModel getPlanElementViewModel() {
//        return planElementViewModel;
//    }
//
//
//
//    /**
//     * Sets the standard effect for the {@link Container}.
//     * This can be overwritten by a child class for individual styling.
//     */
//    public void setEffectToStandard() {
//        setEffect(Container.standardEffect);
//    }
//
//    /**
//     * Sets the custom effect for the {@link Container}.
//     * This can be overwritten by a child class for individual styling.
//     */
//    public void setCustomEffect(Effect effect) {
//        this.setEffect(effect);
//    }
//
//    public ImageView getGraphic(String iconName) {
//        return new ImageView(new AlicaIcon(iconName, AlicaIcon.Size.SMALL));
//    }
//
//    public abstract void setupLabel();
//
//    @Override
//    public void redrawElement() {
//    }
//}
