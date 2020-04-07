package com.trangdv.shipperfood.adapter;

import android.content.Context;
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
    private SimpleDateFormat simpleDateFormat;

    public NeedShippAdapter(Context context, List<Order> orders, ItemListener itemListener) {
        this.context = context;
        this.orderList = orders;
        this.itemListener = itemListener;
        simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
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
        holder.tvOrderId.setText(new StringBuilder("#").append(orderList.get(position).getOrderId()));
//        holder.tvOrderNumOfItem.setText(new StringBuilder(String.valueOf(orderList.get(position).getNumOfItem())));
        holder.tvOrderAddres.setText(new StringBuilder(orderList.get(position).getOrderAddress()));
        holder.tvOrderPhone.setText(new StringBuilder(orderList.get(position).getOrderPhone()));
        holder.tvOrderPrice.setText(new StringBuilder(String.valueOf(orderList.get(position).getTotalPrice())));
        holder.tvOrerDate.setText(new StringBuilder(simpleDateFormat.format(orderList.get(position).getOrderDate())));
        holder.tvOrderNumOfItem.setText(new StringBuilder(String.valueOf(orderList.get(position).getNumOfItem())));
        holder.tvOrderStatus.setText(Common.convertCodeToStatus(orderList.get(position).getOrderStatus()));

        if (orderList.get(position).isCod()) {
            holder.tvOrderCod.setText(new StringBuilder("Cash On Delivery"));
        } else {
            holder.tvOrderCod.setText(new StringBuilder("TransID: ").append(orderList.get(position).getTransactionId()));
        }

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvOrderId, tvOrderStatus, tvOrderPhone, tvOrderAddres, tvOrderCod, tvOrerDate, tvOrderPrice, tvOrderNumOfItem;

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.dispatchToOrderDetail(getAdapterPosition());
                }
            });
        }
    }

    public interface ItemListener {
        void dispatchToOrderDetail(int position);
    }
}
