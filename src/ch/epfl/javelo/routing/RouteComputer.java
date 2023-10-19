package ch.epfl.javelo.routing;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;


/**
 * Represents an itinerary planner
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

public class RouteComputer {

    private static final float UNNECESSARY_NODE = Float.NEGATIVE_INFINITY;

    public final Graph graph;
    public final CostFunction costFunction;


    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }


    /**
     * Calculates the best itinerary a bike could follow between a starting node and an end node
     * record "WeightedNode" contained in it
     *
     * @param startNodeId : Start node ID
     * @param endNodeId   : End node ID
     * @return a Route representing the best itinerary possible to follow
     */

    public Route bestRouteBetween(int startNodeId, int endNodeId) {

        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        Preconditions.checkArgument(startNodeId != endNodeId);

        PointCh endNodePoint = graph.nodePoint(endNodeId);
        int counter = graph.nodeCount();

        float[] distance = new float[counter];
        int[] predecessor = new int[counter];

        Arrays.fill(distance, (float) Double.POSITIVE_INFINITY);

        distance[startNodeId] = 0;

        PriorityQueue<WeightedNode> exploring = new PriorityQueue<>();
        exploring.add(new WeightedNode(startNodeId, 0));

        boolean reach = false;

        do {
            assert exploring.peek() != null;

            int distanceMinNodeId = exploring.remove().nodeId;


            if (distance[distanceMinNodeId] >= 0) {
                if (endNodeId == distanceMinNodeId) {
                    reach = true;
                    break;
                }


                for (int i = 0; i < graph.nodeOutDegree(distanceMinNodeId); i++) {

                    int edgeId = graph.nodeOutEdgeId(distanceMinNodeId, i);
                    int Nprime = graph.edgeTargetNodeId(edgeId);

                    PointCh pointNp = graph.nodePoint(Nprime);

                    float d = distance[distanceMinNodeId]
                            + (float) (graph.edgeLength(edgeId)
                            * costFunction.costFactor(distanceMinNodeId, edgeId));

                    if (d < distance[Nprime]) {
                        distance[Nprime] = d;
                        predecessor[Nprime] = i << 28 | distanceMinNodeId;

                        exploring.add(new WeightedNode(Nprime,
                                distance[Nprime]
                                        + (float) endNodePoint.distanceTo(pointNp)));
                    }


                }
                distance[distanceMinNodeId] = (int) UNNECESSARY_NODE;
            }


        } while (!exploring.isEmpty());


        if (reach) {

            Deque<Edge> edges = new ArrayDeque<>();
            int N = endNodeId;

            do {
                int nodeId = Bits.extractUnsigned(predecessor[N], 0, 28);
                edges.addFirst(Edge.of(graph,
                        graph.nodeOutEdgeId(nodeId,
                                Bits.extractUnsigned(predecessor[N],
                                        28,
                                        4)),
                        nodeId,
                        N));

                N = nodeId;

            } while (N != startNodeId);


            return new SingleRoute(new ArrayList<>(edges));
        }
        return null;
    }
}
