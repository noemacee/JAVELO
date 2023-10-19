package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

/**
 * Takes care of all the waypoints interactions
 *
 * @author Noé Macé 341504
 * @author Mathis Richard 346419
 */
public final class WaypointsManager {

    private final Consumer<String> consumer;
    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;

    private final Graph graph;
    private final Pane pane;

    /**
     * Takes care of the waypoint representation on the map creation, thus adding pins when we click
     *
     * @param graph              the graph
     * @param mapViewParametersP the parameters of the map at our level
     * @param waypoints          the list of waypoints
     * @param consumer           the consumer of errors
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapViewParametersP,
                            ObservableList<Waypoint> waypoints, Consumer<String> consumer) {

        this.consumer = consumer;
        this.mapViewParametersP = mapViewParametersP;
        this.waypoints = waypoints;
        this.graph = graph;
        Canvas canvas = new Canvas();
        this.pane = new Pane(canvas);

        pinCreator();

        pane.setPickOnBounds(false);

        waypoints.addListener((Observable o) -> pinCreator());

        mapViewParametersP.addListener((Observable o) -> pinCreator());


    }


    /**
     * adds a Waypoint to the list of Waypoints if it exists
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void addWaypoint(double x, double y) {
        Waypoint waypoint = isNode(x, y);
        if (waypoint != null) {
            waypoints.add(waypoint);
        }

    }

    /**
     * checks whether the x and y corresponds to a Node or if it is too far away (>500 meters)
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the Waypoint closest to x,y or null if it is to far away
     */

    private Waypoint isNode(double x, double y) {
        PointCh waypointCh = mapViewParametersP.get().pointAt(x, y).toPointCh();

        if (waypointCh == null || graph.nodeClosestTo(waypointCh, 500) == -1) {

            consumer.accept("Aucune route à proximité !");
        } else {

            int nodeId = graph.nodeClosestTo(waypointCh, 500);
            return new Waypoint(waypointCh, nodeId);
        }
        return null;
    }

    /**
     * returns the pane
     *
     * @return the pane
     */
    public Pane pane() {
        return pane;
    }


    /**
     * handles all our pins interactions
     *
     * @param pin   a group corresponding to the pin
     * @param index index of the waypoint attached to the pin in the list of waypoints
     */
    private void handler(Group pin, int index) {
        PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(waypoints.get(index).swiss());

        Waypoint waypoint = waypoints.get(index);

        ObjectProperty<Point2D> pinPoint = new SimpleObjectProperty<>();

        pin.setLayoutX(mapViewParametersP.get().viewX(pointWebMercator));
        pin.setLayoutY(mapViewParametersP.get().viewY(pointWebMercator));

        pin.setOnMousePressed(e ->
                pinPoint.set(new Point2D(e.getX(), e.getY())));

        pin.setOnMouseDragged(e -> {
            Point2D newPoint = new Point2D(pin.getLayoutX(), pin.getLayoutY())
                    .add(e.getX(), e.getY()).subtract(pinPoint.get());

            pin.setLayoutX(newPoint.getX());
            pin.setLayoutY(newPoint.getY());

        });


        pin.setOnMouseReleased(e -> {
            if (e.isStillSincePress()) {
                waypoints.remove(waypoint);
            } else {

                Point2D newPoint = new Point2D(pin.getLayoutX(),
                        pin.getLayoutY()).add(e.getX(),
                        e.getY()).subtract(pinPoint.get());

                Waypoint newWaypoint = isNode(newPoint.getX(), newPoint.getY());


                if (newWaypoint != null) {

                    PointWebMercator pinNewCoordinates = PointWebMercator.ofPointCh(newWaypoint.swiss());

                    pin.setLayoutX(mapViewParametersP.get().viewX(pinNewCoordinates));
                    pin.setLayoutY(mapViewParametersP.get().viewY(pinNewCoordinates));

                    waypoints.set(index, newWaypoint);

                } else {

                    pin.setLayoutX(pinPoint.get().getX());
                    pin.setLayoutY(pinPoint.get().getY());

                    waypoints.set(index, waypoint);
                }
            }
        });
    }

    /**
     * method to create the pins displayed to represent our waypoints
     */
    private void pinCreator() {

        pane.getChildren().clear();


        for (int i = 0; i < waypoints.size(); i++) {


            SVGPath svgOutside = new SVGPath();
            svgOutside.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
            svgOutside.getStyleClass().add("pin_outside");

            SVGPath svgInside = new SVGPath();
            svgInside.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");
            svgInside.getStyleClass().add("pin_inside");

            Group pin = new Group(svgOutside, svgInside);
            pin.getStyleClass().add("pin");

            PointWebMercator pinCoordinates = PointWebMercator.ofPointCh(waypoints.get(i).swiss());

            pin.setLayoutX(mapViewParametersP.get().viewX(pinCoordinates));
            pin.setLayoutY(mapViewParametersP.get().viewY(pinCoordinates));

            int size = waypoints.size() - 1;


            if (i == 0) {
                pin.getStyleClass().add("first");
            } else if (i == size) {
                pin.getStyleClass().add("last");
            } else {
                pin.getStyleClass().add("middle");
            }

            pane.getChildren().add(pin);
            handler(pin, i);
        }
    }
}





