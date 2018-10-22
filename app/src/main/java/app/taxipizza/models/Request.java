package app.taxipizza.models;

import java.util.List;

public class Request {

    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private String timeStamp;
    private List<Order> orders;
    private String deliveryLocation;

    public Request() {
    }

    public Request(String phone, String name, String total, String timeStamp, List<Order> orders, String deliveryLocation) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.timeStamp = timeStamp;
        this.deliveryLocation = deliveryLocation;
        this.status = "0";
        this.orders = orders;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

}
