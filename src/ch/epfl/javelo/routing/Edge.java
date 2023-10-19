package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;


/**
 * Represents one Edge of an itinerary
 *
 * @param fromNodeId the node the edge starts at
 * @param toNodeId   the node the edge ends at
 * @param fromPoint  the point we start from
 * @param toPoint    the point we reach at the end of the edge
 * @param length     the length of the edge
 * @param profile    the profile of the edge
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public final record Edge(int fromNodeId,
                   int toNodeId,
                   PointCh fromPoint,
                   PointCh toPoint,
                   double length,
                   DoubleUnaryOperator profile) {


    /**
     * @param graph      the graph
     * @param edgeId     the identity of the edge
     * @param fromNodeId the identity of the starting node
     * @param toNodeId   the identity of the ending node
     * @return an Edge with the given attributes and the rest are given by the edgeId's in the graph "Graph"
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId,
                toNodeId,
                graph.nodePoint(fromNodeId),
                graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId),
                graph.edgeProfile(edgeId));
    }

    /**
     * @param point a certain point
     * @return the position on the edge closest to the given point
     */
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(),
                fromPoint.n(),
                toPoint.e(),
                toPoint.n(),
                point.e(),
                point.n());
    }


    /**
     * @param position the position wanted
     * @return the point on the edge at the position given
     */
    public PointCh pointAt(double position) {

        if (length != 0) {

            double x = position / length;

            double e = Math2.interpolate(fromPoint.e(), toPoint.e(), x);
            double n = Math2.interpolate(fromPoint.n(), toPoint().n(), x);

            return new PointCh(e, n);
        }

        return fromPoint;
    }


    /**
     * @param position the position wanted
     * @return the elevation at the position on the edge
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }


}


