package ch.epfl.javelo.projection;


/**
 * This class allows us to convert WGS84 coordinates to E and N coordinates and vice versa
 *
 * @author Noé Macé 342504
 * @author Mathis Richard 346419
 */

public final class Ch1903 {


    private Ch1903() {
    }

    /**
     * @param longitude the longitude in radians
     * @param latitude  the latitude in radians
     * @return the E coordinate given the latitude and the longitude, in WGS84 system
     */
    public static double e(double longitude, double latitude) {
        double longitudeDefault = 1e-4 * (3600 * Math.toDegrees(longitude) - 26782.5);
        double latitudeDefault = 1e-4 * (3600 * Math.toDegrees(latitude) - 169028.66);
        double e = 2600072.37
                + 211455.93 * longitudeDefault
                - 10938.51 * latitudeDefault * longitudeDefault
                - 0.36 * longitudeDefault * latitudeDefault * latitudeDefault
                - 44.54 * longitudeDefault * longitudeDefault * longitudeDefault;
        return e;
    }

    /**
     * @param longitude the latitude in radians
     * @param latitude  the longitude in radians
     * @return the N coordinate in WGS84 system
     */
    public static double n(double longitude, double latitude) {
        double longitudeDefault = 1e-4 * (3600 * Math.toDegrees(longitude) - 26782.5);
        double latitudeDefault = 1e-4 * (3600 * Math.toDegrees(latitude) - 169028.66);
        double n = 1200147.07
                + 308807.95 * latitudeDefault
                + 3745.25 * longitudeDefault * longitudeDefault
                + 76.63 * latitudeDefault * latitudeDefault
                - 194.56 * latitudeDefault * longitudeDefault * longitudeDefault
                + 119.79 * latitudeDefault * latitudeDefault * latitudeDefault;
        return n;
    }

    /**
     * @param e the east coordinate in WGS84 system
     * @param n the north coordinate in WGS84 system
     * @return the longitude given E and N, in radians
     */
    public static double lon(double e, double n) {
        double x = 1e-6 * (e - 2600000);
        double y = 1e-6 * (n - 1200000);
        double longitude0 = 2.6779094
                + 4.728982 * x
                + 0.791484 * x * y
                + 0.1306 * x * y * y
                - 0.0436 * x * x * x;
        double longitude = longitude0 * 100 / 36;
        return Math.toRadians(longitude);
    }

    /**
     * @param e the east coordinate in WGS84 system
     * @param n the north coordinate in WGS84 system
     * @return the latitude in radians
     */
    public static double lat(double e, double n) {
        double x = 1e-6 * (e - 2600000);
        double y = 1e-6 * (n - 1200000);
        double latitude0 = 16.9023892
                + 3.238272 * y
                - 0.270978 * x * x
                - 0.002528 * y * y
                - 0.0447 * x * x * y
                - 0.0140 * y * y * y;
        double latitude = latitude0 * 100 / 36;
        return Math.toRadians(latitude);
    }
}