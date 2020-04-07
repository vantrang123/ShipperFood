package com.trangdv.shipperfood.presenter.login;

import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.retrofit.IAnNgonAPI;
import com.trangdv.shipperfood.view.ILoginView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class LoginPresenter implements ILoginPresenter{
    ILoginView iLoginView;
    IAnNgonAPI anNgonAPI;
    CompositeDisposable compositeDisposable;

    public LoginPresenter(ILoginView iLoginView, IAnNgonAPI anNgonAPI, CompositeDisposable compositeDisposable) {
        this.iLoginView = iLoginView;
        this.anNgonAPI = anNgonAPI;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void onLogin(String phoneNumber, String password) {
        compositeDisposable.add(
                anNgonAPI.getShipper(Common.API_KEY,
                        phoneNumber,
                        password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(shipperModel -> {
                                    if (shipperModel.isSuccess()) {
                                        iLoginView.onLoginSuccess(shipperModel.getResult().get(0));
                                    } else {
                                        iLoginView.onLoginError(shipperModel.getMessage());
                                    }
                                },
                                throwable -> {
                                    iLoginView.onLoginError(throwable.getMessage());
                                }
                        ));
    }
}
