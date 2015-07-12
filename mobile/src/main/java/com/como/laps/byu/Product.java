package com.como.laps.byu;

/**
 * Created by Antonello on 11/07/15.
 */
public class Product {

    private String name;
    private String photoUrl;
    private float price;
    private boolean deliverable;

    public Product(String name, String photoUrl, float price, boolean deliverable) {
        this.name = name;
        this.photoUrl = photoUrl;
        this.price = price;
        this.deliverable = deliverable;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public float getPrice() {
        return price;
    }

    public boolean isDeliverable() {
        return deliverable;
    }
}
