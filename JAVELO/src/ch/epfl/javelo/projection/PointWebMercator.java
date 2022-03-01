package ch.epfl.javelo.projection;

import ch.epfl.javelo.Ch1903;
import ch.epfl.javelo.Preconditions;

import static ch.epfl.javelo.projection.SwissBounds.containsEN;

public record PointWebMercator(double x, double y) {
    public PointWebMercator {  // constructeur compact
        Preconditions.checkArgument(x <= 1 && x >= 0 && y <= 1 && y >= 0);
    }

    public static PointWebMercator of(int zoomLevel, double x, double y){
        return new PointWebMercator(Math.scalb(x,-8-zoomLevel), Math.scalb(y,-8-zoomLevel));
    }

    public static PointWebMercator ofPointCh(PointCh pointCh){
        return of(1, WebMercator.x(pointCh.lon()),WebMercator.y(pointCh.lat()));
    }

    public double xAtZoomLevel(int zoomLevel){
        return Math.scalb(x,8+zoomLevel);
    }

    public double yAtZoomLevel(int zoomLevel){
        return Math.scalb(y,8+zoomLevel);
    }

    public double lon(){
       return WebMercator.lon(x);
    }

    public double lat(){
        return WebMercator.lat(y);
    }

    public PointCh toPointCh(){
        if (SwissBounds.containsEN(Ch1903.e(lon(),lat()),Ch1903.n(lon(),lat()))){
            return new PointCh(Ch1903.e(lon(),lat()),Ch1903.n(lon(),lat()));
        }
        return null;
    }
}
