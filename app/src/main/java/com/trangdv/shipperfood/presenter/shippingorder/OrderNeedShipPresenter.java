package com.trangdv.shipperfood.presenter.shippingorder;

import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.retrofit.IAnNgonAPI;
import com.trangdv.shipperfood.view.IOrderNeedShipView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class OrderNeedShipPresenter implements IOrderNeedShipPresenter {
    IOrderNeedShipView iShippingOrderView;
    IAnNgonAPI anNgonAPI;
    CompositeDisposable compositeDisposable;

    public OrderNeedShipPresenter(IOrderNeedShipView iShippingOrderView, IAnNgonAPI anNgonAPI, CompositeDisposable compositeDisposable) {
        this.iShippingOrderView = iShippingOrderView;
        this.anNgonAPI = anNgonAPI;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void getOrderNeedShip(int restaurantId) {
        compositeDisposable.add(
                anNgonAPI.getOrderNeedShip(Common.API_KEY, restaurantId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(orderModel -> {
                                    if (orderModel.isSuccess()) {
                                        iShippingOrderView.onOrderNeedShipSuccess(orderModel.getResult());
                                    } else {
                                        iShippingOrderView.onShippingOrderError(orderModel.getMessage());
                                    }
                                },
                                throwable -> {
                                    iShippingOrderView.onShippingOrderError(throwable.getMessage());
                                })
        );
    }

    @Override
    public void getShippingOrder(int restaurantId, String shipperId) {
        compositeDisposable.add(
                anNgonAPI.getShippingOrder(Common.API_KEY, restaurantId, Common.currentShipper.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(shippingOrderModel -> {
                                    if (shippingOrderModel.isSuccess()) {
                                        iShippingOrderView.onShippingOrderSuccess(shippingOrderModel.getResult());
                                    } else {
                                        iShippingOrderView.onShippingOrderError(shippingOrderModel.getMessage());
                                    }
                                },
                                throwable -> {
                                    iShippingOrderView.onShippingOrderError(throwable.getMessage());
                                })
        );
    }
}
