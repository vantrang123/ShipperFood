package com.trangdv.shipperfood.view;

import com.trangdv.shipperfood.model.Shipper;

public interface ILoginView {
    void onLoginSuccess(Shipper user);
    void onLoginError(String message);
}
