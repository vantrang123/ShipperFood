package com.trangdv.shipperfood.view;

import com.trangdv.shipperfood.model.OrderDetail;

import java.util.List;

public interface IOrderItemView {
    void onAllOrderSuccess(List<OrderDetail> orderDetailList);
    void onAllOrderError(String message);
}
