package app.taxipizza.models;


import java.util.List;

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String Thumb_image;
    private List<Order> Cart;
    private LatLngLocation deliveryAddress;
    private boolean notificationsEnabled;

    public User(String name, String password, String phone, String thumb_image, List<Order> cart, LatLngLocation deliveryAddress, boolean notificationsEnabled) {
        Name = name;
        Password = password;
        Phone = phone;
        Thumb_image = thumb_image;
        Cart = cart;
        this.deliveryAddress = deliveryAddress;
        this.notificationsEnabled = notificationsEnabled;
    }
    public User() {

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

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getThumb_image() {
        return Thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        Thumb_image = thumb_image;
    }

    public List<Order> getCart() {
        return Cart;
    }

    public void setCart(List<Order> cart) {
        Cart = cart;
    }

    public LatLngLocation getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(LatLngLocation deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}
