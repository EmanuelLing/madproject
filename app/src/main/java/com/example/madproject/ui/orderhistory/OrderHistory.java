package com.example.madproject.ui.orderhistory;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madproject.DatabaseHelper;
import com.example.madproject.R;
import com.example.madproject.ui.ordercart.AddToCart;
import com.example.madproject.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderModel> orderList = new ArrayList<>();
    private Handler handler = new Handler();
    private static final String CHANNEL_ID = "OrderStatusChannel";
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize RecyclerView
        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch orders from the database
        fetchOrdersFromDatabase();

        // Initialize adapter and set it to RecyclerView
        orderAdapter = new OrderAdapter(orderList, this);
        orderRecyclerView.setAdapter(orderAdapter);

        // Setup Bottom Navigation
        //setupBottomNavigation();
    }

    /*private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_orderhistory);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(OrderHistory.this, MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.navigation_ordercart:
                    startActivity(new Intent(OrderHistory.this, AddToCart.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.navigation_orderhistory:
                    return true; // Current page
            }
            return false;
        });
    }*/

    private void fetchOrdersFromDatabase() {
        Cursor cursor = dbHelper.getAllOrder();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int orderId = cursor.getInt(cursor.getColumnIndex("order_id"));
                String custId = cursor.getString(cursor.getColumnIndex("cust_id"));
                String totalPrice = cursor.getString(cursor.getColumnIndex("total_price"));
                String status = cursor.getString(cursor.getColumnIndex("status"));
                String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                String date = cursor.getString(cursor.getColumnIndex("date"));

                // Create an OrderModel object
                OrderModel order = new OrderModel(orderId, custId, Double.parseDouble(totalPrice), status, date, quantity);

                // Add the order at the beginning of the list
                orderList.add(0, order);

                // Schedule a status change for orders that are not yet completed
                if (!"completed".equals(status)) {
                    changeOrderStatusAfterDelay(order);
                }
            } while (cursor.moveToNext());

            cursor.close(); // Close the cursor to avoid memory leaks
        }
    }

    private void changeOrderStatusAfterDelay(OrderModel order) {
        handler.postDelayed(() -> {
            // Update the order status to "completed" in the local object
            order.setStatus("completed");

            // Notify the adapter to refresh the UI
            orderAdapter.notifyDataSetChanged();

            // Update the status in the database
            boolean isUpdated = dbHelper.updateOrderStatus(order.getOrderId(), "completed");
            if (!isUpdated) {
                // Handle database update failure if necessary
                System.err.println("Failed to update order status in the database.");
            }

            // Show a notification
            showNotification();
        }, 7000); // 7 seconds delay
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Order Status Channel";
            String description = "Channel for order status notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }

        // Build and display the notification
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Order Status")
                    .setContentText("Your order is ready!")
                    .setSmallIcon(android.R.drawable.ic_notification_overlay)
                    .build();
        }

        notificationManager.notify(0, notification);
    }
}
