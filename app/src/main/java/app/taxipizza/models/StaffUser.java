package app.taxipizza.models;

/**
 * Created by user on 06/03/2018.
 */

public class StaffUser {
    private String Name;
    private String Password;
    private String latitude;
    private String longitude;
    private String currentOrder;

    public StaffUser() {
    }

    public StaffUser(String name, String password, String latitude, String longitude, String currentOrder) {
        Name = name;
        Password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentOrder = currentOrder;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(String currentOrder) {
        this.currentOrder = currentOrder;
    }
}
