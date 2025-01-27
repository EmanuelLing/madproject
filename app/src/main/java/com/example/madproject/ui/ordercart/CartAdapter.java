package com.example.madproject.ui.ordercart;

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

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final OnQuantityChangeListener quantityChangeListener;

    public CartAdapter(List<CartItem> cartItems, OnQuantityChangeListener quantityChangeListener) {
        this.cartItems = cartItems;
        this.quantityChangeListener = quantityChangeListener;
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        holder.itemPrice.setText(String.format("RM %.2f", item.getPrice() * item.getQuantity()));

        // Load item image using Glide
        Glide.with(holder.itemView.getContext())
                .load(item.getImageURL())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.itemImage);

        // Increment quantity
        holder.incrementButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);

            // Notify the parent activity to update totals
            if (quantityChangeListener != null) {
                quantityChangeListener.onQuantityChanged();
            }
        });

        // Decrement quantity
        holder.decrementButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                // Decrease quantity and update UI
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
            } else {
                // Remove the item when quantity reaches 0
                CartManager.getInstance().removeItem(item); // Remove from CartManager
                cartItems.remove(position); // Remove from local list
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
            }

            // Notify the parent activity to update totals
            if (quantityChangeListener != null) {
                quantityChangeListener.onQuantityChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemQuantity, itemPrice;
        ImageButton incrementButton, decrementButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.cart_item_image);
            itemName = itemView.findViewById(R.id.cart_item_name);
            itemQuantity = itemView.findViewById(R.id.cart_item_quantity);
            itemPrice = itemView.findViewById(R.id.cart_item_price);
            incrementButton = itemView.findViewById(R.id.increment_button);
            decrementButton = itemView.findViewById(R.id.decrement_button);
        }
    }
}

