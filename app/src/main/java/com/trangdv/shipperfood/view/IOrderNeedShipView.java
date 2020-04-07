package com.trangdv.shipperfood.view;

import com.trangdv.shipperfood.model.Order;
import com.trangdv.shipperfood.model.ShippingOrder;

import java.util.List;

public interface IOrderNeedShipView {
    void onOrderNeedShipSuccess(List<Order> orderList);
    void onShippingOrderError(String message);
    void onShippingOrderSuccess(List<ShippingOrder> shippingOrderList);
}
