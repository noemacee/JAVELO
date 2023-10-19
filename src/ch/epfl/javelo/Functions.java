package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * allows us to create methods that create objects representing functions from real number to real number
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public final class Functions {


    private Functions() {
    }

    /**
     * @param samples the samples of the itinerary
     * @param xMax    the length, going from 0 to xMax
     * @return a DoubleUnaryOperator of sample
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {

        Preconditions.checkArgument(samples.length >= 2 && xMax > 0);

        return new Sampled(samples, xMax);
    }

    /**
     * @param y : a double value
     * @return an instance of Constant, a function that is always equal to y
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }


    private record Constant(double y) implements DoubleUnaryOperator {

        /**
         * apply as double representing the return value of a function
         *
         * @param y x value of f(x)
         * @return the constant value y
         */
        public double applyAsDouble(double y) {
            return this.y;
        }


    }


    private static final class Sampled implements DoubleUnaryOperator {

        private final float[] samples;
        private final double xMax;

        /**
         * Setter
         *
         * @param samples the table containing the samples
         * @param xMax
         */


        public Sampled(float[] samples, double xMax) {

            this.samples = samples;
            this.xMax = xMax;
        }

        /**
         * We find the y for a given x on the interval [0;xMax]
         * While doing the interpolate method, since it's only doing calculations on [0;1],
         * we have to proportionally adjust the straight line connecting both dots, thus we can find y corresponding
         * to x, considering we have 0<x<xMax, we need to remove the integer in order to have x between [0;1]
         *
         * @param x the starting x
         * @return the new X
         */
        @Override
        public double applyAsDouble(double x) {

            double interval = xMax / (samples.length - 1);
            int intervalMin = (int) (x / interval);

            if (x < 0) {
                return samples[0];
            }
            if (x >= xMax) {
                return samples[samples.length - 1];
            }
            return Math2.interpolate(samples[intervalMin],
                    samples[Math2.clamp(0,
                            intervalMin + 1,
                            samples.length - 1)],
                    ((x - intervalMin * interval) / interval));

        }
    }
}
