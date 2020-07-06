package com.trangdv.shipperfood.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.model.Order;
import com.trangdv.shipperfood.ui.dialog.RestaurantDetailDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 1;
    private static final int VIEW_TYPE_ITEM = 0;
    private LayoutInflater mInflater;
    private List<Order> orderList = new ArrayList<>();
    Context context;
    ItemListener listener;

    public OrderAdapter(Context context, List<Order> requests, ItemListener itemListener) {
        super();
        this.context = context;
        this.orderList = requests;
        listener = itemListener;
    }

    public void addItem(List<Order> addedItems) {
        int startInsertedIndex = orderList.size();
        orderList.addAll(addedItems);
        notifyItemInserted(startInsertedIndex);
    }

    public void addNull() {
        orderList.add(null);
        notifyItemInserted(orderList.size() - 1);
    }

    public void removeNull() {
        orderList.remove(orderList.size() - 1);
        notifyItemRemoved(orderList.size());
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    @Override
    public int getItemViewType(int position) {
        if (orderList.get(position) == null) {
            return VIEW_TYPE_LOADING;
        } else return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        mInflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ITEM) {
            itemView = mInflater.inflate(R.layout.item_order, parent, false);
            return new ViewHolder(itemView);
        } else {
            itemView = mInflater.inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            Order order = orderList.get(position);
            viewHolder.tvOrderNumOfItem.setText(new StringBuilder(String.valueOf(order.getNumOfItem())));
            viewHolder.tvOrderAddres.setText(new StringBuilder(order.getOrderAddress()));
            viewHolder.tvOrderPhone.setText(new StringBuilder(order.getOrderPhone()));
            viewHolder.tvOrderPrice.setText(new StringBuilder(String.valueOf(order.getTotalPrice())));
            viewHolder.tvOrerDate.setText(order.getOrderDate());
            viewHolder.tvOrderId.setText(new StringBuilder("#").append((order.getOrderId())));
            viewHolder.tvOrderStatus.setText(Common.convertCodeToStatus(order.getOrderStatus()));

            viewHolder.tvOrderPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callPhone(order.getOrderPhone());
                }
            });

            viewHolder.tvOrderAddres.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+order.getLat()+","+order.getLng()));
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(mapIntent);
                    }
                }
            });

            viewHolder.tvOrderDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.currentOrder = orderList.get(position);
                    listener.dispatchToOrderDetail(position);
                }
            });

            viewHolder.tvInfoRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.openRestaurantDetail();
                }
            });

            if (orderList.get(position).isCod()) {
                viewHolder.tvOrderCod.setText(new StringBuilder("Cash On Delivery"));
            } else {
                viewHolder.tvOrderCod.setText(new StringBuilder("TransID: ").append(orderList.get(position).getTransactionId()));
            }
        } else if (holder instanceof LoadingHolder) {
            LoadingHolder loadingHolder = (LoadingHolder) holder;
            loadingHolder.progressBar.setIndeterminate(true);
        }
    }

    private void callPhone(String orderPhone) {
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(orderPhone))));
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

    public class LoadingHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }

    public interface ItemListener {
        void dispatchToOrderDetail(int position);
        void openRestaurantDetail();
    }
}
