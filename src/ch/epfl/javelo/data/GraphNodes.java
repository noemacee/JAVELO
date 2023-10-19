package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Q28_4;

import java.nio.IntBuffer;


/**
 * Represents the table of all the nodes of the Graph
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public record GraphNodes(IntBuffer buffer) {

    /**
     * the offset of the e coordinate
     */
    private static final int OFFSET_E = 0;

    /**
     * the offset of the n coordinate
     */
    private static final int OFFSET_N = OFFSET_E + 1;

    /**
     * the offset of the outer edges
     */
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;

    /**
     *
     */
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    /**
     * starting point of the extraction for "outDegree"
     */
    private static final int FIRST_INDEX_EXTRACTION_OUTDEGREE = 28;

    /**
     * length of the extraction for the "outDegree"
     */
    private static final int LENGTH_EXTRACTION_OUTDEGREE = 4;

    /**
     * starting point of the extraction
     */
    private static final int FIRST_INDEX_EXTRACTION_EDGEID = 0;

    /**
     * length of the extraction for the edgeId
     */
    private static final int LENGTH_EXTRACTION_EDGEID = 28;

    /**
     * @return counts the number of nodes
     */
    public int count() {
        return buffer.capacity() / NODE_INTS;
    }


    /**
     * @param nodeId the identity of a given node
     * @returnreturns the "E" coordinate of the given node
     */
    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get((nodeId) * NODE_INTS + OFFSET_E));
    }


    /**
     * @param nodeId the identity of a given node
     * @returnreturns the "N" coordinate of the given node
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get((nodeId) * NODE_INTS + OFFSET_N));
    }


    /**
     * @param nodeId the identity of a given node
     * @return the number of edges leaving the node
     */
    public int outDegree(int nodeId) {
        return Bits.extractUnsigned(buffer.get((nodeId) * NODE_INTS + OFFSET_OUT_EDGES),
                FIRST_INDEX_EXTRACTION_OUTDEGREE,
                LENGTH_EXTRACTION_OUTDEGREE);
    }


    /**
     * @param nodeId    the identity of a given node
     * @param edgeIndex the index of the edge on the node
     * @return the identity of the edgeIndex-Path leaving from the given node
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return Bits.extractUnsigned(buffer.get((nodeId) * NODE_INTS + OFFSET_OUT_EDGES),
                FIRST_INDEX_EXTRACTION_EDGEID,
                LENGTH_EXTRACTION_EDGEID)
                + edgeIndex;
    }

}