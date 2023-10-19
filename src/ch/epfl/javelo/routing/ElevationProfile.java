package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;

/**
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

/**
 * represents the profile of an simple or multiple itinerary
 */
public final class ElevationProfile {

    private final DoubleSummaryStatistics s;
    private final double length;
    private final float[] elevationSamples;

    /**
     * Checks if there are more than 2 components to the profile
     *
     * @param length           the length of the profile
     * @param elevationSamples the float[] containing the samples
     */

    public ElevationProfile(double length, float[] elevationSamples) {

        Preconditions.checkArgument(elevationSamples.length >= 2 && length > 0);

        this.length = length;
        this.elevationSamples = new float[elevationSamples.length];

        System.arraycopy(elevationSamples, 0, this.elevationSamples, 0, elevationSamples.length);

        this.s = new DoubleSummaryStatistics();

        for (float elevationSample : elevationSamples) {
            s.accept(elevationSample);
        }
    }

    /**
     * @return the length of the ElevationProfile in meters
     */

    public double length() {
        return length;
    }

    /**
     * @return the minimum height of the elevationSamples
     */

    public double minElevation() {
        return s.getMin();
    }

    /**
     * @return the maximum height of the elevationSamples
     */

    public double maxElevation() {
        return s.getMax();
    }

    /**
     * @return the total of height increase of the elevationSamples
     */

    public double totalAscent() {
        double ascent = 0;
        for (int i = 0; i < elevationSamples.length - 1; i++) {
            if (elevationSamples[i + 1] - elevationSamples[i] > 0) {
                ascent += elevationSamples[i + 1] - elevationSamples[i];
            }
        }
        return ascent;
    }


    /**
     * @return the total of height decrease of the elevationSamples
     */

    public double totalDescent() {
        double descent = 0;
        for (int i = 0; i < elevationSamples.length - 1; i++) {
            if (elevationSamples[i + 1] - elevationSamples[i] < 0) {
                descent -= elevationSamples[i + 1] - elevationSamples[i];
            }
        }
        return descent;
    }


    /**
     * @param position the position wanted
     * @return the height of the point at the given position on the edge
     */

    public double elevationAt(double position) {
        return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    }
}
