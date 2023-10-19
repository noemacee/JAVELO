package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * Represents the Map parameters depending on the zoomLevel, and coordinates of
 * the top left point
 *
 * @param zoomLevel the zoom level of the map
 * @param x         the x coordinate in Web Mercator
 * @param y         the y coordinate in Web Mercator
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

public final record MapViewParameters(int zoomLevel, double x, double y) {

    /**
     * Creates a Point2D of the top left points
     *
     * @return a Point2D top the top left point
     */

    public Point2D topLeft() {
        return new Point2D(x, y);
    }

    /**
     * @param x the x coordinate
     * @param y the y coordinate
     * @return a new WebMercator with x and y as its top left coordinates
     */
    public MapViewParameters withMinXY(double x, double y) {
        return new MapViewParameters(zoomLevel, x, y);
    }

    /**
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the point in WebMercator coordinates
     */
    public PointWebMercator pointAt(double x, double y) {
        return PointWebMercator.of(zoomLevel, x + this.x, y + this.y);
    }

    /**
     * @param p the point in WebMercator coordinates
     * @return the x coordinate of p compared with the top left coordinates
     */

    public double viewX(PointWebMercator p) {
        return p.xAtZoomLevel(zoomLevel) - this.x;
    }

    /**
     * @param p the point in WebMercator coordinates
     * @return the y coordinate of p compared with the top left coordinates
     */
    public double viewY(PointWebMercator p) {
        return p.yAtZoomLevel(zoomLevel) - this.y;
    }
}
