package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

/**
 * The class represents an itinerary multiple, this means it is composed of multiple segments
 */
public class MultiRoute implements Route {

    private final List<Route> segments;

    /**
     * generates an itinerary made out of the different segments given
     *
     * @param segments the different segments making our MultiRoute
     */
    public MultiRoute(List<Route> segments) {

        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
    }


    /**
     * @param position the position we want to look at
     * @return the index of the segment of the itinerary containing the given position
     */
    @Override
    public int indexOfSegmentAt(double position) {

        position = Math2.clamp(0, position, length());
        int index = 0;

        for (Route r : segments) {
            if (position > r.length()) {

                position -= r.length();
                index += r.indexOfSegmentAt(r.length()) + 1;

            } else {

                index += r.indexOfSegmentAt(position);
                break;
            }
        }
        return index;
    }


    /**
     * @return the length of the itinerary
     */
    @Override
    public double length() {


        double length = 0;

        for (Route r : segments) {
            length += r.length();
        }

        return length;
    }


    /**
     * @return the list of edges of the itinerary
     */

    @Override
    public List<Edge> edges() {


        List<Edge> edgesList = new ArrayList<>();

        for (Route s : segments) {
            for (Edge e : s.edges()) {
                if (!edgesList.contains(e)) {
                    edgesList.add(e);
                }
            }
        }
        return edgesList;
    }


    /**
     * @return the list of points at the extremity of each edge without duplicate
     */
    @Override
    public List<PointCh> points() {

        List<PointCh> pointsList = new ArrayList<>();

        for (Route r : segments) {
            if (!pointsList.isEmpty()) {
                pointsList.remove(pointsList.size() - 1);
            }
            pointsList.addAll(r.points());
        }
        return pointsList;
    }


    /**
     * @param position the position we want to reach
     * @return the height at the given position on the itinerary
     */
    @Override
    public double elevationAt(double position) {

        position = Math2.clamp(0, position, length());

        return segments
                .get((int) segmentIndexPosition(position)[0])
                .elevationAt(segmentIndexPosition(position)[1]);
    }


    /**
     * @param position the position we want to reach
     * @return the point on the given position on the itinerary
     */
    @Override
    public PointCh pointAt(double position) {

        position = Math2.clamp(0, position, length());

        return segments
                .get((int) segmentIndexPosition(position)[0])
                .pointAt(segmentIndexPosition(position)[1]);
    }


    /**
     * @param position the position we want to reach
     * @return the closest node of the itinerary to the position given
     */
    @Override
    public int nodeClosestTo(double position) {

        position = Math2.clamp(0, position, length());
        double[] segmentsTable = segmentIndexPosition(position);

        return segments
                .get((int) segmentsTable[0])
                .nodeClosestTo(segmentsTable[1]);
    }


    /**
     * @param point a given point to compare
     * @return the point of the itinerary closest to the given point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {

        RoutePoint actualRoutePoint = RoutePoint.NONE;
        double d = 0;

        for (Route s : segments) {

            actualRoutePoint = actualRoutePoint
                    .min(s
                            .pointClosestTo(point)
                            .withPositionShiftedBy(d));

            d += s.length();
        }
        return actualRoutePoint;
    }


    /**
     * In order to avoid coping code, this function is here to search for the segment index in the list the position is located in
     * and the position inside this segment
     *
     * @param position position given
     * @return the index of the segment the position is at and the position inside this segment
     */

    private double[] segmentIndexPosition(double position) {

        int index = 0;
        double[] tab = new double[2];

        for (Route r : segments) {
            if (position > r.length()) {
                position -= r.length();
                index++;
            } else {
                break;
            }
        }

        tab[0] = index;
        tab[1] = position;

        return tab;
    }
}
