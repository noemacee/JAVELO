package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * The record containing the information of what the Waypoints are made of
 *
 * @author NoeMace 341504
 * @author Mathis Richard 346419
 */
public final record Waypoint(PointCh swiss, int nodeId) {
}
