package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

/**
 * The class performs operations on given points in WebMercator coordinates
 *
 * @param x the x coordinate of a point
 * @param y the y coordinate of a point
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public final record PointWebMercator(double x, double y) {

    private static final int POWER_EIGHT = 8;

    public PointWebMercator {
        Preconditions.checkArgument(x <= 1 && x >= 0 && y <= 1 && y >= 0);
    }

    /**
     * @param zoomLevel the zoomlevel
     * @param x         the x coordinate of the point
     * @param y         the y coordinate of the point
     * @return the point with these characteristics
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        return new PointWebMercator(Math.scalb(x, -8 - zoomLevel), Math.scalb(y, -POWER_EIGHT - zoomLevel));
    }

    /**
     * @param pointCh the point in swiss coordinates
     * @return the pointWebMercator corresponding to the swiss coordinate point given
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * @param zoomLevel the zoomlevel
     * @return the x coordinate
     */
    public double xAtZoomLevel(int zoomLevel) {
        return Math.scalb(x, POWER_EIGHT + zoomLevel);
    }

    /**
     * @param zoomLevel the zoomlevel
     * @return the y coordinate
     */
    public double yAtZoomLevel(int zoomLevel) {
        return Math.scalb(y, POWER_EIGHT + zoomLevel);
    }

    /**
     * @return the longitude in radians
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * @return the latitude in radians
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * @return whether the point in swiss coordinates is at the same position then the receptor "this" or
     * "null" if the point is not in Switzerland
     */
    public PointCh toPointCh() {
        double lon = lon();
        double lat = lat();
        double e = Ch1903.e(lon, lat);
        double n = Ch1903.n(lon, lat);

        return SwissBounds.containsEN(e, n) ? new PointCh(e, n) : null;
    }
}
