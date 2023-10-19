package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.Math2.clamp;


/**
 * Represents the table containing all 16348 sectors of JaVelo
 *
 * @param buffer the memory containing the attributes of the sectors
 * @author Noé Macé 342504
 * @author Mathis Richard 346419 *
 */
public record GraphSectors(ByteBuffer buffer) {

    public final static int OFFSET_NODE = 0;
    public final static int NUMBER_SECTOR_AXIS = 128;
    public final static int OFFSET_LENGTH = Integer.BYTES + OFFSET_NODE;
    public final static int OFFSET_SECTOR = OFFSET_LENGTH + Short.BYTES;

    /**
     * @param center   : center of the square of given point in Swiss coordinates
     * @param distance : shortest distance between the given point and the side od the square
     * @return the list of all sectors having an intersection with the square centered
     * at the given point and with a side equal to twice the given distance
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {


        double yMax = clamp(SwissBounds.MIN_N, center.n() + distance, SwissBounds.MAX_N);
        double yMin = clamp(SwissBounds.MIN_N, center.n() - distance, SwissBounds.MAX_N);
        double xMax = clamp(SwissBounds.MIN_E, center.e() + distance, SwissBounds.MAX_E);
        double xMin = clamp(SwissBounds.MIN_E, center.e() - distance, SwissBounds.MAX_E);

        int ySectorMax = (int) ((yMax - SwissBounds.MIN_N) / SwissBounds.HEIGHT * NUMBER_SECTOR_AXIS);
        int xSectorMax = (int) ((xMax - SwissBounds.MIN_E) / SwissBounds.WIDTH * NUMBER_SECTOR_AXIS);
        int xSectorMin = (int) ((xMin - SwissBounds.MIN_E) / SwissBounds.WIDTH * NUMBER_SECTOR_AXIS);
        int ySectorMin = (int) ((yMin - SwissBounds.MIN_N) / SwissBounds.HEIGHT * NUMBER_SECTOR_AXIS);

        List<Sector> sectorsInArea = new ArrayList<>();

        for (int x = xSectorMin; x <= xSectorMax; x++) {
            for (int y = ySectorMin; y <= ySectorMax; y++) {

                int index = y * NUMBER_SECTOR_AXIS + x;
                int startNodeId = buffer.getInt(index * OFFSET_SECTOR);

                sectorsInArea.add(new Sector(buffer.getInt(index * OFFSET_SECTOR),
                        startNodeId + Short.toUnsignedInt(buffer.getShort(index * OFFSET_SECTOR + OFFSET_LENGTH))));
            }
        }
        return sectorsInArea;
    }

    /**
     * Creates a new object representing Sectors with their startNode and endNode Ids
     */
    public record Sector(int startNodeId, int endNodeId) {
    }
}