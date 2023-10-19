package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

/**
 * Represents the annotated map of our Route
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public final class AnnotatedMapManager {
    /**
     * Starting zoom level constant
     */
    private final static int START_ZOOM = 12;
    /**
     * Starting x coordinate constant
     */
    private final static int START_X = 543200;
    /**
     * Starting y coordinate constant
     */
    private final static int START_Y = 370650;
    /**
     * Minimum distance of the mouse to the route in order to be registered
     */
    private final static int MIN_MOUSE_DISTANCE_TO_ROUTE = 15;

    private final ObjectProperty<MapViewParameters> mapViewParametersObjectProperty;
    private final Pane pane;
    private final DoubleProperty mousePositionOnRouteProperty;
    private final ObjectProperty<Point2D> mouseProperty;

    /**
     * The constructor of the class generating an annotated map of our itinerary
     *
     * @param roadNetwork the graph containing the road network
     * @param tileManager the tile manager
     * @param bean        the bean of the route
     * @param consumer    the consumer managing the mistakes
     */

    public AnnotatedMapManager(Graph roadNetwork, TileManager tileManager, RouteBean bean, Consumer<String> consumer) {

        mouseProperty = new SimpleObjectProperty<>();

        mousePositionOnRouteProperty = new SimpleDoubleProperty();
        mousePositionOnRouteProperty.set(Double.NaN);

        mapViewParametersObjectProperty = new SimpleObjectProperty<>(
                new MapViewParameters(START_ZOOM, START_X, START_Y));

        WaypointsManager waypointsManager = new WaypointsManager(
                roadNetwork,
                mapViewParametersObjectProperty,
                bean.waypointsProperty(),
                consumer);

        BaseMapManager baseMapManager = new BaseMapManager(
                tileManager,
                waypointsManager,
                mapViewParametersObjectProperty);

        RouteManager routeManager = new RouteManager(
                bean,
                mapViewParametersObjectProperty);

        pane = new StackPane(
                baseMapManager.pane(),
                routeManager.pane(),
                waypointsManager.pane());

        pane.getStylesheets().add("map.css");

        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(() -> {

            if (bean.getRoute() != null && mouseProperty.get() != null) {

                PointCh mousePositionPointCh = mapViewParametersObjectProperty
                        .get()
                        .pointAt(mouseProperty.get().getX(),
                                mouseProperty.get().getY())
                        .toPointCh();

                if (mousePositionPointCh != null) {

                    RoutePoint routePoint = bean.getRoute().pointClosestTo(mousePositionPointCh);

                    PointWebMercator routePointWebMercator = PointWebMercator.ofPointCh(routePoint.point());

                    if ((Math2.norm(
                            mapViewParametersObjectProperty.get()
                                    .viewX(routePointWebMercator) - mouseProperty.get().getX(),
                            mapViewParametersObjectProperty.get()
                                    .viewY(routePointWebMercator)
                                    - mouseProperty.get().getY()) <= MIN_MOUSE_DISTANCE_TO_ROUTE)) {

                        return routePoint.position();

                    } else {
                        return Double.NaN;
                    }
                } else
                    return Double.NaN;
            } else {
                return Double.NaN;
            }

        }, mouseProperty, bean.routeProperty(), mapViewParametersObjectProperty));

        pane.setOnMouseMoved(e -> mouseProperty.set(new Point2D(e.getX(), e.getY())));

        pane.setOnMouseExited(e -> mouseProperty.set(null));
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
     * @return the DoubleProperty containing the position of the mouse on the Route
     */

    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

}
