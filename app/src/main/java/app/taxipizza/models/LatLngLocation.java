package app.taxipizza.models;

/**
 * Created by user on 28/02/2018.
 */

public class LatLngLocation {

    public double longitude;
    public double latitude;

    public LatLngLocation(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public LatLngLocation() {
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
