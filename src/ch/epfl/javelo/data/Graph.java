package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;


/**
 * Represents the JaVelo Graph
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

public final class Graph {
    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * Creates a "Graph" with the given nodes, the sectors, the edges and the attributeSets
     *
     * @param nodes         the nodes of the graph
     * @param sectors       of the graph
     * @param edges         of the graph
     * @param attributeSets given from the Graph
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * @param basePath the basepath we load our graph from
     * @return the JaVelo graph obtained through the files in "basePath"
     * @throws IOException
     */
    public static Graph loadFrom(Path basePath) throws IOException {

        GraphNodes node = new GraphNodes(tryBuffer("nodes.bin", basePath).asIntBuffer());
        GraphSectors sector = new GraphSectors(tryBuffer("sectors.bin", basePath));
        GraphEdges edges = new GraphEdges(tryBuffer("edges.bin", basePath),
                tryBuffer("profile_ids.bin", basePath).asIntBuffer(),
                tryBuffer("elevations.bin", basePath).asShortBuffer());
        LongBuffer attributesBuffer = tryBuffer("attributes.bin", basePath).asLongBuffer();

        List<AttributeSet> attributeSets = new ArrayList<>();


        for (int i = 0; i < attributesBuffer.capacity(); i++) {
            attributeSets.add(new AttributeSet(attributesBuffer.get(i)));
        }

        return new Graph(node, sector, edges, attributeSets);

    }

    /**
     * In order to avoid code copying, we create this method which returns the buffer of a certain pathString given
     *
     * @param pathString : the name of the path
     * @param basePath   : basePath of loadFrom
     * @return ByteBuffer which could be changed depending on the real buffers needed
     * @throws IOException if there is no file to open inside the projet named pathString
     */

    private static ByteBuffer tryBuffer(String pathString, Path basePath) throws IOException {
        Path path = basePath.resolve(pathString);
        ByteBuffer buffer;

        try (FileChannel channel = FileChannel.open(path)) {
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        return buffer;
    }

    /**
     * @return a count of the nodes in the graph
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * @param nodeId the identity of a given node
     * @return the position of the given node
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * @param nodeId the identity of a given node
     * @return the number of edges the node is connected to
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * @param nodeId    the identity of a given node
     * @param edgeIndex : the edgeIndex th edge going out from the node
     * @return the identity of the edgeIndex-Edge leaving from the node
     */
    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * @param point          a given point
     * @param searchDistance the maximum search distance
     * @return the node closest to our given position
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {

        List<GraphSectors.Sector> sectorsInArea = sectors.sectorsInArea(point, searchDistance);
        double distanceMin = searchDistance * searchDistance;

        int idClosestNode = -1;
        for (GraphSectors.Sector sector : sectorsInArea) {
            for (int j = sector.startNodeId(); j < sector.endNodeId(); j++) {

                double squaredDistance = point.squaredDistanceTo(nodePoint(j));

                if (squaredDistance <= distanceMin) {
                    idClosestNode = j;
                    distanceMin = squaredDistance;
                }
            }
        }
        return idClosestNode;

    }

    /**
     * @param edgeId the identity of a given edge
     * @return the identity of the destination node of the given edge we want to reach
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * @param edgeId the identity of a given edge
     * @return whether the edge is inverted or not
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * @param edgeId the identity of a given edge
     * @return the length of the edge
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * @param edgeId the identity of a given edge
     * @return the elevation gained throughout the edge
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * @param edgeId the identity of a given edge
     * @return the profile of the edge or Double_NaN if the edge does not have a profile
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        return edges.hasProfile(edgeId)
                ? Functions.sampled(edges.profileSamples(edgeId), edgeLength(edgeId))
                : Functions.constant(Double.NaN);
    }

    /**
     * @param edgeId the identity of a given edge
     * @return the OSM attributes attached to the edge
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

}
