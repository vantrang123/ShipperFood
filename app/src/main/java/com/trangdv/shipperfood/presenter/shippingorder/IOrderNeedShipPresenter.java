package com.trangdv.shipperfood.presenter.shippingorder;

public interface IOrderNeedShipPresenter {
    void getOrderNeedShip(int restaurantId);
    void getShippingOrder(int restaurantId, String shipperId);
}
