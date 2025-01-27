package com.example.madproject.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.madproject.R;
import com.example.madproject.ui.ordercart.AddToCart;
import com.example.madproject.ui.ordercart.CartItem;
import com.example.madproject.ui.ordercart.CartManager;

public class FoodDrink_Detail extends AppCompatActivity {

    private int quantity = 1; // Default quantity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activty_food_drink_detail);

        // Retrieve the data from the Intent
        Intent intent = getIntent();
        boolean isFood = intent.getBooleanExtra("isFood", true);
        String userId = intent.getStringExtra("userId"); // Get the userId

        int itemId = intent.getIntExtra("itemId", -1);
        String itemName = intent.getStringExtra("itemName");
        double itemPrice = intent.getDoubleExtra("itemPrice", 0.0);
        String itemDescription = intent.getStringExtra("itemDescription");
        String imageURL = intent.getStringExtra("imageURL");

        // Debugging: Check retrieved data
        if (itemName == null || itemDescription == null || imageURL == null) {
            Toast.makeText(this, "Data retrieval failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bind UI elements
        TextView nameTextView = findViewById(R.id.tv_itemName);
        TextView priceTextView = findViewById(R.id.tv_price);
        TextView descriptionTextView = findViewById(R.id.tv_itemDescription);
        ImageView imageView = findViewById(R.id.iv_itemimage);
        ImageButton ibtn_add = findViewById(R.id.imagebtn_add_to_cart);
        ImageButton ibtn_minus = findViewById(R.id.ibtn_minus);
        TextView tv_quantity = findViewById(R.id.tv_quantity);
        Button btn_addorder = findViewById(R.id.btn_addOrder);

        // Set data
        nameTextView.setText(itemName);
        priceTextView.setText(String.format("RM %.2f", itemPrice));
        descriptionTextView.setText(itemDescription);
        Glide.with(this)
                .load(imageURL)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);

        // Handle quantity adjustment
        ibtn_add.setOnClickListener(v -> {
            quantity++;
            tv_quantity.setText(String.valueOf(quantity));
        });

        ibtn_minus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tv_quantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
            }
        });

        btn_addorder.setOnClickListener(v -> {
            // Add the selected item to the cart
            CartManager.getInstance().addItem(new CartItem(itemId, itemName, itemDescription, itemPrice, imageURL, quantity));

            // Show a confirmation message
            String itemType = isFood ? "Food" : "Drink";
            Toast.makeText(this, itemType + " \"" + itemName + "\" added to cart", Toast.LENGTH_SHORT).show();

            // Navigate to add_to_cart activity
            Intent addtocart = new Intent(FoodDrink_Detail.this, AddToCart.class);
            addtocart.putExtra("userId",userId);
            startActivity(addtocart);
        });

    }

}
