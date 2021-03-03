package de.unikassel.vs.alica.planDesigner.view.editor.container;

import de.unikassel.vs.alica.planDesigner.view.editor.tab.planTab.PlanTab;
import de.unikassel.vs.alica.planDesigner.view.model.BendPointViewModel;
import de.unikassel.vs.alica.planDesigner.view.model.TransitionViewModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class TransitionContainer extends Container implements Observable {
    private TransitionViewModel containedElement;
    private StateContainer fromState;
    private StateContainer toState;
    private List<BendpointContainer> bendpoints;
    private List<PotentialBendPointContainer> potentialBendpoints;
    private List<InvalidationListener> invalidationListeners = new ArrayList<>();

    public TransitionContainer(TransitionViewModel transition,
                               StateContainer fromState, StateContainer toState, PlanTab planTab) {
        super(transition, planTab);
        this.setContainedElement(transition);
        this.setId("TransitionContainer");
        this.fromState = fromState;
        this.toState = toState;
        InvalidationListener invalidationListener = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                TransitionContainer.this.setupContainer();
            }
        };
        fromState.addListener(invalidationListener);
        toState.addListener(invalidationListener);
        bendpoints = new ArrayList<>();
        potentialBendpoints = new ArrayList<>();
        setupContainer();

        ((TransitionViewModel) getPlanElementViewModel()).getBendpoints().addListener(new ListChangeListener<BendPointViewModel>() {
            @Override
            public void onChanged(Change<? extends BendPointViewModel> c) {
                redrawElement();
            }
        });
    }

    @Override
    public void setupContainer() {
        //setBackground(new Background(new BackgroundFill(Color.GREEN,null,null)));
        getChildren().clear();
        bendpoints.clear();
        potentialBendpoints.clear();

        Polygon polygon = new Polygon();
        double _toX = toState.getLayoutX() + toState.getTranslateX();
        double _toY = toState.getLayoutY() + toState.getTranslateY();
        double _fromX = fromState.getLayoutX() + fromState.getTranslateX();
        double _fromY = fromState.getLayoutY() + fromState.getTranslateY();

        double vecX = _toX - _fromX;
        double vecY = _toY - _fromY;
        double vecLen = Math.sqrt(vecX*vecX + vecY*vecY);
        _toX = _toX - StateContainer.STATE_RADIUS*(vecX/vecLen);
        _toY = _toY - StateContainer.STATE_RADIUS*(vecY/vecLen);
        vecX = _toX - _fromX;
        vecY = _toY - _fromY;
        vecLen = Math.sqrt(vecX*vecX + vecY*vecY);

        double triangleSpanVecX = vecY;
        double triangleSpanVecY = -vecX;
        double triangleSpanLen = Math.sqrt(triangleSpanVecY * triangleSpanVecY + triangleSpanVecX * triangleSpanVecX);

        List<BendPointViewModel> bendpoints = ((TransitionViewModel) getPlanElementViewModel()).getBendpoints();
        int size = bendpoints.size();
        if (size == 0) {
            visualRepresentation = new Line(_fromX, _fromY, _toX, _toY);

            //TODO create possible new bendpoint
//            Bendpoint bendpoint = createBendpointInMiddle(_toX, _toY, _fromX, _fromY);
//            potentialBendpoints.add(makePotentialBendpoint(bendpoint));

            //BendPointViewModel potentialBendPoint = new BendPointViewModel();

            polygon = new Polygon(_toX - 5*(vecX/vecLen)+5*(triangleSpanVecX/triangleSpanLen),
                    _toY - 5*(vecY/vecLen) + 5*(triangleSpanVecY/triangleSpanLen),
                    _toX,
                    _toY,
                    _toX - 5*(vecX/vecLen)-5*(triangleSpanVecX/triangleSpanLen),
                    _toY - 5*(vecY/vecLen) - 5*(triangleSpanVecY/triangleSpanLen));
        } else {
            double[] points = new double[size * 2 + 4];
            points[0] = _fromX;
            points[1] = _fromY;

            //BendPointViewModel bendpoint = bendpoints.get(0);
            //BendPointViewModel firstMiddle = createBendpointInMiddle(bendpoint.getX(), bendpoint.getY(), _fromX, _fromY);
            //potentialBendpoints.add(makePotentialBendpoint(firstMiddle));


            for (int i = 0, j = 2; i < points.length / 2 - 2; i++, j += 2) {
                BendPointViewModel currentBendpoint = bendpoints.get(i);
                points[j] = currentBendpoint.getX();
                points[j + 1] = currentBendpoint.getY();
                BendpointContainer bendpointContainer = new BendpointContainer(currentBendpoint, getPlanElementViewModel(), planTab, this);
                //bendpointContainer.setVisible(false);
                this.bendpoints.add(bendpointContainer);
                _fromX = points[j];
                _fromY = points[j+1];
                vecX = _toX - _fromX;
                vecY = _toY - _fromY;
                vecLen = Math.sqrt(vecX*vecX + vecY*vecY);
                triangleSpanVecX = vecY;
                triangleSpanVecY = -vecX;
                triangleSpanLen = Math.sqrt(triangleSpanVecY * triangleSpanVecY + triangleSpanVecX * triangleSpanVecX);

                if (i != size -1 && size != 1) {
                    BendPointViewModel from = bendpoints.get(i);
                    BendPointViewModel to = bendpoints.get(i + 1);
                    //Bendpoint bendpointInMiddle = createBendpointInMiddle(to.getXPos(), to.getYPos(), from.getXPos(), from.getYPos());
                    //potentialBendpoints.add(makePotentialBendpoint(bendpointInMiddle));
                }

            }

            points[points.length - 2] = _toX;
            points[points.length - 1] = _toY;

            BendPointViewModel lastBendpoint = bendpoints.get(size - 1);
            //Bendpoint bendpointInMiddle = createBendpointInMiddle(_toX, _toY, lastBendpoint.getXPos(), lastBendpoint.getYPos());
            //potentialBendpoints.add(makePotentialBendpoint(bendpointInMiddle));

            visualRepresentation = new Polyline(points);
            ((Shape)visualRepresentation).setFill(null);
            polygon = new Polygon(_toX - 5*(vecX/vecLen)+5*(triangleSpanVecX/triangleSpanLen),
                    _toY - 5*(vecY/vecLen) + 5*(triangleSpanVecY/triangleSpanLen),
                    _toX,
                    _toY,
                    _toX - 5*(vecX/vecLen)-5*(triangleSpanVecX/triangleSpanLen),
                    _toY - 5*(vecY/vecLen) - 5*(triangleSpanVecY/triangleSpanLen));
        }

        polygon.setFill(getVisualisationColor());
        polygon.setStroke(getVisualisationColor());
        polygon.setStrokeWidth(4);
        polygon.setVisible(true);
        ((Shape)visualRepresentation).setStrokeWidth(3);
        ((Shape)visualRepresentation).setStroke(getVisualisationColor());
        //setBendpointContainerVisibility(MainWindowController.getInstance().isSelectedPlanElement(this));
        //setPotentialDraggableNodesVisible(MainWindowController.getInstance().isSelectedPlanElement(this));
        visualRepresentation.setPickOnBounds(false);
        this.getChildren().add(visualRepresentation);
        this.getChildren().add(polygon);
        this.getChildren().addAll(this.bendpoints);
        this.getChildren().addAll(potentialBendpoints);
        invalidationListeners.forEach(e-> e.invalidated(this));
    }

