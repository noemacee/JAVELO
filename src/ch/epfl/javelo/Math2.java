package ch.epfl.javelo;


/**
 * This class is similar to the Math class, it allows us to do plenty of mathematical operations
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */
public final class Math2 {


    private Math2() {
    }

    /**
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the entire part by excess from the division from x by y
     * @throws IllegalArgumentException if x < 0 or y <= 0
     */
    public static int ceilDiv(int x, int y) {

        Preconditions.checkArgument(x >= 0 && y > 0);

        return (x + y - 1) / y;
    }

    /**
     * @param y0 the coordinate y at x=0
     * @param y1 the coordinate y at x=1
     * @param x  the x want to find y at
     * @return the y of the point on the segment going from (0,y0) and (1,y1) with x given
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma((y1 - y0), x, y0);
    }

    /**
     * @param min the minimum of the interval
     * @param v   a value/position given
     * @param max the maximum of the interval
     * @return limits the interval to [min;max], returns min if v<max, max if v>max,
     * @throws IllegalArgumentException if min > max
     */
    public static int clamp(int min, int v, int max) {

        Preconditions.checkArgument(min <= max);
        return (v < min) ? min : Math.min(v, max);

    }

    /**
     * @param min the minimum of the interval
     * @param v   a value/position given
     * @param max the maximum of the interval
     * @return limits the interval to [min;max], returns min if v<max, max if v>max,
     * else throws an IllegalArgumentException
     * @throws IllegalArgumentException if min > max
     */
    public static double clamp(double min, double v, double max) {

        Preconditions.checkArgument(min <= max);
        return (v < min) ? min : Math.min(v, max);
    }

    /**
     * @param x the coordinate x
     * @return the inverted hyperbolic sinus of x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.hypot(1, x));
    }

    /**
     * retourne le produit scalaire entre le vecteur u et le vecteur v
     *
     * @param uX the coordinate X from u
     * @param uY the coordinate Y from u
     * @param vX the coordinate X from v
     * @param vY the coordinate Y from v
     * @return the scalar product between u and v
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return uX * vX + uY * vY;
    }

    /**
     * @param uX the coordinate x of the vector
     * @param uY the coordinate y of the vector
     * @return the square of the norm of the vector U
     */
    public static double squaredNorm(double uX, double uY) {
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * @param uX the coordinate x of the vector
     * @param uY the coordinate y of the vector
     * @return the norm of the vector u
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX, uY));
    }

    /**
     * @param aX the coordinate X from the point A
     * @param aY the coordinate Y from the point A
     * @param bX the coordinate X from the point B
     * @param bY the coordinate Y from the point B
     * @param pX the coordinate X from the point P
     * @param pY the coordinate Y from the point P
     * @return the projected point P on the vector AB
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        double uX = pX - aX;
        double uY = pY - aY;
        double vX = bX - aX;
        double vY = bY - aY;
        return (dotProduct(uX, uY, vX, vY) / norm(vX, vY));
    }
}
