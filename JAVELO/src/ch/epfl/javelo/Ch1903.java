package ch.epfl.javelo;

public class Ch1903 {


    public static double e(double longitude, double latitude) {
        double longitudeDefaut = 1e-4 * (3600*Math.toDegrees(longitude) - 26782.5);
        double latitudeDefaut = 1e-4 * (3600*Math.toDegrees(latitude) - 169028.66);
        double e = 2600072.37
                + 211455.93*longitudeDefaut
                - 10938.51*latitudeDefaut*longitudeDefaut
                - 0.36 * longitudeDefaut * latitudeDefaut * latitudeDefaut
                - 44.54*longitudeDefaut*longitudeDefaut*longitudeDefaut;
        return e;
    }

    public static double n(double longitude, double latitude){
        double longitudeDefaut = 1e-4 * (3600*Math.toDegrees(longitude) - 26782.5);
        double latitudeDefaut = 1e-4 * (3600*Math.toDegrees(latitude) - 169028.66);
        double n = 1200147.07
                + 308807.95 * latitudeDefaut
                + 3745.25*longitudeDefaut*longitudeDefaut
                + 76.63 * latitudeDefaut*latitudeDefaut
                - 194.56*latitudeDefaut*longitudeDefaut*longitudeDefaut
                + 119.79 * latitudeDefaut*latitudeDefaut*latitudeDefaut;
        return n;
    }

    public static double lon (double e, double n){
        double x = 1e-6 *(e-2600000);
        double y = 1e-6 *(n-1200000);
        double longitude0 = 2.6779094
                + 4.728982 * x
                + 0.791484 *x*y
                + 0.1306 *x*y*y
                - 0.0436 *x*x*x;
        double longitude = longitude0*100/36;
        return Math.toRadians(longitude);
    }

    public static double lat (double e, double n){
        double x = 1e-6 *(e-2600000);
        double y = 1e-6 *(n-1200000);
        double latitude0 = 16.9023892
                + 3.238272*y
                - 0.270978*x*x
                - 0.002528*y*y
                - 0.0447 *x*x*y
                -0.0140*y*y*y;
        double latitude = latitude0*100/36;
        return Math.toRadians(latitude);
    }
}