//    protected PotentialBendPointContainer makePotentialBendpoint(Bendpoint bendpoint) {
//        return new PotentialBendPointContainer(bendpoint, getPmlUiExtension(), commandStack,
//                fromState.getModelElementId().getInPlan(), this);
//    }

//    protected Bendpoint createBendpointInMiddle(double _toX, double _toY, double _fromX, double _fromY) {
//        Bendpoint bendpoint = EMFModelUtils.getPmlUiExtensionModelFactory().createBendpoint();
//        bendpoint.setXPos((int)(_fromX + ((_toX - _fromX) / 2)));
//        bendpoint.setYPos((int)(_fromY + ((_toY - _fromY) / 2)));
//        return bendpoint;
//    }

    @Override
    public Color getVisualisationColor() {
        return Color.GREEN;
    }

    public void setBendpointContainerVisibility(boolean isVisible) {
        getBendpoints().forEach(d -> d.setVisible(isVisible));
    }

    public List<BendpointContainer> getBendpoints() {
        return bendpoints;
    }

    public void setPotentialDraggableNodesVisible(boolean visible) {
        for (Node potentialDraggableNode : potentialBendpoints) {
            potentialDraggableNode.setVisible(visible);
        }
    }

    @Override
    public void redrawElement() {
        setupContainer();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListeners.remove(listener);
    }

    @Override
    public void setEffectToStandard() {
        this.setEffect(null);
    }

    public TransitionViewModel getContainedElement() {
        return containedElement;
    }

    public void setContainedElement(TransitionViewModel containedElement) {
        this.containedElement = containedElement;
    }

    public StateContainer getFromState() {
        return fromState;
    }

    public StateContainer getToState() {
        return toState;
    }
}
