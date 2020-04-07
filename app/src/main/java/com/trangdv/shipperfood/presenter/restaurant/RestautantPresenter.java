package com.trangdv.shipperfood.presenter.restaurant;

import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.retrofit.IAnNgonAPI;
import com.trangdv.shipperfood.view.IRestaurantView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RestautantPresenter implements IRestaurantPresenter{
    IRestaurantView iRestaurantView;
    IAnNgonAPI anNgonAPI;
    CompositeDisposable compositeDisposable;

    public RestautantPresenter(IRestaurantView iRestaurantView, IAnNgonAPI anNgonAPI, CompositeDisposable compositeDisposable) {
        this.iRestaurantView = iRestaurantView;
        this.anNgonAPI = anNgonAPI;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void getRestaurant(double latitude, double longitude, int distance) {
        compositeDisposable.add(
                anNgonAPI.getNearbyRestaurant(Common.API_KEY, latitude, longitude, distance)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(restaurantModel -> {
                                    if (restaurantModel.isSuccess()) {
                                        iRestaurantView.onRestaurantSuccess(restaurantModel.getResult());
                                    } else {
                                        iRestaurantView.onRestaurantError(restaurantModel.getMessage());
                                    }
                                },
                                throwable -> {
                                    iRestaurantView.onRestaurantError(throwable.getMessage());
                                })
        );
    }


}
