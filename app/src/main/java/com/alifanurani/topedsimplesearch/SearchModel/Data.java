package com.alifanurani.topedsimplesearch.SearchModel;

/**
 * Created by USER on 9/14/2016.
 */
public class Data {

    private long id;

    private String name;

    private String image_uri;

    private String price;

    public Data(String name, String image_uri, String price) {
        this.name = name;
        this.image_uri = image_uri;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image_uri='" + image_uri + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
