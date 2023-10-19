package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class represents a simple itinerary and implements Route
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

public final class SingleRoute implements Route {

    private final List<Edge> edges;
    private final double[] tablePosition;
    private final List<PointCh> points;

    /**
     * returns the simple itinerary made of the given edges, or throws an IllegalArgumentException if the
     * list is empty
     *
     * @param edges the list of the edges making the itinerary
     */
    public SingleRoute(List<Edge> edges) {

        assert edges != null;
        Preconditions.checkArgument(!edges.isEmpty());

        this.edges = List.copyOf(edges);
        this.tablePosition = new double[edges.size() + 1];

        tablePosition[0] = 0;
        for (int i = 1; i < tablePosition.length; i++) {
            tablePosition[i] = tablePosition[i - 1] + this.edges.get(i - 1).length();
        }

        points = pointsCreator();
    }

    /**
     * @param position the position we want to analyze
     * @return the index of the segment at the given position in the route
     */

    public int indexOfSegmentAt(double position) {
        return 0;
    }


    /**
     * @return the length of the route
     */
    public double length() {
        return tablePosition[tablePosition.length - 1];
    }


    /**
     * @return the list of edges of the route
     */
    public List<Edge> edges() {
        return this.edges;
    }


    /**
     * @return the points located at the extremity of each path
     */
    public List<PointCh> points() {
        return points;
    }

    /**
     * Initialises the List of PointCh "points", with the points located at the extremity of each path
     */
    private List<PointCh> pointsCreator() {

        List<PointCh> pointList = new ArrayList<>();

        for (Edge edge : edges) {
            pointList.add(edge.fromPoint());
        }

        pointList.add(edges.get(edges().size() - 1).toPoint());
        return List.copyOf(pointList);
    }


    /**
     * @param position the position we want to analyze
     * @return the point at the given position on the route
     */
    public PointCh pointAt(double position) {

        position = Math2.clamp(0, position, length());
        int index = Arrays.binarySearch(tablePosition, position);

        if (index >= 0) {
            if (index == 0) {
                return edges.get(index).fromPoint();
            }
            return edges().get(index - 1).toPoint();
        }

        int edge = Math.abs(index) - 2;
        return edges().get(edge).pointAt(position - tablePosition[edge]);

    }

    /**
     * @param position the position we want to analyze
     * @return the elevation at the given position on the route
     */

    public double elevationAt(double position) {

        position = Math2.clamp(0, position, length());

        int index = Arrays.binarySearch(tablePosition, position);

        if (index < 0) {
            index = Math.abs(index) - 2;
        }

        return index == edges.size()
                ? edges.get(index - 1).elevationAt(edges.get(index - 1).length())
                : edges.get(index).elevationAt(position - tablePosition[index]);
    }

    /**
     * @param position the position we want to analyze
     * @return the identity of the closest node to the given position
     */

    public int nodeClosestTo(double position) {

        position = Math2.clamp(0, position, length());
        int index = Arrays.binarySearch(tablePosition, position);

        if (index == edges().size()) {
            return edges.get(index - 1).toNodeId();
        }
        if (index >= 0) {
            return edges.get(index).fromNodeId();
        }

        int edge = Math.abs(index) - 2;

        return ((edge < tablePosition.length - 1)
                && (position - tablePosition[edge] < tablePosition[edge + 1] - position))
                ? edges.get(edge).fromNodeId()
                : edges.get(edge).toNodeId();
    }

    /**
     * @param point the given point
     * @return the point of the route the closest to the given point
     */
    public RoutePoint pointClosestTo(PointCh point) {

        RoutePoint actualRoutePoint = RoutePoint.NONE;
        int node = 0;
        PointCh closestPoint;

        for (Edge edge : edges) {

            double distanceClosest = Math2.clamp(0, edge.positionClosestTo(point), edge.length());
            closestPoint = edge.pointAt(distanceClosest);

            double distancePointToRef = closestPoint.distanceTo(point);
            actualRoutePoint = actualRoutePoint.min(closestPoint,
                    distanceClosest + tablePosition[node],
                    distancePointToRef);

            node++;
        }
        return actualRoutePoint;
    }
}