package com.trangdv.shipperfood.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NeedShippAdapter extends RecyclerView.Adapter<NeedShippAdapter.ViewHolder> {
    private Context context;
    private List<Order> orderList = new ArrayList<>();
    private ItemListener itemListener;

    public NeedShippAdapter(Context context, List<Order> orders, ItemListener itemListener) {
        this.context = context;
        this.orderList = orders;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_order, parent, false);
        return new NeedShippAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        bindView(holder, position);
    }

    private void bindView(ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderId.setText(new StringBuilder("#").append(order.getOrderId()));
//        holder.tvOrderNumOfItem.setText(new StringBuilder(String.valueOf(orderList.get(position).getNumOfItem())));
        holder.tvOrderAddres.setText(new StringBuilder(order.getOrderAddress()));
        holder.tvOrderPhone.setText(new StringBuilder(order.getOrderPhone()));
        holder.tvOrderPrice.setText(new StringBuilder(String.valueOf(order.getTotalPrice())));
        holder.tvOrerDate.setText(order.getOrderDate());
        holder.tvOrderNumOfItem.setText(new StringBuilder(String.valueOf(order.getNumOfItem())));
        holder.tvOrderStatus.setText(Common.convertCodeToStatus(order.getOrderStatus()));

        if (order.isCod()) {
            holder.tvOrderCod.setText(new StringBuilder("Cash On Delivery"));
        } else {
            holder.tvOrderCod.setText(new StringBuilder("TransID: ").append(order.getTransactionId()));
        }

        holder.tvOrderAddres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent;
                if (!TextUtils.isEmpty(order.getLat())) {
                    mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+order.getLat()+","+order.getLng()));

                }
                else {
                    Uri gmmIntentUri;
                    try {
                        String[] split = order.getOrderAddress().split(",");
                        String house_number = split[0];
                        String street_names = split[1];
                        String city = split[2];
                        gmmIntentUri = Uri.parse("google.navigation:q="+house_number+street_names+city);
                    } catch (Exception e) {
                        gmmIntentUri = Uri.parse("google.navigation:q="+order.getOrderAddress());
                    }

                    mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                }

                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
            }
        });

        holder.tvOrderDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.currentOrder = order;
                itemListener.dispatchToOrderDetail(position);
            }
        });

        holder.tvInfoRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.openRestaurantDetail();
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvOrderId, tvOrderStatus, tvOrderPhone, tvOrderAddres, tvOrderCod,
                tvOrerDate, tvOrderPrice, tvOrderNumOfItem, tvInfoRestaurant, tvOrderDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderAddres = itemView.findViewById(R.id.tv_order_address);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderPhone = itemView.findViewById(R.id.tv_order_phone);
            tvOrderCod = itemView.findViewById(R.id.tv_order_cod);
            tvOrerDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderPrice = itemView.findViewById(R.id.tv_order_price);
            tvOrderNumOfItem = itemView.findViewById(R.id.tv_order_num_of_item);
            tvInfoRestaurant = itemView.findViewById(R.id.tv_info_restaurnt);
            tvOrderDetail = itemView.findViewById(R.id.tv_order_detail);
        }
    }

    public interface ItemListener {
        void dispatchToOrderDetail(int position);
        void openRestaurantDetail();
    }
}
