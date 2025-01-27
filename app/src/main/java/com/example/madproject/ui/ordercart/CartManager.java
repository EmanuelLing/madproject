package com.example.madproject.ui.ordercart;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    // Singleton instance
    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Add item to the cart
    public void addItem(CartItem item) {
        cartItems.add(item);
    }

    // Retrieve all items in the cart
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    // Clear the cart
    public void clearCart() {
        cartItems.clear();
    }

    public void removeItem(CartItem item) {
        cartItems.remove(item);
    }

}