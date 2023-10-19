package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The class managing the beans of the route
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419 *
 */
public final class RouteBean {

    /**
     * the maximum capacity of the LinkedHashMap
     */
    private static final int MAX_CAPACITY = 100;

    /**
     * the smallest number of waypoints to create a route
     */
    private static final int MIN_WAYPOINTS_SIZE = 2;

    /**
     * the maximum length between steps
     */
    private static final int MAX_STEP_LENGTH = 5;

    private final RouteComputer routeComputer;
    private final LinkedHashMap<Pair<Integer, Integer>, Route> memory;

    private final ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<ElevationProfile> elevationProfile;

    /**
     * JavaFX bean regrouping the properties relating to waypoints and the corresponding route
     *
     * @param routeComputer Represents an itinerary planner we want to attach to the bean
     */

    public RouteBean(RouteComputer routeComputer) {

        this.routeComputer = routeComputer;
        this.memory = new LinkedHashMap<>(MAX_CAPACITY, 0.75F, true);

        highlightedPosition = new SimpleDoubleProperty();
        waypoints = FXCollections.observableArrayList();
        elevationProfile = new SimpleObjectProperty<>();
        route = new SimpleObjectProperty<>();

        waypoints.addListener((Observable o) ->
                newRouteCalculations());
    }

    /**
     * @return the observable list of waypoints
     */
    public ObservableList<Waypoint> waypointsProperty() {
        return waypoints;
    }

    /**
     * @return the list of waypoints
     */
    public List<Waypoint> getWaypoints() {
        return waypoints;
    }


    /**
     * allows us to set a list of waypoints
     *
     * @param waypoints List of waypoint to set to the waypoints property
     */

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints.setAll(List.copyOf(waypoints));
    }

    /**
     * Getter of the object property containing the Route as an only readable object
     *
     * @return the route
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    /**
     * calculates a new route with our entire list of waypoints
     */

    private void newRouteCalculations() {

        List<Route> segments = new ArrayList<>();

        if (waypoints.size() >= MIN_WAYPOINTS_SIZE)
            for (int i = 0; i < waypoints.size() - 1; i++) {

                int waypointNodeId1 = waypoints.get(i).nodeId();
                int waypointNodeId2 = waypoints.get(i + 1).nodeId();

                Pair<Integer, Integer> segmentWaypoints = new Pair<>(
                        waypoints.get(i).nodeId(),
                        waypoints.get(i + 1).nodeId());

                if (memory.containsKey(segmentWaypoints)) {

                    segments.add(memory.get(segmentWaypoints));

                } else {
                    if (memory.size() == MAX_CAPACITY) {

                        Map.Entry<Pair<Integer, Integer>, Route> oldestMap = memory.entrySet().iterator().next();

                        memory.remove(oldestMap.getKey(), oldestMap.getValue());
                    }
                    if (waypointNodeId1 != waypointNodeId2) {

                        Route route = routeComputer.bestRouteBetween(waypointNodeId1, waypointNodeId2);

                        if (route == null) {

                            setRoute(null);
                            elevationProfile.set(null);
                            return;

                        }

                        memory.put(segmentWaypoints, route);
                        segments.add(route);
                    }
                    else {
                        setRoute(null);
                        elevationProfile.set(null);
                        return;
                    }
                }
            }
        else {

            setRoute(null);
            elevationProfile.set(null);
            return;
        }

        if(!segments.isEmpty()){
            MultiRoute route = new MultiRoute(segments);
            setRoute(route);
            elevationProfile.set(ElevationProfileComputer.elevationProfile(route, MAX_STEP_LENGTH));
        }
    }

    /**
     * getter for the route
     *
     * @return the route
     */
    public Route getRoute() {
        return this.route.get();
    }

    /**
     * allows us to set a Route (is a setter)
     *
     * @param route the route we would like to set
     */
    public void setRoute(Route route) {
        this.route.setValue(route);
    }

    /**
     * Contains the highlightedPosition in a DoubleProperty value
     *
     * @return the highlightedPosition
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * Getter of highlightedPosition
     *
     * @return the highlightedPosition
     */
    public double getHighlightedPosition() {
        return highlightedPosition.get();
    }

    /**
     * Getter of the object property containing the elevationProfile as an only readable object
     *
     * @return the elevationProfile
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {
        return elevationProfile;
    }

    /**
     * Getter of the elevationProfile
     *
     * @return the elevationProfile of the itinerary
     */
    public ElevationProfile getElevationProfile() {
        return elevationProfile.get();
    }


    /**
     * Gives us the index of the current non-empty segment on our route
     *
     * @param position the positions on the route
     * @return the index of our segment
     */
    public int indexOfNonEmptySegmentAt(double position) {

        int index = getRoute().indexOfSegmentAt(position);

        for (int i = 0; i <= index; i += 1) {

            int n1 = waypoints.get(i).nodeId();
            int n2 = waypoints.get(i + 1).nodeId();

            if (n1 == n2) index += 1;
        }
        return index;
    }
}


