package app.taxipizza.models;



public class Rating {
    private String userPhone;
    private String foodId;
    private String rateValue;
    private String comment;
    private String timeStamp;


    public Rating() {
    }

    public Rating(String userPhone, String foodId, String rateValue, String comment, String timestamp) {
        this.userPhone = userPhone;
        this.foodId = foodId;
        this.rateValue = rateValue;
        this.comment = comment;
        this.timeStamp = timestamp;
    }

    public String getUserPhone() {
        return userPhone;
    }
    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
