package app.taxipizza.models;

/**
 * Created by user on 01/01/2018.
 */

public class Menus {
    private String Name;
    private String Image;

    public Menus(String name, String image) {
        Name = name;
        Image = image;
    }

    public Menus() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
