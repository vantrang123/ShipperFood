package com.trangdv.shipperfood.service;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.trangdv.shipperfood.common.Common;
import com.trangdv.shipperfood.retrofit.IAnNgonAPI;
import com.trangdv.shipperfood.retrofit.RetrofitClient;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MyFirebaseIdService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseIdService";
    IAnNgonAPI anNgonAPI;
    CompositeDisposable compositeDisposable;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        anNgonAPI = RetrofitClient.getInstance(Common.API_ANNGON_ENDPOINT).create(IAnNgonAPI.class);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        String fbid = Paper.book().read(Common.REMENBER_FBID);
        String apiKey = Paper.book().read(Common.API_KEY_TAG);

        compositeDisposable.add(anNgonAPI.updateToken(apiKey,fbid,newToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tokenModel -> {

                        }
                        , throwable -> {
                            Toast.makeText(this, "[REFRESH TOKEN]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }
}
