package com.trangdv.shipperfood.view;

import com.trangdv.shipperfood.model.Restaurant;
import com.trangdv.shipperfood.model.ShippingOrder;

import java.util.List;

public interface IRestaurantView {
    void onRestaurantSuccess(List<Restaurant> restaurantList);
    void onRestaurantError(String message);
}
