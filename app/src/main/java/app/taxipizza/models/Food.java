package app.taxipizza.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Food implements Parcelable {
    private String Name;
    private String ImageOne;
    private String ImageTwo;
    private String ImageThree;
    private String Description;
    private String Price;
    private String Discount;
    private String MenuId;
    private String Estimated;
    private String Ingredients;

    public Food(String name, String imageOne, String imageTwo, String imageThree, String description, String price, String discount, String menuId, String estimated, String ingredients) {
        Name = name;
        ImageOne = imageOne;
        ImageTwo = imageTwo;
        ImageThree = imageThree;
        Description = description;
        Price = price;
        Discount = discount;
        MenuId = menuId;
        Estimated = estimated;
        Ingredients = ingredients;
    }

    public Food() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImageOne() {
        return ImageOne;
    }

    public void setImageOne(String imageOne) {
        ImageOne = imageOne;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getEstimated() {
        return Estimated;
    }

    public void setEstimated(String estimated) {
        Estimated = estimated;
    }

    public String getIngredients() {
        return Ingredients;
    }

    public void setIngredients(String ingredients) {
        Ingredients = ingredients;
    }

    protected Food(Parcel in) {
        Name = in.readString();
        ImageOne = in.readString();
        ImageTwo = in.readString();
        ImageThree = in.readString();
        Description = in.readString();
        Price = in.readString();
        Discount = in.readString();
        MenuId = in.readString();
        Estimated = in.readString();
        Ingredients = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Name);
        dest.writeString(this.ImageOne);
        dest.writeString(this.ImageTwo);
        dest.writeString(this.ImageThree);
        dest.writeString(this.Description);
        dest.writeString(this.Price);
        dest.writeString(this.Discount);
        dest.writeString(this.MenuId);
        dest.writeString(this.Estimated);
        dest.writeString(this.Ingredients);
    }

    public String getImageTwo() {
        return ImageTwo;
    }

    public void setImageTwo(String imageTwo) {
        ImageTwo = imageTwo;
    }

    public String getImageThree() {
        return ImageThree;
    }

    public void setImageThree(String imageThree) {
        ImageThree = imageThree;
    }
}
