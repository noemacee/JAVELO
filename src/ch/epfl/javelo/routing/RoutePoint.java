package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

/**
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

/**
 * Represents a given point to the closest point on the itinerary
 *
 * @param point               a given point
 * @param position            the position on the edge
 * @param distanceToReference the distance between the RoutePoint and the given point
 */
public record RoutePoint(PointCh point, double position, double distanceToReference) {

    /**
     * creating a NONE RoutePoint with abstract attributes for uses when needed
     */
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, Double.POSITIVE_INFINITY);


    /**
     * @param positionDifference the distance separating both points
     * @return an identical point to the receptor this, with a different position from the distance given
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return (positionDifference == 0) ? this : new RoutePoint(point,
                position + positionDifference,
                distanceToReference);
    }


    /**
     * @param that another RoutePoint we compare to this
     * @return this if the distanceToReference is inferior to the one of that, else returns that
     */
    public RoutePoint min(RoutePoint that) {
        return distanceToReference <= that.distanceToReference ? this : that;
    }


    /**
     * returns the RoutePoint with the smallest distance to "thatDistanceToReference"
     *
     * @param thatPoint               the first point
     * @param thatPosition            the second point
     * @param thatDistanceToReference the distance we want to compare them with
     * @return this if the distanceToReference <= thatDistanceToReference, or a new instance of Routepoint
     * with the arguments passed to min
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        if (distanceToReference <= thatDistanceToReference) {
            return this;
        }
        return new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}