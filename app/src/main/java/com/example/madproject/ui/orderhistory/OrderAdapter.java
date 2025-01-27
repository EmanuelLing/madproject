package com.example.madproject.ui.orderhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madproject.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final List<OrderModel> orderList;
    private final Context context;

    public OrderAdapter(List<OrderModel> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        // Bind order data to the views
        holder.orderId.setText("Order ID: " + order.getOrderId());
        holder.customerId.setText("Customer ID: " + order.getCustomerId());
        holder.totalPrice.setText(String.format("Total Price: RM %.2f", order.getTotalPrice()));
        holder.status.setText("Status: " + order.getStatus());
        holder.orderDate.setText("Date: " + order.getDate());

        // Show delete button for completed orders
        if ("completed".equals(order.getStatus())) {
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }

        // Delete button functionality
        holder.deleteButton.setOnClickListener(v -> {
            orderList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, orderList.size());
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // ViewHolder class to hold item views
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, customerId, totalPrice, status, orderDate;
        Button deleteButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            customerId = itemView.findViewById(R.id.customerId);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            status = itemView.findViewById(R.id.status);
            orderDate = itemView.findViewById(R.id.orderDate);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}