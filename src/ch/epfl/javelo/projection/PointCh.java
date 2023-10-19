package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import static ch.epfl.javelo.projection.SwissBounds.containsEN;

/**
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */


public record PointCh(double e, double n) {

    /**
     * @param e e the east coordinate in WGS84 system
     * @param n n the north coordinate in WGS84 system
     * @throws IllegalArgumentException if e or n coordinates aren't in Switzerland
     */
    public PointCh {
        Preconditions.checkArgument(containsEN(e, n));
    }

    /**
     * @param that Point given
     * @return the square of the distance in meters separating "this" from "that"
     */
    public double squaredDistanceTo(PointCh that) {
        return Math2.squaredNorm(that.e - this.e, that.n - this.n);
    }

    /**
     * @return the distance in meters separating "this" from "that
     */

    public double distanceTo(PointCh that) {
        return Math.sqrt(squaredDistanceTo(that));
    }

    /**
     * @return the longitude given E and N in the WGS84 system in radians
     */
    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * @return the latitude given E and N in the WGS84 system in radians
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }
}
