package ch.epfl.javelo.projection;

public class SwissBounds {

    private SwissBounds(){}

    final static public double MIN_E = 2485000;
    final static public double MAX_E = 2834000;
    final static public double MIN_N = 1075000;
    final static public double MAX_N = 1296000;
    final static public double WIDTH = MAX_E - MIN_E;
    final static public double HEIGHT = MAX_N - MIN_N;

    public static boolean containsEN (double e, double n){
        if ((e < MAX_E)&&(e> MIN_E)){
            return (n < MAX_N) && (n > MIN_N);
        } else {
            return false;
        }
    }
}