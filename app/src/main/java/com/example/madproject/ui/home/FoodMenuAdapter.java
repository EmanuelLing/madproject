package com.example.madproject.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.madproject.R;

import java.util.ArrayList;
import java.util.List;

public class FoodMenuAdapter extends RecyclerView.Adapter<FoodMenuAdapter.ViewHolder> {

    private final List<Object> menuList = new ArrayList<>();
    private final String userId; // Store the userId

    public FoodMenuAdapter(String userId) {
        this.userId = userId;
    }

    public void updateData(List<MenuFoodModel> newFoodList, List<MenuDrinkModel> newDrinkList) {
        menuList.clear();
        if (newFoodList != null) {
            menuList.addAll(newFoodList);
        }
        if (newDrinkList != null) {
            menuList.addAll(newDrinkList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = menuList.get(position);

        if (item instanceof MenuFoodModel) {
            MenuFoodModel food = (MenuFoodModel) item;
            bindData(holder, food.getFoodName(), food.getFoodPrice(), food.getImageURL(), v -> {
                Intent intent = new Intent(v.getContext(), FoodDrink_Detail.class);
                intent.putExtra("isFood", true);
                intent.putExtra("itemId", food.getFoodID());
                intent.putExtra("itemName", food.getFoodName());
                intent.putExtra("itemPrice", food.getFoodPrice());
                intent.putExtra("itemDescription", food.getFoodDescription());
                intent.putExtra("imageURL", food.getImageURL());
                intent.putExtra("userId", userId); // Pass the userId
                v.getContext().startActivity(intent);
            });
        } else if (item instanceof MenuDrinkModel) {
            MenuDrinkModel drink = (MenuDrinkModel) item;
            bindData(holder, drink.getDrinkName(), drink.getDrinkPrice(), drink.getImageURL(), v -> {
                Intent intent = new Intent(v.getContext(), FoodDrink_Detail.class);
                intent.putExtra("isFood", false);
                intent.putExtra("itemId", drink.getDrinkID());
                intent.putExtra("itemName", drink.getDrinkName());
                intent.putExtra("itemPrice", drink.getDrinkPrice());
                intent.putExtra("itemDescription", drink.getDrinkDescription());
                intent.putExtra("imageURL", drink.getImageURL());
                intent.putExtra("userId", userId); // Pass the userId
                v.getContext().startActivity(intent);
            });
        }
    }


    private void bindData(ViewHolder holder, String name, double price, String imageURL, View.OnClickListener clickListener) {
        holder.tv_item_name.setText(name);
        holder.tv_item_price.setText(String.format("RM %.2f", price));
        Glide.with(holder.itemView.getContext())
                .load(imageURL)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.iv_item_image);
        holder.imagebtn_add_to_cart.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_item_image;
        TextView tv_item_name, tv_item_price;
        ImageButton imagebtn_add_to_cart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_item_image = itemView.findViewById(R.id.iv_item_image);
            tv_item_name = itemView.findViewById(R.id.tv_item_name);
            tv_item_price = itemView.findViewById(R.id.tv_item_price);
            imagebtn_add_to_cart = itemView.findViewById(R.id.imagebtn_add_to_cart);
        }
    }
}

