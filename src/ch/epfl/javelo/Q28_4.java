package ch.epfl.javelo;


/**
 * converts numbers in Q28.4 to other notations
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public final class Q28_4 {
    private static final int DECIMAL_PLACES_NUMBER = 4;


    /**
     * empty constructor
     */
    private Q28_4() {
    }

    /**
     * @param i a value in 32 bits
     * @return i in the Q28_4 notation
     */
    public static int ofInt(int i) {
        return i << DECIMAL_PLACES_NUMBER;
    }

    /**
     * @param q28_4 the value in Q28_4
     * @return converts the int as a double
     */
    public static double asDouble(int q28_4) {
        return Math.scalb((double) q28_4, -DECIMAL_PLACES_NUMBER);
    }

    /**
     * @param q28_4 the value in Q28_4
     * @return the value as a float
     */
    public static float asFloat(int q28_4) {
        return Math.scalb(q28_4, -DECIMAL_PLACES_NUMBER);
    }
}
