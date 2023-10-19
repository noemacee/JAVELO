package ch.epfl.javelo.routing;

/**
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

/**
 * It is a function representing the cost of an itinerary
 */
public interface CostFunction {

    /**
     * @param nodeId the identity of the node
     * @param edgeId the identity of the edge
     * @return the factor by which the length  between both nodes needs to be multiplied (>= 1)
     */
    double costFactor(int nodeId, int edgeId);
}
    