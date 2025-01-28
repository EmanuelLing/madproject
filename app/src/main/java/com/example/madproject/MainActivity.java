package com.example.madproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.madproject.ui.home.FoodMenuAdapter;
import com.example.madproject.ui.home.FoodMenuResponse;
import com.example.madproject.ui.home.MenuDrinkModel;
import com.example.madproject.ui.home.MenuFoodModel;
import com.example.madproject.ui.ordercart.AddToCart;
import com.example.madproject.ui.orderhistory.OrderHistory;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madproject.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private FoodMenuAdapter adapter;
    private RecyclerView rvMyCardItem;
    private Spinner itemTypeSpinner;

    // Lists to hold all data
    private List<MenuFoodModel> allFoodItems = new ArrayList<>();
    private List<MenuDrinkModel> allDrinkItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Get userId and fullName from intent
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String fullName = intent.getStringExtra("fullName");

        TextView username = findViewById(R.id.tv_username);
        username.setText("Welcome Back, " + fullName);

        // Initialize the Spinner
        itemTypeSpinner = findViewById(R.id.item_type);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.items_type,
                android.R.layout.simple_spinner_item
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemTypeSpinner.setAdapter(spinnerAdapter);

        // Initialize RecyclerView and Adapter
        rvMyCardItem = findViewById(R.id.rv_foodMenu);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMyCardItem.setLayoutManager(gridLayoutManager);

        adapter = new FoodMenuAdapter(userId); // Pass the userId to the adapter
        rvMyCardItem.setAdapter(adapter);

        // Fetch menus and setup spinner
        fetchMenus();
        setupSpinnerListener();
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Highlight the current menu item
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        // Set listener for menu item selection
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_orderhistory) {
                startActivity(new Intent(MainActivity.this, OrderHistory.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_ordercart) {
                startActivity(new Intent(MainActivity.this, AddToCart.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.navigation_home) {
                // Do nothing as it's the current page
                return true;
            }
            return false;
        });
    }


    private void fetchMenus() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hushed-charming-clipper.glitch.me/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FoodMenuApi api = retrofit.create(FoodMenuApi.class);

        // Fetch food menu
        api.getFoodMenu().enqueue(new Callback<FoodMenuResponse>() {
            @Override
            public void onResponse(Call<FoodMenuResponse> call, Response<FoodMenuResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allFoodItems = response.body().getFoodMenu();

                    // Fetch drink menu after food menu
                    fetchDrinkMenu();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch food menu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FoodMenuResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchDrinkMenu() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://hushed-charming-clipper.glitch.me/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FoodMenuApi api = retrofit.create(FoodMenuApi.class);

        // Fetch drink menu
        api.getDrinkMenu().enqueue(new Callback<FoodMenuResponse>() {
            @Override
            public void onResponse(Call<FoodMenuResponse> call, Response<FoodMenuResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allDrinkItems = response.body().getDrinkMenu();

                    // Initially display all items
                    adapter.updateData(allFoodItems, allDrinkItems);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch drink menu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FoodMenuResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinnerListener() {
        itemTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();

                // Filter based on the selected type
                switch (selectedType) {
                    case "All":
                        adapter.updateData(allFoodItems, allDrinkItems);
                        break;
                    case "Food":
                        adapter.updateData(allFoodItems, null); // Display only food
                        break;
                    case "Drinks":
                        adapter.updateData(null, allDrinkItems); // Display only drinks
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default to "All"
                adapter.updateData(allFoodItems, allDrinkItems);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Check if login status is false
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            editor.clear();
        } else {
        }

        editor.apply();
    }
}