package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * the class managing the route we want to see on the map
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

public final class RouteManager {

    /**
     * the radius of our disk giving our current position
     */
    private final static int radiusDisk = 5;

    private final RouteBean routeBean;
    private final Pane pane;
    private final ReadOnlyObjectProperty<MapViewParameters> mapViewParametersP;
    private final Circle circle;
    private final Polyline itinerary;

    /**
     * The constructor manages the layout of the itinerary and the possibility of seeing it on the map
     *
     * @param routeBean          the bean of the route
     * @param mapViewParametersP the parameters of our map
     */

    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapViewParametersP) {


        this.routeBean = routeBean;
        this.mapViewParametersP = mapViewParametersP;

        itinerary = new Polyline();

        circle = new Circle(radiusDisk);
        circle.setId("highlight");
        circle.setVisible(false);

        Canvas canvas = new Canvas();
        pane = new Pane(canvas);

        pane.getChildren().add(circle);
        pane.setPickOnBounds(false);


        circleHandler();

        routeBean.routeProperty().addListener((e, oldV, newV) ->
                itineraryConstructor());

        mapViewParametersP.addListener((e, oldV, newV) -> {
            if (routeBean.getRoute() != null) {
                routeMovement(oldV, newV);
            }
        });
    }


    /**
     * the interactions between the disque and our route, it detects when we click on it and when we drag our cursor
     * on the route.
     */
    private void circleHandler() {

        circle.setOnMouseClicked(e -> {
            Point2D coordinatePoint2D = circle.localToParent(new Point2D(e.getX(), e.getY()));
            PointCh coordinatePointCh = mapViewParametersP
                    .get()
                    .pointAt(coordinatePoint2D.getX(),
                            coordinatePoint2D.getY()).toPointCh();

            Route route = routeBean.getRoute();

            double highlightedPosition = routeBean.getHighlightedPosition();

            int closestNodeId = route.nodeClosestTo(highlightedPosition);

            if (!waypointAlreadyExists(closestNodeId)) {

                Waypoint waypoint = new Waypoint(coordinatePointCh, closestNodeId);

                List<Waypoint> waypointList = new ArrayList<>(routeBean.getWaypoints());

                waypointList.add(routeBean.indexOfNonEmptySegmentAt(highlightedPosition) + 1, waypoint);

                routeBean.setWaypoints(waypointList);
            }
        });

        circle.centerXProperty().bind(Bindings.createDoubleBinding(() -> {

            if (routeBean.getRoute() != null && !Double.isNaN(routeBean.getHighlightedPosition())) {

                return mapViewParametersP
                        .get()
                        .viewX(PointWebMercator.ofPointCh(routeBean.getRoute()
                                .pointAt(routeBean.getHighlightedPosition())));
            }
            return Double.NaN;

        }, routeBean.highlightedPositionProperty(), mapViewParametersP));


        circle.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                        (!Double.isNaN(routeBean.getHighlightedPosition()) && !Double.isNaN(circle.getCenterX())
                                && !Double.isNaN(circle.getCenterY())),
                routeBean.highlightedPositionProperty(),
                circle.centerXProperty(),
                circle.centerYProperty()));


        circle.centerYProperty().bind(Bindings.createDoubleBinding(() -> {
                    if (routeBean.getRoute() != null && !Double.isNaN(routeBean.getHighlightedPosition())) {
                        return mapViewParametersP.get()
                                .viewY(PointWebMercator
                                        .ofPointCh(routeBean.getRoute()
                                                .pointAt(routeBean.getHighlightedPosition())));
                    }
                    return Double.NaN;
                }, routeBean.highlightedPositionProperty(), mapViewParametersP
        ));
    }

    /**
     * method managing the change of scale of our map, and allowing us to keep the same route
     *
     * @param oldValue the old map view parameters
     * @param newValue the new map view parameters
     */
    private void routeMovement(MapViewParameters oldValue, MapViewParameters newValue) {

        if (oldValue.zoomLevel() != newValue.zoomLevel()) {
            itineraryConstructor();
        }

        itinerary.setLayoutX(-newValue.x());
        itinerary.setLayoutY(-newValue.y());
    }


    /**
     * checks whether the waypoints already exists
     *
     * @param nodeId the id of a given node
     * @return a boolean on whether the waypoint already exists
     */
    private boolean waypointAlreadyExists(int nodeId) {
        for (Waypoint w : routeBean.getWaypoints()) {
            if (w.nodeId() == nodeId) {
                return true;
            }
        }
        return false;
    }

    /**
     * constructs an itinerary that we can visually observe on the map
     */

    private void itineraryConstructor() {

        int zoomLevel = mapViewParametersP.get().zoomLevel();

        List<Double> nodesPosition = new ArrayList<>();
        itinerary.setVisible(routeBean.getRoute() != null);


        if (routeBean.getRoute() != null) {

            for (PointCh p : routeBean.getRoute().points()) {

                PointWebMercator routePointWebMercator = PointWebMercator.ofPointCh(p);

                nodesPosition.add(routePointWebMercator.xAtZoomLevel(zoomLevel));
                nodesPosition.add(routePointWebMercator.yAtZoomLevel(zoomLevel));
            }

            itinerary.getPoints().clear();
            itinerary.getPoints().setAll(nodesPosition);
            itinerary.setId("route");

            pane.getChildren().clear();
            pane.getChildren().addAll(itinerary, circle);


            itinerary.setLayoutX(-mapViewParametersP.get().x());
            itinerary.setLayoutY(-mapViewParametersP.get().y());
        }
    }


    /**
     * returns the pane
     *
     * @return the pane
     */

    public Pane pane() {
        return pane;
    }
}
