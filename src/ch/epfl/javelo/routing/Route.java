package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;


/**
 * The interface represents an itinerary
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */


public interface Route {

    /**
     * @param position on the itinerary
     * @return the index of the segment at the given position
     */
    int indexOfSegmentAt(double position);

    /**
     * @return the length of the route
     */
    double length();

    /**
     * @return the list of edges
     */
    List<Edge> edges();

    /**
     * @return the list of points
     */
    List<PointCh> points();

    /**
     * @param position the given position on the itinerary
     * @return the elevation at that position
     */
    double elevationAt(double position);

    /**
     * @param position the given position on the itinerary
     * @return the point at the position
     */
    PointCh pointAt(double position);

    /**
     * @param position the given position on the itinerary
     * @return the closest node to the position
     */
    int nodeClosestTo(double position);

    /**
     * @param point a given point
     * @return the closest point of the itinerary
     */
    RoutePoint pointClosestTo(PointCh point);


}
