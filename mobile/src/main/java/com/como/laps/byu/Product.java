package com.como.laps.byu;

/**
 * Created by Antonello on 11/07/15.
 */
public class Product {

    private String id;
    private String name;
    private String photo;
    private Float price;
    private boolean deliverable;

    public Product(String id, String name, String photo, Float price, boolean deliverable) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.price = price;
        this.deliverable = deliverable;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public Float getPrice() {
        return price;
    }

    public boolean isDeliverable() {
        return deliverable;
    }
}
