package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;


/**
 * This class allows us to convert WGS84 coordinates to WebMercator coordinates and vice versa
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

public final class WebMercator {

    private WebMercator() {
    }

    /**
     * transforms a longitude in  WGS84 coordinates to the x value in WebMercator coordinates
     *
     * @param lon the longitude in radians
     * @return the x coordinate corresponding to the longitude given
     */
    public static double x(double lon) {
        return (lon + Math.PI) / (2 * Math.PI);
    }

    /**
     * transforms a latitude in  WGS84 coordinates to the y value in WebMercator coordinates
     *
     * @param lat the latitude in radians
     * @return the y coordinate corresponding to the latitude given
     */
    public static double y(double lat) {
        return ((Math.PI - Math2.asinh(Math.tan(lat))) / (2 * Math.PI));
    }

    /**
     * transforms a x value in WebMercator coordinates to its corresponding WGS84 longitude
     *
     * @param x the x coordinate
     * @return the longitude in radians corresponding to the x given
     */
    public static double lon(double x) {
        return 2 * Math.PI * x - Math.PI;
    }

    /**
     * transforms a y value in WebMercator coordinates to its corresponding WGS84 latitude
     *
     * @param y the y coordinate
     * @return the latitude in radians corresponding to the y given
     */
    public static double lat(double y) {
        return Math.atan(Math.sinh(Math.PI - 2 * Math.PI * y));
    }
}
