package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * This class takes care of the layout and the interactions on our map
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public final class BaseMapManager {

    /**
     * Number of pixel per tiles
     */
    private final static int pixelPerTile = 256;

    private final TileManager tileManager;
    private final WaypointsManager wayPointsManager;
    private final ObjectProperty<MapViewParameters> mapViewParametersP;
    private final Pane pane;
    private final Canvas canvas;
    private boolean redrawNeeded;


    /**
     * The constructor manages the layout of our map and the waypoints on it
     *
     * @param tileManager        the manager of the tiles
     * @param wayPointsManager   the manager of all the waypoints
     * @param mapViewParametersP the parameters of our Map
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager wayPointsManager,
                          ObjectProperty<MapViewParameters> mapViewParametersP) {

        this.tileManager = tileManager;
        this.wayPointsManager = wayPointsManager;
        this.mapViewParametersP = mapViewParametersP;

        canvas = new Canvas();
        pane = new Pane(canvas);

        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        canvas.widthProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());

        canvas.heightProperty().addListener((p, oldS, newS) -> redrawOnNextPulse());

        this.mapViewParametersP.addListener(e -> redrawOnNextPulse());

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });
        eventHandler();
    }

    /**
     * returns the pane representing our itinerary
     *
     * @return the pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * redraws the elements if variable redrawNeeded is true;
     */

    private void redrawIfNeeded() {

        if (!redrawNeeded) return;
        redrawNeeded = false;

        GraphicsContext context = canvas.getGraphicsContext2D();
        double xImageGap = mapViewParametersP.get().x() % pixelPerTile;
        double yImageGap = mapViewParametersP.get().y() % pixelPerTile;

        for (int x = 0; x < canvas.getWidth() + pixelPerTile; x += pixelPerTile) {
            for (int y = 0; y < canvas.getHeight() + pixelPerTile; y += pixelPerTile) {

                int tileXCoordinate = Math.floorDiv((int) (mapViewParametersP.get().x() + x), pixelPerTile);
                int tileYCoordinate = Math.floorDiv((int) (mapViewParametersP.get().y() + y), pixelPerTile);

                if (TileManager.TileId.isValid(
                        mapViewParametersP.get().zoomLevel(),
                        tileXCoordinate,
                        tileYCoordinate)) {

                    TileManager.TileId tile = new TileManager.TileId(
                            mapViewParametersP.get().zoomLevel(),
                            tileXCoordinate,
                            tileYCoordinate);

                    try {
                        context.drawImage(tileManager.imageForTileAt(tile),
                                x - xImageGap,
                                y - yImageGap);
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    /**
     * redraws the elements on the next pulse
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * Manages the interactions on the map, such as the mouse scroll to zoom in or out, the mouse drag to move the map,
     * and the mouse click to generate or remove a waypoint
     */
    private void eventHandler() {

        ObjectProperty<Point2D> p = new SimpleObjectProperty<>();

        pane.setOnMouseClicked(e -> {
            if (e.isStillSincePress()) {
                wayPointsManager.addWaypoint(e.getX(), e.getY());
            }
        });

        pane.setOnMouseDragged(e -> {

            Point2D newCoordinates = mapViewParametersP
                    .get()
                    .topLeft()
                    .add(p.get().getX(), p.get().getY())
                    .subtract(e.getX(), e.getY());

            mapViewParametersP.set(
                    mapViewParametersP
                            .get()
                            .withMinXY(newCoordinates.getX(), newCoordinates.getY()));

            p.set(new Point2D(e.getX(), e.getY()));
        });


        SimpleLongProperty minScrollTime = new SimpleLongProperty();


        pane.setOnScroll(e -> {

            if (e.getDeltaY() == 0d) return;

            long currentTime = System.currentTimeMillis();

            if (currentTime < minScrollTime.get()) return;

            minScrollTime.set(currentTime + 200);

            int zoomDelta = (int) Math.signum(e.getDeltaY());
            int newZoom = Math2.clamp(8, mapViewParametersP.get().zoomLevel() - zoomDelta, 19);

            PointWebMercator positionMouseBeforeZoom = mapViewParametersP.get().pointAt(e.getX(), e.getY());

            double xAfterZoom = (positionMouseBeforeZoom.xAtZoomLevel(newZoom) - e.getX());
            double yAfterZoom = (positionMouseBeforeZoom.yAtZoomLevel(newZoom) - e.getY());

            mapViewParametersP.set(new MapViewParameters(newZoom, xAfterZoom, yAfterZoom));

        });


        pane.setOnMousePressed(e -> p.set(new Point2D(e.getX(), e.getY())));

    }
}
