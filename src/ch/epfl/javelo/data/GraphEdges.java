package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static java.lang.Short.toUnsignedInt;


/**
 * Represents the table with all the edges of the graph
 *
 * @param edgesBuffer contains the attributes of the edges
 * @param profileIds  contains the attributes attached to the profile
 * @param elevations  contains the samples of the profile compressed or not
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    /**
     * @param edgeId the identity of a given edge
     * @return if the edge is inverted
     */
    public boolean isInverted(int edgeId) {
        return edgesBuffer.get(edgeId * 10) < 0;
    }

    /**
     * @param edgeId the identity of a given edge
     * @return the index
     */
    public int targetNodeId(int edgeId) {
        int edgesBufferInt = edgesBuffer.getInt(edgeId * 10);
        return isInverted(edgeId) ? ~edgesBufferInt : edgesBufferInt;
    }

    /**
     * @param edgeId the identity of a given edge
     * @return the length of the edge
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.
                getShort(edgeId * 10 + 4)));
    }

    /**
     * @param edgeId the identity of a given edge
     * @return the elevation gain from the edge
     */
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(edgeId * 10 + 6)));
    }

    /**
     * @param edgeId the identity of a given edge
     * @return if the edge has a profile
     */
    public boolean hasProfile(int edgeId) {

        int profile = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);

        return profile != 0;
    }

    /**
     * @param edgeId the identity of a given edge
     * @return the float[] containing the profile for a given edge
     */
    public float[] profileSamples(int edgeId) {

        int profile = Bits.extractUnsigned(profileIds.get(edgeId), 30, 2);

        if (hasProfile(edgeId)) {

            int numberSamples = 1 + Math2.ceilDiv(toUnsignedInt(edgesBuffer.getShort(10 * edgeId + 4)),
                    Q28_4.ofInt(2));
            float[] ProfileSamples = new float[numberSamples];
            int firstIndex = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
            if (profile == 2) {
                ProfileSamples[0] = Q28_4.asFloat(toUnsignedInt(elevations.get(firstIndex)));
                for (int i = 1; i < numberSamples; i += 2) {

                    ProfileSamples[i] = profileExtractor(edgeId, profile, i, i, 8, 8, ProfileSamples);

                    if (numberSamples > i + 1) {

                        ProfileSamples[i + 1] = profileExtractor(edgeId,
                                profile,
                                i + 1,
                                i,
                                0,
                                8,
                                ProfileSamples);
                    }
                }

            } else if (profile == 3) {
                ProfileSamples[0] = Math.scalb(toUnsignedInt(elevations.get(firstIndex)), -4);
                for (int i = 1; i < numberSamples; i += 4) {

                    ProfileSamples[i] = profileExtractor(edgeId, profile, i, i, 12, 4, ProfileSamples);

                    if (numberSamples > i + 1) {

                        ProfileSamples[i + 1] = profileExtractor(edgeId,
                                profile,
                                i + 1,
                                i,
                                8,
                                4,
                                ProfileSamples);

                        if (numberSamples > 2 + i) {

                            ProfileSamples[i + 2] = profileExtractor(edgeId,
                                    profile,
                                    i + 2,
                                    i,
                                    4,
                                    4,
                                    ProfileSamples);

                            if (numberSamples > i + 3) {

                                ProfileSamples[i + 3] = profileExtractor(edgeId,
                                        profile,
                                        i + 3,
                                        i,
                                        0,
                                        4,
                                        ProfileSamples);
                            }
                        }
                    }
                }
            }
            if (profile == 1) {
                for (int i = 0; i < numberSamples; i++) {
                    ProfileSamples[i] = Q28_4.asFloat(toUnsignedInt(elevations.get(i + firstIndex)));
                }
            }

            if (isInverted(edgeId)) {
                return invert(ProfileSamples);
            }

            return ProfileSamples;

        } else {
            return new float[0];
        }
    }

    public float profileExtractor(int edgeId, int profile, int index, int i,
                                  int start, int length, float[] ProfileSamples) {

        int firstIndex = Bits.extractUnsigned(profileIds.get(edgeId), 0, 30);
        int offset = 3;
        int division = 4;

        if (profile == 2) {
            division = 2;
            offset = 1;
        }

        float profileExtractor = ProfileSamples[index - 1] +
                Q28_4.asFloat(Bits.extractSigned(elevations.get((i + offset) / division + firstIndex),
                        start, length));
        return profileExtractor;
    }

    /**
     * Inverts a table of floats
     *
     * @param samples is the float table containing the information on each point/sample
     * @return the invert of the table
     */
    public float[] invert(float[] samples) {

        float[] invert = new float[samples.length];

        for (int i = 0; i < samples.length; i++) {
            invert[i] = samples[samples.length - 1 - i];
        }
        return invert;
    }


    /**
     * @param edgeId the identity of the given edge
     * @return the index of the attribute
     */
    public int attributesIndex(int edgeId) {
        return toUnsignedInt(edgesBuffer.getShort(edgeId * 10 + 8));
    }

    public ByteBuffer edgesBuffer() {
        return null;
    }


}