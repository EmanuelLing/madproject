package com.example.madproject.ui.ordercart;

public class CartItem {
    private int id;
    private String name;
    private String description;
    private double price;
    private String imageURL;
    private int quantity;

    public CartItem(int id, String name, String description, double price, String imageURL, int quantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageURL = imageURL;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}