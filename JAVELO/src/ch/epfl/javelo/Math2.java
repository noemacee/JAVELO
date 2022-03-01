package ch.epfl.javelo;

public class Math2 {
    private Math2(){}

    public static int ceilDiv(int x, int y){
        Preconditions.checkArgument(x < 0 || y <= 0);

        return (x+y-1)/y;
    }

    public static double interpolate(double y0, double y1, double x){
        return Math.fma((y1-y0),x,y0);
    }

    public static int clamp(int min, int v, int max){
        Preconditions.checkArgument(min <= max);

        if (v < min){
            return min;
        }
        else return Math.min(v, max);
    }

    public static double clamp(double min, double v, double max){
        Preconditions.checkArgument(min <= max);

        if (v < min){
            return min;
        }
        else return Math.min(v, max);
    }

    public static double asinh(double x){
        return Math.log(x + Math.sqrt(1+(x*x)));
    }

    public static double dotProduct(double uX, double uY, double vX, double vY){
        return uX*vX + uY*vY;
    }

    public static double squaredNorm(double uX, double uY){
        return uX*uX + uY*uY;
    }

    public static double norm(double uX, double uY){
        return Math.sqrt(uX*uX + uY*uY);
    }

    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY){
        double uX = pX - aX;
        double uY = pY - aY;
        double vX = bX - aX;
        double vY = bY - aY;
        return (dotProduct(uX,uY,vX,vY) / norm(vX,vY));
    }





}
