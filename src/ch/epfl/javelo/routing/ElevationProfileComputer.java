package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Allows us to calculate the entire profile of an itinerary
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

public final class ElevationProfileComputer {

    /**
     * empty constructor
     */
    private ElevationProfileComputer() {
    }


    /**
     * @param route         the itinerary
     * @param maxStepLength the maximum step length between points in meters
     * @return the profile of the itinerary "route" with a guaranty that the spacing between each sample of the profile
     * is at maximum: MaxStepLength meters
     */

    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {

        Preconditions.checkArgument(maxStepLength > 0);

        int numberSample = (int) Math.ceil(route.length() / maxStepLength) + 1;
        double lengthSpace = route.length() / (numberSample - 1);

        float[] profilSamples = new float[numberSample];

        for (int i = 0; i < profilSamples.length; i++) {
            profilSamples[i] = (float) route.elevationAt(i * lengthSpace);
        }

        ArrayList<Integer> indexesNan = new ArrayList<>();

        boolean empty = true;

        int lastNumber = -1;
        int firstNumber = -1;

        for (int i = 0; i < profilSamples.length; i++) {
            if (!((Float) profilSamples[i]).isNaN()) {
                if (empty) {
                    empty = false;
                    firstNumber = i;
                }
                lastNumber = i;
            }
        }

        if (empty) {
            return new ElevationProfile(route.length(), new float[numberSample]);
        }

        Arrays.fill(profilSamples, 0, firstNumber, profilSamples[firstNumber]);
        Arrays.fill(profilSamples, lastNumber, profilSamples.length, profilSamples[lastNumber]);


        for (int i = 0; i < profilSamples.length; i++) {
            if ((i == 0)
                    && (!Float.isNaN(profilSamples[0]))
                    && (Float.isNaN(profilSamples[1]))) {

                indexesNan.add(0);
            }

            if ((i == profilSamples.length - 1)
                    && (!Float.isNaN(profilSamples[i]))
                    && (Float.isNaN(profilSamples[i - 1]))) {

                indexesNan.add(i);
            }

            if ((i > 0)
                    && (i < profilSamples.length - 1)
                    && (!Float.isNaN(profilSamples[i]))) {

                if ((Float.isNaN(profilSamples[i - 1]))
                        && (Float.isNaN(profilSamples[i + 1]))) {

                    indexesNan.add(i);
                    indexesNan.add(i);

                } else if ((Float.isNaN(profilSamples[i + 1])
                        || Float.isNaN(profilSamples[i - 1]))) {

                    indexesNan.add(i);
                }
            }
        }


        for (int i = 0; i < (indexesNan.size() / 2); i++) {

            int length = indexesNan.get(2 * i + 1) - indexesNan.get(2 * i);

            for (int j = 1; j < length; j++) {
                profilSamples[indexesNan.get(2 * i) + j] =
                        (float) Math2.interpolate(profilSamples[indexesNan.get(2 * i)],
                                profilSamples[indexesNan.get((2 * i) + 1)],
                                (float) j / length);
            }
        }
        return new ElevationProfile(route.length(), profilSamples);
    }
}

