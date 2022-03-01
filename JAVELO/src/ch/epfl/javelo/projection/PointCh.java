package ch.epfl.javelo.projection;
import ch.epfl.javelo.Ch1903;
import ch.epfl.javelo.Preconditions;

import static ch.epfl.javelo.projection.SwissBounds.containsEN;

public record PointCh (double e, double n){
    public PointCh {  // constructeur compact
        Preconditions.checkArgument(containsEN(e,n));
    }

    public double squaredDistanceTo(PointCh that){
        return (that.e - this.e)*(that.e - this.e) + (that.n - this.n)*(that.n - this.n);
    }

    public double distanceTo(PointCh that){
        return Math.sqrt(squaredDistanceTo(that));
    }

    public double lon(){
        return Ch1903.lon(e,n);
    }

    public double lat(){
        return Ch1903.lat(e,n);
    }
}
