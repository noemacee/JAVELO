package ch.epfl.javelo.projection;


/**
 * This class contains a method relative to the swiss limits/borders
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public final class SwissBounds {


    public static final double MIN_E = 2485000;
    public static final double MAX_E = 2834000;
    public static final double MIN_N = 1075000;
    public static final double MAX_N = 1296000;
    public static final double WIDTH = MAX_E - MIN_E;
    public static final double HEIGHT = MAX_N - MIN_N;

    /**
     * empty constructor
     */
    private SwissBounds() {
    }

    /**
     * @param e the east coordinate
     * @param n the north coordinate
     * @return whether E and N are located in Switzerland
     */
    public static boolean containsEN(double e, double n) {
        return (e <= MAX_E) && (e >= MIN_E) && (n <= MAX_N) && (n >= MIN_N);
    }
}