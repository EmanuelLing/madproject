package com.example.madproject.ui.ordercart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madproject.DatabaseHelper;
import com.example.madproject.R;
import com.example.madproject.ui.orderhistory.OrderHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AddToCart extends AppCompatActivity {

    private RecyclerView cartView;
    private TextView totalFeeTxt, taxTxt, totalTxt, emptyTxt;
    private double taxPercentage = 0.06; // Example tax percentage (6%)
    private DatabaseHelper dbHelper;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Bind UI components
        cartView = findViewById(R.id.cartView);
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        taxTxt = findViewById(R.id.taxTxt);
        totalTxt = findViewById(R.id.totalTxt);
        emptyTxt = findViewById(R.id.emptyTxt);

        // Retrieve cart items from CartManager
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();

        if (cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCartWithItems(cartItems);
        }

        // Checkout button logic
        setupCheckoutButton(cartItems);
    }

    private void showEmptyCart() {
        emptyTxt.setVisibility(View.VISIBLE);
        cartView.setVisibility(View.GONE);
    }

    private void showCartWithItems(List<CartItem> cartItems) {
        emptyTxt.setVisibility(View.GONE);
        cartView.setVisibility(View.VISIBLE);

        // Set up RecyclerView with CartAdapter
        cartView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartItems, this::updateTotals);
        cartView.setAdapter(adapter);

        // Calculate and display totals initially
        updateTotals();
    }

    private void setupCheckoutButton(List<CartItem> cartItems) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = sdf.format(new Date());

        TextView checkoutButton = findViewById(R.id.Checkout);
        checkoutButton.setOnClickListener(v -> {
            String userId = getIntent().getStringExtra("userId");

            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Cart is empty. Please add items before checkout.", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalFee = 0;
            StringBuilder quantityBuilder = new StringBuilder();

            for (CartItem item : cartItems) {
                totalFee += item.getPrice() * item.getQuantity();
                quantityBuilder.append(item.getQuantity()).append("x ").append(item.getName()).append(", ");
            }

            double tax = totalFee * taxPercentage;
            double totalCost = totalFee + tax;

            boolean isInserted = dbHelper.insertOrder(
                    userId, // dynamic userId
                    String.format("%.2f", totalCost),
                    "pending",
                    quantityBuilder.toString().trim(),
                    currentDate
            );

            if (isInserted) {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();

                // Pass the order to OrderHistory via intent
                Intent intent = new Intent(AddToCart.this, OrderHistory.class);
                startActivity(intent);

                // Clear the cart
                CartManager.getInstance().clearCart();
                cartItems.clear();
                adapter.notifyDataSetChanged();
                showEmptyCart();
            } else {
                Toast.makeText(this, "Failed to place the order. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTotals() {
        double totalFee = 0;

        // Retrieve updated cart items from CartManager
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();

        for (CartItem item : cartItems) {
            totalFee += item.getPrice() * item.getQuantity();
        }

        double tax = totalFee * taxPercentage;
        double totalCost = totalFee + tax;

        totalFeeTxt.setText(String.format("RM %.2f", totalFee));
        taxTxt.setText(String.format("RM %.2f", tax));
        totalTxt.setText(String.format("RM %.2f", totalCost));

        if (cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            emptyTxt.setVisibility(View.GONE);
            cartView.setVisibility(View.VISIBLE);
        }
    }
}
